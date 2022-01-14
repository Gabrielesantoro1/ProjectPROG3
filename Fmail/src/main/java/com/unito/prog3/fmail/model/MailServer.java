package com.unito.prog3.fmail.model;

import com.unito.prog3.fmail.support.Support;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static javax.swing.UIManager.get;

public class MailServer{
    private AtomicInteger emailId_count; //TODO: bisogna salvare il valore in qualche posto per poterlo prendere anche dopo che il server si riconnette.
    private List<Mailbox> mailboxes;

    /**
     *   {@code MailServer} Constructor
     **/
    public MailServer() {
        this.mailboxes = new ArrayList<>();
        emailId_count = new AtomicInteger();
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
    public boolean saveEmail(Email email_to_write){
        boolean saved = false;
        email_to_write.setId(emailId_count.getAndIncrement());
        String path_sender = Support.PATH_NAME_DIR + "\\" + email_to_write.getFrom() +"\\sent";
        String path_recipient = Support.PATH_NAME_DIR + "\\" + email_to_write.getTo() + "\\received";

        try{
            File rcvd = new File(path_recipient + "\\" +  email_to_write.getId() + ".txt");
            File sent = new File(path_sender + "\\" +  email_to_write.getId() + ".txt");
            if(rcvd.createNewFile() && sent.createNewFile()){
                SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String what_write =  email_to_write.getId()+"\n"+email_to_write.getFrom()+"\n"+email_to_write.getTo()+"\n"+email_to_write.getObject()+"\n"+email_to_write.getText()+"\n"+ DateFor.format(email_to_write.getDate()).toString();
                BufferedWriter buffer = new BufferedWriter(new FileWriter(rcvd));
                buffer.write(what_write);
                buffer.flush();

                buffer = new BufferedWriter(new FileWriter(sent));
                buffer.write(what_write);
                buffer.flush();

                this.mailboxes.get(getindexbyname(email_to_write.getFrom())).setMail_sent(email_to_write);
                this.mailboxes.get(getindexbyname(email_to_write.getTo())).setMail_rcvd(email_to_write);
                saved = true;
                System.out.println("The e-mail "+ email_to_write.getId() +" was successfully saved in memory and in local");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  saved;
    }

    /**
     * The function scrolls through all the folders of each account saved locally and for each account and for each folder of the emails received, sent and deleted, loads the emails in the list corresponding to each account.
     * @throws IOException
     */
    public void loadEmailFromLocal() throws IOException, ParseException {
        File file = new File(Support.PATH_NAME_DIR);
        for (File account : Objects.requireNonNull(file.listFiles())){
            for(File lists : Objects.requireNonNull(account.listFiles())){
                switch (lists.getName()){
                    case "deleted":
                        for (File email: Objects.requireNonNull(lists.listFiles())){
                            if(!email.isDirectory()){
                                BufferedReader reader = new BufferedReader(new FileReader(email.getAbsolutePath()));
                                String line = reader.readLine();
                                ArrayList<String> email_string_array = new ArrayList<>();
                                while(line != null){
                                    email_string_array.add(line);
                                    line = reader.readLine();
                                }
                                Email email_to_load = new Email(Integer.parseInt(email_string_array.get(0)), email_string_array.get(1), email_string_array.get(2), email_string_array.get(3), email_string_array.get(4), new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(email_string_array.get(5)));
                                this.mailboxes.get(this.getindexbyname(account.getName())).setMail_del(email_to_load);
                            }
                        }

                    case "sent":
                        for (File email: Objects.requireNonNull(lists.listFiles())){
                            if(!email.isDirectory()){
                                BufferedReader reader = new BufferedReader(new FileReader(email.getAbsolutePath()));
                                String line = reader.readLine();
                                ArrayList<String> email_string_array = new ArrayList<>();
                                while(line != null){
                                    email_string_array.add(line);
                                    line = reader.readLine();
                                }
                                Email email_to_load = new Email(Integer.parseInt(email_string_array.get(0)), email_string_array.get(1), email_string_array.get(2), email_string_array.get(3), email_string_array.get(4), new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(email_string_array.get(5)));
                                this.mailboxes.get(this.getindexbyname(account.getName())).setMail_sent(email_to_load);
                            }
                        }
                        break;

                    case "received":
                        for (File email: Objects.requireNonNull(lists.listFiles())){
                            if(!email.isDirectory()){
                                BufferedReader reader = new BufferedReader(new FileReader(email.getAbsolutePath()));
                                String line = reader.readLine();
                                ArrayList<String> email_string_array = new ArrayList<>();
                                while(line != null){
                                    email_string_array.add(line);
                                    line = reader.readLine();
                                }
                                Email email_to_load = new Email(Integer.parseInt(email_string_array.get(0)), email_string_array.get(1), email_string_array.get(2), email_string_array.get(3), email_string_array.get(4), new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(email_string_array.get(5)));
                                this.mailboxes.get(this.getindexbyname(account.getName())).setMail_rcvd(email_to_load);
                            }
                        }
                        break;
                }
            }
        }
        System.out.println("All the mailboxes were loaded successfully from local directory");
    }

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

    public int getindexbyname(String account){
        for (int i = 0; i< mailboxes.size(); i++){
            if(account.equals(mailboxes.get(i).getAccount_name())){
                return i;
            }
        }
        return -1;
    }

    public void addMailBox(Mailbox mailbox){this.mailboxes.add(mailbox);}

    public List<Mailbox> getMailboxes() {return mailboxes;}

    @Override
    public String toString() {
        return "MailServer{`\n" +
                "   mailboxes= " + mailboxes +
                '}';
    }
}
