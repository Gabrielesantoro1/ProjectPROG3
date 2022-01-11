package com.unito.prog3.fmail.model;

import com.unito.prog3.fmail.model.Email;
import com.unito.prog3.fmail.model.Mailbox;
import com.unito.prog3.fmail.support.Support;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

public class MailClient {
    private Mailbox mailbox;
    private InetAddress local;

    /**
     * {@code MailClient} Constructor
     * @param mailbox
     */
    public MailClient(Mailbox mailbox){
        try{
            local = InetAddress.getLocalHost();
        }catch (UnknownHostException e){
            e.printStackTrace();
        }
        this.mailbox = mailbox;
    }

    public void getConnection() {
        Socket client_socket = null;
        try {
            client_socket = new Socket(this.local, Support.port);
            System.out.println("C1");
            try {
                ObjectOutputStream out = new ObjectOutputStream(client_socket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(client_socket.getInputStream());
                System.out.println("C2");
                out.writeObject(this.getMailbox().getAccount_name());
                System.out.println("C3");
                Boolean done = false;
                while(!done) {
                    Boolean connection_established = (Boolean) input.readObject();
                    System.out.println("C4 " + input.readObject().toString());
                    System.out.println("Connection established: " + connection_established);
                    done = true;
                }
                System.out.println("C5");
            } finally {
                client_socket.close();
            }
        }catch (IOException | ClassNotFoundException e){
            e.getStackTrace();}
    }

    public Mailbox getMailbox() {return mailbox;}

    @Override
    public String toString() {
        return "\n      MailClient{" +
                ", mailbox=" + mailbox +
                "}";
    }

}

