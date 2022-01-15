package com.unito.prog3.fmail.model;

import com.unito.prog3.fmail.support.Support;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
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
        ObjectInputStream in = null;
        try {
            Socket client_socket = new Socket(this.local, Support.port);

            try {
                out = new ObjectOutputStream(client_socket.getOutputStream());
                in = new ObjectInputStream(client_socket.getInputStream());

                Objects.requireNonNull(out).writeObject(this.mailbox.getAccount_name());

                String input = (String) in.readObject();
                if (input.equals("true"))
                    connection_established = true;

                this.mailbox = (Mailbox) in.readObject();
                System.out.println(mailbox.toString()); //STAMPA TEST
                return connection_established;

            }finally {out.flush();in.close();out.close();client_socket.close();}
        }catch (IOException | ClassNotFoundException e){e.printStackTrace();}
    return connection_established;
    }

    public boolean sendEmail(Email email) {
        boolean saved = false;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try{
            Socket client_socket = new Socket(this.local, Support.port);
            try {
                out = new ObjectOutputStream(client_socket.getOutputStream());
                in = new ObjectInputStream(client_socket.getInputStream());

                Objects.requireNonNull(out).writeObject(email);

                String input = (String) in.readObject();
                if(input.equals("true")){
                    this.mailbox.setMail_sent(email);
                    saved = true;
                }
            } finally {out.flush();in.close();out.close();client_socket.close();}
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return saved;
    }

    public boolean refresh_listEmail(){
        boolean getted = false;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try{
            Socket client_socket = new Socket(this.local, Support.port);
            try{
                out = new ObjectOutputStream(client_socket.getOutputStream());
                in = new ObjectInputStream(client_socket.getInputStream());

                ArrayList<String> what_send = new ArrayList<>();
                what_send.add(this.mailbox.getAccount_name());

                Objects.requireNonNull(out).writeObject(what_send);

                String input = (String) in.readObject();
                if(input.equals("true")){
                    this.mailbox = (Mailbox) in.readObject();
                    getted = true;
                }
                return getted;
            }finally {out.flush();in.close();out.close();client_socket.close();}
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return getted;
    }

    public Mailbox getMailbox() {return mailbox;}

    @Override
    public String toString() {
        return "\n      MailClient{" +
                ", mailbox=" + mailbox +
                "}";
    }
}

