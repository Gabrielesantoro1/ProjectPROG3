package com.unito.prog3.fmail.server;

import com.unito.prog3.fmail.model.Email;
import com.unito.prog3.fmail.model.MailClient;
import com.unito.prog3.fmail.model.MailServer;
import com.unito.prog3.fmail.support.Support;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public record ThreadConnectionHandle(MailServer server, Socket socket) implements Runnable {
    @Override
    public void run() {
        try {
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("ST1");
            Object in = null;
            while(true){
                System.out.println("ST1.1");
                in = input.readObject();//TODO:Dopo che prende il primo valore riprova dinuovo e vede che é chiusa
                if(in instanceof String name){
                    System.out.println("ST2");
                    if (server.existAccount(name)){
                        Objects.requireNonNull(output).writeObject("true");
                        System.out.println("Client " + name + " is now connected");
                    } else {
                        output.writeObject("false");
                        System.out.println("An unknown client tried to connect unsuccessfully");
                    }
                }
            }
        }catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

}



