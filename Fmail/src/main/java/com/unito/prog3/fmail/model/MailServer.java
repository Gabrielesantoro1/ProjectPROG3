package com.unito.prog3.fmail.model;

import com.unito.prog3.fmail.support.Support;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;

import java.io.*;
import java.lang.constant.Constable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MailServer{
    private List<Mailbox> mailboxes;

    /**
     *   {@code MailServer} Constructor
     **/
    public MailServer() {
        this.mailboxes = new ArrayList<>();
        System.out.println("MailServer created");
    }

    public void create_dirs(){
        for (int i = 0; i < this.getMailboxes().size(); i++) {
            String dir_name = this.getnamebyindex(i);
            File f = new File(Support.PATH_NAME_DIR + "\\" + dir_name + "\\" + "deleted");
            if (f.mkdirs()) {
                //System.out.println("Deleted email directory created");
            }
            f = new File(Support.PATH_NAME_DIR + "\\" + dir_name + "\\" + "received");
            if (f.mkdirs()) {
                //System.out.println("Received email directory created");
            }
            f = new File(Support.PATH_NAME_DIR + "\\" + dir_name + "\\" + "sent");
            if (f.mkdirs()) {
                //System.out.println("Sent email directory created");
            }
        }
    }

    /**
     * The function automatically extracts the sender and the recipient information and save the email in the right directory of each one.
     * @param email_to_write Object Email to write
     */
    public void saveEmailInLocal(Email email_to_write){
        String path_sender = Support.PATH_NAME_DIR + "\\" + email_to_write.getFrom() +"\\received";
        String path_recipient = Support.PATH_NAME_DIR + "\\" + email_to_write.getTo() + "\\sent";

        try{
            File rcvd = new File(path_recipient + "\\" +  email_to_write.getId() + ".txt");
            File sent = new File(path_recipient + "\\" +  email_to_write.getId() + ".txt");
            if(rcvd.createNewFile() && sent.createNewFile()){
                String what_write =  email_to_write.getId()+"\n"+email_to_write.getFrom()+"\n"+email_to_write.getTo()+"\n"+email_to_write.getObject()+"\n"+email_to_write.getText()+"\n"+email_to_write.getDate().toString();
                BufferedWriter buffer = new BufferedWriter(new FileWriter(rcvd));
                buffer.write(what_write);
                buffer.flush();

                buffer = new BufferedWriter(new FileWriter(sent));
                buffer.write(what_write);
                buffer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("The e-mail "+ email_to_write.getId() +" was successfully saved in memory");
    }

    /*Metodo per caricare nelle mailbox email da backup già esistente*/


    public Boolean existAccount(String account_name){
        boolean exist = false;
        for(int i = 0; i < mailboxes.size() && !exist; i++){
            if(mailboxes.get(i).getAccount_name().equals(account_name)){
                exist = true;
            }
        }
        return  exist;
    }

    private String getnamebyindex(Integer i){return mailboxes.get(i).getAccount_name();}

    public void removeMailBox(Mailbox mailbox){this.mailboxes.remove(mailbox);}

    public void addMailBox(Mailbox mailbox){this.mailboxes.add(mailbox);}

    public List<Mailbox> getMailboxes() {return mailboxes;}

    public void setMailboxes(List<Mailbox> mailboxes) {this.mailboxes = mailboxes;}

    @Override
    public String toString() {
        return "MailServer{\n" +
                "   MailBoxes=" + mailboxes.toString() +
                "}\n";
    }
}
