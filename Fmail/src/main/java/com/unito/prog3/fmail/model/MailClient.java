package com.unito.prog3.fmail.model;

import com.unito.prog3.fmail.support.Support;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MailClient {
    private Mailbox mailbox;
    private InetAddress local;

    /**
     * {@code MailClient} Constructor
     */
    public MailClient(Mailbox mailbox){
        try{
            local = InetAddress.getLocalHost();
        }catch (UnknownHostException e){e.printStackTrace();}
        this.mailbox = mailbox;
    }

    public Boolean getConnection() {
        ObjectOutputStream output;
        ObjectInputStream input;
        boolean connection_established = false;
        try {
            Socket client_socket = new Socket(this.local, Support.port);
            output = new ObjectOutputStream(client_socket.getOutputStream());
            input = new ObjectInputStream(client_socket.getInputStream());
            try {
                Objects.requireNonNull(output).writeObject(this.mailbox.getAccount_name());

                String in = (String) input.readObject();
                if (in.equals("true")) {
                    connection_established = true;
                }
                this.mailbox = (Mailbox) input.readObject();
                return connection_established;
            }finally {output.flush();input.close();output.close();client_socket.close();}
        }catch (IOException | ClassNotFoundException e){e.printStackTrace();}
        return connection_established;
    }

    public boolean sendEmail(Email email) {
        ObjectOutputStream output;
        ObjectInputStream input;
        boolean saved = false;
        try{
            Socket client_socket = new Socket(this.local, Support.port);
            output = new ObjectOutputStream(client_socket.getOutputStream());
            input = new ObjectInputStream(client_socket.getInputStream());
            try {
                Objects.requireNonNull(output).writeObject(email);

                String in = (String) input.readObject();
                if(in.equals("true")){
                    this.mailbox.setMailSent(email);
                    saved = true;
                    System.out.println(mailbox.toString());
                }
                return saved;
            } finally {output.flush();input.close();output.close();client_socket.close();}
        }catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
        return saved;
    }

    //TODO updateMailBox e deleteMails sono praticamente uguali, possiamo unirli differenziando l'azione da fare con uno switch-case

    public boolean updateMailbox(){
        ObjectOutputStream output;
        ObjectInputStream input;
        boolean result = false;
        try{
            Socket client_socket = new Socket(this.local, Support.port);
            output = new ObjectOutputStream(client_socket.getOutputStream());
            input = new ObjectInputStream(client_socket.getInputStream());
            try{
                ArrayList<String> client_request = new ArrayList<>();
                client_request.add(this.mailbox.getAccount_name());
                client_request.add("refresh");

                Objects.requireNonNull(output).writeObject(client_request);

                String in = (String) input.readObject();
                if(in.equals("true")){
                    this.mailbox = (Mailbox) input.readObject();
                    result = true;
                    System.out.println(mailbox.toString());
                }
                return result;
            }finally {output.flush();input.close();output.close();client_socket.close();}
        } catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
        return result;
    }

    public boolean deleteMails() {
        ObjectOutputStream output;
        ObjectInputStream input;
        boolean deleted = false;
        try {
            Socket client_socket = new Socket(this.local, Support.port);
            output = new ObjectOutputStream(client_socket.getOutputStream());
            input = new ObjectInputStream(client_socket.getInputStream());
            try {
                ArrayList<String> client_request = new ArrayList<>();
                client_request.add(this.mailbox.getAccount_name());
                client_request.add("delete_all");

                Objects.requireNonNull(output).writeObject(client_request);

                String in = (String) input.readObject();
                if (in.equals("true")) {
                    this.mailbox.clearMailDel();
                    deleted = true;
                }
                return deleted;
            } finally {output.flush();input.close();output.close();client_socket.close();}
        } catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
        return deleted;
    }

    public void automaticUpdate(){
        Timer timer_update = new Timer();
        timer_update.schedule(new TimerTask() {
            @Override
            public void run() {
                updateMailbox();
            }
        }, 0, 10000);
    }

    public Mailbox getMailbox() {return mailbox;}

    @Override
    public String toString() {
        return "\n      MailClient{" +
                ", mailbox=" + mailbox +
                "}";
    }
}

