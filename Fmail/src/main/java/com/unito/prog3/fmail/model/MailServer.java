package com.unito.prog3.fmail.model;

import com.unito.prog3.fmail.support.Support;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;

import java.io.File;
import java.io.PrintWriter;
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

    /*Metodo per fare backup email arrivate già esistente*/


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
