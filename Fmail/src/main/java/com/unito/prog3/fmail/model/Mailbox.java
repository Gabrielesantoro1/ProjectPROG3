package com.unito.prog3.fmail.model;
import com.unito.prog3.fmail.support.Support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Mailbox implements Serializable{
    private String account_name;
    private final List<Email> mail_rcvd;
    private final List<Email> mail_sent;
    private final List<Email> mail_del;

    public Mailbox(String account_name) {
        if(Support.match_account(account_name)){
            this.account_name = account_name;
        }else{
            System.out.println("The account name does not follow the right pattern. Example: example@gmail.com");
        }
        mail_rcvd = new ArrayList<>();
        mail_sent = new ArrayList<>();
        mail_del = new ArrayList<>();
    }

    public String getAccount_name() {return account_name;}

    public void clearMailDel(){this.mail_del.clear();}

    /**Takes the id of the email that we want to delete
     * and first adds it to the mail_del list of the mailbox
     * then removes it from the mail_rcvd list.
     * @param id of the email
     */
    public synchronized void delete_email_rcvd(int id){
        this.mail_del.add(this.mail_rcvd.get(getIndexbyID_rcvd(id)));
        this.mail_rcvd.remove(getIndexbyID_rcvd(id));
    }

    public synchronized void delete_email_sent(int id){
        this.mail_del.add(this.mail_sent.get(getIndexbyID_sent(id)));
        this.mail_sent.remove(getIndexbyID_sent(id));
    }

    /**
     * Takes the ID of the email we want to delete and check on the mail list (rcvd in this case)
     * if there is such element. If it is, it returns the element position in the list.
     * @param ID of the email
     * @return i index of the email-ID
     */
    public int getIndexbyID_rcvd(int ID){
        for(int i = 0; i<mail_rcvd.size();i++){
            if(mail_rcvd.get(i).getId() == ID){
                return i;
            }
        }
        return -1;
    }

    public int getIndexbyID_sent(int ID){
        for(int i = 0; i<mail_sent.size();i++){
            if(mail_sent.get(i).getId() == ID){
                return i;
            }
        }
        return -1;
    }

    /**
     * Set an email
     */
    public void setMailRcvd(Email mail_rcvd) {this.mail_rcvd.add(mail_rcvd);}

    public void setMailSent(Email mail_sent) {this.mail_sent.add(mail_sent);}

    public void setMail_del(Email mail_del) {this.mail_del.add(mail_del);}

    /**
     * Get a List of Email
     */
    public List<Email> getAllMailRcvd(){return this.mail_rcvd;}

    public List<Email> getAllMailSent(){return this.mail_sent;}

    public List<Email> getAllMailDel(){return this.mail_del;}

    @Override
    public String toString() {
        return  "\n       account_name: " + account_name +
                "\n         mail_rcvd = " + mail_rcvd +
                "\n         mail_sent = " + mail_sent +
                "\n         mail_del = " + mail_del +
                '}';
    }

}
