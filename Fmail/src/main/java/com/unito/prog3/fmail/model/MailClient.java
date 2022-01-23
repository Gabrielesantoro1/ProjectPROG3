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
    private boolean Connect = false;


    /**
     * {@code MailClient} Constructor
     */
    public MailClient(Mailbox mailbox){
        try{
            local = InetAddress.getLocalHost();
        }catch (UnknownHostException e){e.printStackTrace();}
        this.mailbox = mailbox;
    }

    public String getConnection() {
        ObjectOutputStream output;
        ObjectInputStream input;
        String return_result;
        try {
            Socket client_socket = new Socket(this.local, Support.port);
            output = new ObjectOutputStream(client_socket.getOutputStream());
            input = new ObjectInputStream(client_socket.getInputStream());
            try {
                Objects.requireNonNull(output).writeObject(this.mailbox.getAccount_name());

                String in = (String) input.readObject();
                if (in.equals("true")) {
                    return_result = "CC"; //Client Connected
                    Connect = true;
                }else{
                    return_result = "CNR"; //Client Not Registered
                }
                this.mailbox = (Mailbox) input.readObject();
            }finally {output.flush();input.close();output.close();client_socket.close();}
        }catch (IOException | ClassNotFoundException e){
            return_result = "SNC"; //Server Not Connected
        }
        return return_result;
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
                    System.out.println("Emails Updated");
                }
                return result;
            }finally {output.flush();input.close();output.close();client_socket.close();}
        } catch (IOException | ClassNotFoundException e) {}
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

    public InetAddress getLocal() {
        return local;
    }

    public boolean isConnect() {
        return Connect;
    }

    public void setConnect(boolean connect) {
        Connect = connect;
    }

    @Override
    public String toString() {
        return "\n      MailClient{" +
                ", mailbox=" + mailbox +
                "}";
    }
}

