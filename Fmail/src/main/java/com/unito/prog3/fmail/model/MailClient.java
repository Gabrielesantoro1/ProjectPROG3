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
        }catch (UnknownHostException e){e.printStackTrace();}
        this.mailbox = mailbox;
    }

    public Boolean getConnection() {
        boolean connection_established = false;
        ObjectOutputStream out = null;
        ObjectInputStream input = null;
        try {
            Socket client_socket = new Socket(this.local, Support.port);
            //System.out.println("C1");
            try {
                out = new ObjectOutputStream(client_socket.getOutputStream());
                input = new ObjectInputStream(client_socket.getInputStream());
                //System.out.println("C2");

                Objects.requireNonNull(out).writeObject(this.mailbox.getAccount_name());
                //System.out.println("C3");

                Object output = input.readObject();
                if (output.equals("true"))
                    connection_established = true;
                System.out.println("Connection established: " + connection_established);
                //System.out.println("C5");
                return connection_established;

            }finally {client_socket.close(); out.close(); out.flush();}
        }catch (IOException | ClassNotFoundException e){e.printStackTrace();}
    return connection_established;
    }

    public Mailbox getMailbox() {return mailbox;}

    @Override
    public String toString() {
        return "\n      MailClient{" +
                ", mailbox=" + mailbox +
                "}";
    }

}

