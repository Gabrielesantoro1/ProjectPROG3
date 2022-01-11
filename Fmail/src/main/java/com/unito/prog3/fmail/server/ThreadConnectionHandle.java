package com.unito.prog3.fmail.server;

import com.unito.prog3.fmail.model.Email;
import com.unito.prog3.fmail.model.MailClient;
import com.unito.prog3.fmail.model.MailServer;
import com.unito.prog3.fmail.support.Support;

import java.io.*;
import java.net.Socket;

public record ThreadConnectionHandle(MailServer server, Socket socket) implements Runnable {
    @Override
    public void run() {
        try {
            System.out.println(socket.isBound());
            System.out.println(socket.getInetAddress());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("ST1");
            while(true){
                Object in = input.readObject();
                System.out.println("ST2 " + input.readObject().toString());
                if(in instanceof String name){
                    if (server.existAccount(name)){
                        System.out.println("Client " + name + " is now connected");
                        output.writeObject(true);
                    } else {
                        System.out.println("Client tried to connect unsuccessfully");
                        output.writeObject(false);
                    }
                }
            }
        }catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

}



