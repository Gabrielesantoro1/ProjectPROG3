package com.unito.prog3.fmail.model;

import com.unito.prog3.fmail.support.Support;

import java.io.*;
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
    private static boolean Connect = false;
    
    /**
     * {@code MailClient} Constructor
     */
    public MailClient(Mailbox mailbox){
        try{
            local = InetAddress.getLocalHost();
        }catch (UnknownHostException e){e.printStackTrace();}
        this.mailbox = mailbox;
    }

    public MailClient(){}

    /**
     * The function tries to open a connection with the server, if it fails it means that the server is probably offline so it returns the String "SNC" to warn the controller. Otherwise it forwards its account name, if it receives a "true" result it means that the entered name is registered and then returns the String "CC" otherwise it returns the value "CNR"
     * @return A string that is able to make the controller understand what happened and therefore be able to proceed accordingly
     */
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
                    this.mailbox = (Mailbox) input.readObject();
                    Connect = true;
                }else{
                    return_result = "CNR"; //Client Not Registered
                }
            }finally {output.flush();input.close();output.close();client_socket.close();}
        }catch (IOException | ClassNotFoundException e){
            return_result = "SNC"; //Server Not Connected
        }
        return return_result;
    }

    /**
     *A connection with the server is created and the email is sent. Wait for a return value that indicates whether the email was sent successfully or not
     * @param email email to send
     * @return a Boolean value that indicates whether the email was sent successfully or not
     */
    public ArrayList<String> sendEmail(Email email) {
        ObjectOutputStream output;
        ObjectInputStream input;
        ArrayList<String> fails = new ArrayList<>();
        try{
            Socket client_socket = new Socket(this.local, Support.port);
            output = new ObjectOutputStream(client_socket.getOutputStream());
            input = new ObjectInputStream(client_socket.getInputStream());
            try {
                Objects.requireNonNull(output).writeObject(email);

                String in = (String) input.readObject();
                if(in.equals("true")){
                    System.out.println(mailbox.toString());
                }else{
                    fails = (ArrayList<String>) input.readObject();
                }
            } finally {output.flush();input.close();output.close();client_socket.close();}
        }catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
        return fails;
    }

    /**
     * Send a request to update its emails.
     * @return a Boolean value that indicates whether the mailbox was refreshed successfully or not
     */
    public boolean updateAction(){
        boolean result = false;
        if(Connect){
            ObjectOutputStream output;
            ObjectInputStream input;
            try {
                Socket client_socket = new Socket(this.local, Support.port);
                output = new ObjectOutputStream(client_socket.getOutputStream());
                input = new ObjectInputStream(client_socket.getInputStream());
                try {
                    ArrayList<String> client_request = new ArrayList<>();
                    client_request.add(this.mailbox.getAccount_name());
                    client_request.add("update");
                    Objects.requireNonNull(output).writeObject(client_request);

                    String in = (String) input.readObject();
                    if (in.equals("true")) {
                        this.mailbox = (Mailbox) input.readObject();
                        System.out.println("Emails Updated");
                        result = true;
                    }
                } finally {
                    output.flush();
                    input.close();
                    output.close();
                    client_socket.close();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Send a request to update its emails.
     * @return a Boolean value that indicates whether the mailbox was refreshed successfully or not
     */
    public boolean deleteAction(String request, String id, String position){
        boolean result = false;
        if(Connect){
            ObjectOutputStream output;
            ObjectInputStream input;
            try {
                Socket client_socket = new Socket(this.local, Support.port);
                output = new ObjectOutputStream(client_socket.getOutputStream());
                input = new ObjectInputStream(client_socket.getInputStream());
                try {
                    ArrayList<String> client_request = new ArrayList<>();
                    client_request.add(this.mailbox.getAccount_name());
                    client_request.add(request);
                    if(request.equals("delete_single")){
                        client_request.add(id);
                        client_request.add(position);
                    }

                    Objects.requireNonNull(output).writeObject(client_request);

                    String in = (String) input.readObject();
                    if (in.equals("true")) {
                        if (request.equals("delete_all")) {
                            this.mailbox.getAllMailDel().clear();
                        } else if (request.equals("delete_single")){
                            if(id.equals("rcvd")) {
                                this.mailbox.delete_email_rcvd(Integer.parseInt(id));
                            }else{
                                this.mailbox.delete_email_sent(Integer.parseInt(id));
                            }
                        }
                        result = true;
                    }
                } finally {
                    output.flush();
                    input.close();
                    output.close();
                    client_socket.close();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Start a timer where every 10000ms updates the mailbox
     */
    public void automaticUpdate(){
        Timer timer_update = new Timer();
        timer_update.schedule(new TimerTask() {
            @Override
            public void run() {
                if(updateAction()){

                }
            }
        }, 0, 5000);
    }

    public void startBeat(){
        //HeartBeat to check every 5000ms if the server is still online
        Thread heartbeatThread = new Thread(() -> {
            while (true) {
                try {
                    Socket client_socket = new Socket(this.local, Support.port);
                    ObjectOutputStream output = null;
                    ObjectInputStream input = null;
                    try {
                        output = new ObjectOutputStream(client_socket.getOutputStream());
                        output.writeObject(666);
                        this.setConnect(true);
                        System.out.println("Still connected");
                        Thread.sleep(3000);
                    }finally {output.close(); client_socket.close();}
                }catch(IOException | InterruptedException e){
                    System.out.println("Server offline");
                    this.setConnect(false);
                }
            }
        });
        heartbeatThread.setDaemon(true);
        heartbeatThread.start();
    }


    public char checkNewEmail(Integer mailrcvd_size, Integer mailsent_size, Integer maildel_size){
        int mailrcvd_newsize = this.mailbox.getAllMailRcvd().size();
        int mailsent_newsize = this.mailbox.getAllMailSent().size();
        int maildel_newsize = this.mailbox.getAllMailDel().size();

        if(!mailrcvd_size.equals(mailrcvd_newsize)){
            return 'r';
        }
        if(!mailsent_size.equals(mailsent_newsize)){
            return 's';
        }
        if(!maildel_size.equals(maildel_newsize)){
            return 'd';
        }
        return 0;
    }


    public Mailbox getMailbox() {return mailbox;}

    public InetAddress getLocal() {return local;}

    public boolean isConnect() {return Connect;}

    public void setConnect(boolean connect) {Connect = connect;}

    @Override
    public String toString() {
        return "\n      MailClient{" +
                ", mailbox=" + mailbox +
                "}";
    }
}

