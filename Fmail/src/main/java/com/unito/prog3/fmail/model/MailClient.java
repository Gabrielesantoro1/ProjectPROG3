package com.unito.prog3.fmail.model;

import com.unito.prog3.fmail.support.Support;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Objects;

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
        if(isConnect()){
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
                } finally {output.flush();input.close();output.close();client_socket.close();}
            } catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
        }
        return result;
    }

    /**
     * Send a request to update its emails.
     * @return a Boolean value that indicates whether the mailbox was refreshed successfully or not
     */
    public boolean deleteAction(String request, String id, String position){
        boolean result = false;
        if(isConnect()){
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
                        if (request.equals("delete_single")) {
                            this.mailbox = (Mailbox) input.readObject();
                            System.out.println(this.mailbox.toString());
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


    public void startBeat(){
        //HeartBeat to check every 5000ms if the server is still online
        Thread heartbeatThread = new Thread(() -> {
            while (true) {
                try {
                    Socket client_socket = new Socket(this.local, Support.port);
                    ObjectOutputStream output = null;
                    try {
                        output = new ObjectOutputStream(client_socket.getOutputStream());
                        output.writeObject(666);
                        this.setConnect(true);
                        System.out.println("Still connected");
                        Thread.sleep(3000);
                    }finally {
                        assert output != null;
                        output.close();
                        client_socket.close();
                    }
                }catch(IOException | InterruptedException e){
                    System.out.println("Server offline");
                    this.setConnect(false);
                }
            }
        });
        heartbeatThread.setDaemon(true);
        heartbeatThread.start();
    }

    public boolean closeAction(){
        boolean result = false;
            ObjectOutputStream output;
            ObjectInputStream input;
            try {
                Socket client_socket = new Socket(this.local, Support.port);
                output = new ObjectOutputStream(client_socket.getOutputStream());
                input = new ObjectInputStream(client_socket.getInputStream());
                try {
                    ArrayList<String> client_request = new ArrayList<>();
                    client_request.add(this.mailbox.getAccount_name());
                    client_request.add("close_connection");
                    Objects.requireNonNull(output).writeObject(client_request);

                    String in = (String) input.readObject();
                    if (in.equals("true")) {
                        result = true;
                        System.out.println("Request for connection closing was received");
                    }
                } finally {output.flush();input.close();output.close();client_socket.close();}
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        return result;
    }

    public int checkChangeMail(char list) {
        int new_size = 0;
        switch (list) {
            case 'r' -> {
                new_size = mailbox.getAllMailRcvd().size();
                if (new_size > 0) {
                    return new_size;
                }
            }
            case 's' -> {
                new_size = mailbox.getAllMailSent().size();
                if (new_size > 0) {
                    return new_size;
                }
            }
            case 'd' -> {
                new_size = mailbox.getAllMailDel().size();
                if (new_size > 0) {
                    return new_size;
                }
            }
        }
        return new_size;
    }


    public Mailbox getMailbox() {return mailbox;}

    public boolean isConnect() {return Connect;}

    public void setConnect(boolean connect) {Connect = connect;}

    @Override
    public String toString() {
        return "\n      MailClient{" +
                ", mailbox=" + mailbox +
                "}";
    }
}

