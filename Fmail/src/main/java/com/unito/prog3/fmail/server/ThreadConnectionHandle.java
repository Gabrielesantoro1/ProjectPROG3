package com.unito.prog3.fmail.server;

import com.unito.prog3.fmail.model.Email;
import com.unito.prog3.fmail.model.MailServer;
import com.unito.prog3.fmail.model.Mailbox;
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
        synchronized (server) {
            ObjectInputStream input;
            ObjectOutputStream output;
            Object in;
            try {
                input = new ObjectInputStream(socket.getInputStream());
                output = new ObjectOutputStream(socket.getOutputStream());
                try {
                    in = input.readObject();

                    //new client asked for connection
                    if (in instanceof String name) { //First connection Case
                        clientConnectionCase(output, name);
                    //new email sent
                    } else if (in instanceof Email email) {
                        sendMailCase(output, email);
                    //a client sent a request (update, delete_all/delete_single, close connection)
                    } else if (in instanceof ArrayList request) {
                        requestCase(output, request);
                    }
                } finally {
                    output.flush();
                    input.close();
                    output.close();
                    input.close();
                    socket.close();
                }
            } catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
        }
    }

    private void requestCase(ObjectOutputStream output, ArrayList<String> request) throws IOException {
        //update case
        String client_name = request.get(0);
        switch (request.get(1)) {
            case "update":
                if (server.existAccount(client_name)) {
                    Objects.requireNonNull(output).writeObject("true");
                    Objects.requireNonNull(output).writeObject(server.getMailboxes().get(server.getIndexByName(client_name)));
                } else {
                    output.writeObject("false");
                }
                break;

            case "delete_all":
                if (server.existAccount(client_name)) {
                    server.getMailboxes().get(server.getIndexByName(client_name)).clearMailDel();
                    server.clearDelEmail(client_name);
                    Objects.requireNonNull(output).writeObject("true");
                    Platform.runLater(() -> server.addLog(new Date() + ": Deleted mails of client " + client_name + " successfully cleared"));
                } else {
                    output.writeObject("false");
                }
                break;

            case "delete_single":
                if (server.existAccount(client_name)) {
                    int id = Integer.parseInt(request.get(2));

                    if (request.get(3).equals("rcvd")) {
                        Mailbox mailbox = server.getMailboxes().get(server.getIndexByName(client_name));
                        Objects.requireNonNull(output).writeObject("true");

                        mailbox.delete_email_rcvd(id);
                        server.deleteEmailRcvd(client_name, id);
                        Objects.requireNonNull(output).writeObject(server.getMailboxes().get(server.getIndexByName(client_name)));

                        Platform.runLater(() -> server.addLog(new Date() + ": Email " + id + " from client " + client_name + " was successfully deleted"));

                    }else if (request.get(3).equals("sent")){
                        Mailbox mailbox = server.getMailboxes().get(server.getIndexByName(client_name));
                        Objects.requireNonNull(output).writeObject("true");

                        mailbox.delete_email_sent(id);
                        server.deleteEmailSent(client_name, id);
                        Objects.requireNonNull(output).writeObject(server.getMailboxes().get(server.getIndexByName(client_name)));

                        Platform.runLater(() -> server.addLog(new Date() + ": Email " + id + " from client " + client_name + " was successfully deleted"));
                    }
                }else{
                    output.writeObject("false");
                }
                break;

            case "close_connection":
                if (server.existAccount(client_name)) {
                    Objects.requireNonNull(output).writeObject("true");
                    Platform.runLater(() -> server.addLog(new Date() + ": Client " + client_name + " closed the connection with the server"));
                    Platform.runLater(server::decrClient);
                } else {
                    output.writeObject("false");
                }
                break;
            default:
                output.writeObject("false");
                break;
        }
    }

    private void sendMailCase(ObjectOutputStream output, Email email) throws IOException {
        String recipients_text = email.getTo();
        String[] recipients = recipients_text.split(" ");
        ArrayList<String> fails = new ArrayList<>();
        for (String recipient : recipients) {
            if (!server.existAccount(recipient)) {
                fails.add(recipient);
                recipients_text = recipients_text.replace(recipient, "");
                System.out.println(recipients_text);
            }
        }
        email.setTo(recipients_text);
        System.out.println(email.getTo());
        for (String s2 : recipients) {
            if (!server.saveEmail(email, s2)) {
                Platform.runLater(() -> server.addLog(new Date() + ": Error occurred on saving email" + email.getId() + " from " + email.getFrom() + " to " + s2 + " because the recipient doesn't exists"));
            }else {
                Platform.runLater(() -> server.addLog(new Date() + ": Email from " + email.getFrom() + " to " + s2 + " sent successfully"));
            }
        }if(fails.isEmpty()) {
            Objects.requireNonNull(output).writeObject("true");
        }else{
            Objects.requireNonNull(output).writeObject("false");
            Objects.requireNonNull(output).writeObject(fails);
        }
    }

    private void clientConnectionCase(ObjectOutputStream output, String name) throws IOException {
        if (server.existAccount(name)) {
            Objects.requireNonNull(output).writeObject("true");
            Platform.runLater(() -> server.addLog(new Date() + ": Client " + name + " is now connected"));
            Platform.runLater(server::incrClient);
            Objects.requireNonNull(output).writeObject(server.getMailboxes().get(server.getIndexByName(name)));
        } else {
            output.writeObject("false");
            Platform.runLater(() -> server.addLog(new Date() + ":Unknown client " + name + " tried to connect unsuccessfully"));
        }
    }

}



