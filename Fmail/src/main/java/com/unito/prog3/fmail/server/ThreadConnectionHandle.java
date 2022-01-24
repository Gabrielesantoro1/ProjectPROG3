package com.unito.prog3.fmail.server;

import com.unito.prog3.fmail.model.Email;
import com.unito.prog3.fmail.model.MailServer;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public record ThreadConnectionHandle(MailServer server, Socket socket) implements Runnable {
    /**
     * The thread running this runnable is because it has received a request or any other interaction from the client. Then it reads the value it received and has several options available:
     * 1) The client may want to connect so it sent its name, in this case the server checks that the name is of a registered account and, if necessary, sends the client a value of confirmation and its mailbox.
     * 2) The client may have sent an email to want to send. In this case the server checks that the recipient to whom you want to send the email is from an existing account and then saves the email in files and in its memory, returning the confirmation value to the client or not.
     * 3) The client may want to perform a request which can be: updating their emails or permanently deleting their deleted emails. In both cases the server proceeds with the request and sends the client a confirmation value.
     */
    @Override
    public void run() {
        ObjectInputStream input;
        ObjectOutputStream output;
        Object in;
        try {
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
            try{
                in = input.readObject();
                if (in instanceof String name) { //First connection Case
                    if (server.existAccount(name)) {
                        Objects.requireNonNull(output).writeObject("true");
                        Platform.runLater(() -> server.addLog(new Date() + ": Client " + name + " is now connected"));
                        Objects.requireNonNull(output).writeObject(server.getMailboxes().get(server.getindexbyname(name)));
                    }
                    else {
                        output.writeObject("false");
                        Platform.runLater(() -> server.addLog(new Date() + ": Unknown client " + name + " tried to connect unsuccessfully"));
                    }
                }
                else if(in instanceof Email email){ //Sendmail Case
                    if(server.existAccount(email.getTo())){
                       if(server.saveEmail(email)) {
                           Objects.requireNonNull(output).writeObject("true");
                           Platform.runLater(() -> server.addLog(new Date() + ": Email from " + email.getFrom() + " to " + email.getTo() + " sent successfully"));
                       }else {
                           Objects.requireNonNull(output).writeObject("false");
                           Platform.runLater(() -> server.addLog(new Date() + ": Error occurred on saving email" + email.getId() +" from " + email.getFrom() + " to " + email.getTo()));
                       }
                    }
                    else{
                        Objects.requireNonNull(output).writeObject("false");
                        Platform.runLater(() -> server.addLog(new Date() + ": The recipient " + email.getTo() + " indicated by " + email.getFrom() + "does not exist"));
                    }
                }else if (in instanceof ArrayList request){ //Request Case
                    String client_name = (String) request.get(0);
                    if(request.get(1).equals("refresh")){ //Refresh request
                        if(server.existAccount(client_name)){ //Controllo non necessario, ma lo rende piú sicuro
                            Objects.requireNonNull(output).writeObject("true");
                            System.out.println("Request of refresh received successfully");

                            Objects.requireNonNull(output).writeObject(server.getMailboxes().get(server.getindexbyname(client_name)));
                            System.out.println("Mailbox sent to " + client_name + " successfully");
                        }else{
                            output.writeObject("false");
                            System.out.println("An unknown client tried the refresh request");
                        }
                    }
                    else if(request.get(1).equals("delete_all")){ //Permanent elimination request
                        if(server.existAccount(client_name)){
                            server.getMailboxes().get(server.getindexbyname(client_name)).clearMailDel();
                            Objects.requireNonNull(output).writeObject("true");
                            Platform.runLater(() -> server.addLog(new Date() + "Deleted mails of client " + client_name + " successfully cleared"));
                        }else{
                            output.writeObject("false");
                            System.out.println("An unknown client tried the delete request");
                        }
                    }
                }
            }finally {output.flush();input.close();output.close();input.close();socket.close();}
        } catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
    }

}



