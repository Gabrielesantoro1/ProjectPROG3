package com.unito.prog3.fmail.model;
import com.unito.prog3.fmail.support.Support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Mailbox implements Serializable{
    private String account_name;
    private List<Email> mail_rcvd;
    private List<Email> mail_sent;
    private List<Email> mail_del;

    public Mailbox(String account_name) {
            if(Support.match_account(account_name)){
                this.account_name = account_name;
            }else{
                System.out.println("The account name does not follow the right pattern. Example: example@gmail.com");
            }
            mail_rcvd = new ArrayList<>();
            mail_del = new ArrayList<>();
            mail_sent = new ArrayList<>();
    }

    @Override
    public String toString() {
        return  "\n       account_name: " + account_name +
                "\n         mail_rcvd = " + mail_rcvd +
                "\n         mail_sent = " + mail_sent +
                "\n         mail_del = " + mail_del +
                '}';
    }

    public String getAccount_name() {return account_name;}

    public void setMail_rcvd(Email mail_rcvd) {
        this.mail_rcvd.add(mail_rcvd);
    }

    public void setMail_sent(Email mail_sent) {
        this.mail_sent.add(mail_sent);
    }

    public void setMail_del(Email mail_del) {
        this.mail_del.add(mail_del);
    }

    public void deleteEmails_del(){this.mail_del.clear();}

    public List<Email> getAllMail_rcvd(){return this.mail_rcvd;}

    public List<Email> getAllMail_sent(){return this.mail_sent;}

    public List<Email> getAllMail_del(){return this.mail_del;}

}
