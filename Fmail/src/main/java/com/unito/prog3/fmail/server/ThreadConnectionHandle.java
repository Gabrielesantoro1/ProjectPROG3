package com.unito.prog3.fmail.server;

import com.unito.prog3.fmail.model.Email;
import com.unito.prog3.fmail.model.MailClient;
import com.unito.prog3.fmail.model.MailServer;
import com.unito.prog3.fmail.support.Support;

import java.io.*;
import java.net.Socket;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;

public record ThreadConnectionHandle(MailServer server, Socket socket) implements Runnable {

    @Override
    public void run() {
        ObjectInputStream input = null;
        ObjectOutputStream output = null;
        try {
            try{
                input = new ObjectInputStream(socket.getInputStream());
                output = new ObjectOutputStream(socket.getOutputStream());
                Object in;
                in = input.readObject();
                if (in instanceof String name) {
                    if (server.existAccount(name)) {
                        Objects.requireNonNull(output).writeObject("true");
                        System.out.println("Client " + name + " is now connected");
                        Objects.requireNonNull(output).writeObject(server.getMailboxes().get(server.getindexbyname(name)));
                    } else {
                        output.writeObject("false");
                        System.out.println("An unknown client tried to connect unsuccessfully");
                    }
                }else if(in instanceof Email email){
                    if(server.existAccount(email.getTo())){
                       if(server.saveEmail(email)) {
                           Objects.requireNonNull(output).writeObject("true");
                           System.out.println("Email sent successfully");
                       }else {
                           Objects.requireNonNull(output).writeObject("false");
                           System.out.println("An error occurs on saving email");
                       }
                    }else{
                        Objects.requireNonNull(output).writeObject("false");
                        System.out.println("The recipient doesn't exist");
                    }
                }
            }finally {output.flush();input.close();output.close();input.close();socket.close();
                System.out.println("Connection closed");}
        } catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
    }

}



