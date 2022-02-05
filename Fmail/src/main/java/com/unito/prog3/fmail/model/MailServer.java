package com.unito.prog3.fmail.model;

import com.unito.prog3.fmail.support.Support;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MailServer{
    private static AtomicInteger emailId_count;
    private final List<Mailbox> mailboxes;
    private final ListProperty<String> logs;
    private final ObservableList<String> logs_content;
    private SimpleIntegerProperty NUM_CLIENT;

    /**
     *   {@code MailServer} Constructor
     **/
    public MailServer() {
        this.mailboxes = new ArrayList<>();
        emailId_count = new AtomicInteger();
        this.logs_content = FXCollections.observableList(new LinkedList<>());
        this.logs = new SimpleListProperty<>();
        this.logs.set(logs_content);
        NUM_CLIENT = new SimpleIntegerProperty();
        NUM_CLIENT.set(0);
    }

    public int getNUM_CLIENT() {return NUM_CLIENT.get();}

    public void setNUM_CLIENT(int NUM_CLIENT) {this.NUM_CLIENT.set(Integer.parseInt(String.valueOf(NUM_CLIENT)));}

    public SimpleIntegerProperty NUM_CLIENT(){return this.NUM_CLIENT;}

    /**It's called when the server is launched; it creates the directories for the mailboxes
     * of all the mail clients that are in the MailBoxes list.
     * @throws IOException;
     */
    public void create_dirs() throws IOException {
        for (int i = 0; i < this.getMailboxes().size(); i++) {
            String dir_name = this.getNameByIndex(i);

            File f = new File(Support.PATH_NAME_DIR + "\\" + dir_name + "\\" + "deleted");
            f.mkdirs();

            f = new File(Support.PATH_NAME_DIR + "\\" + dir_name + "\\" + "received");
            f.mkdirs();

            f = new File(Support.PATH_NAME_DIR + "\\" + dir_name + "\\" + "sent");
            f.mkdirs();
        }

        File f = new File(Support.PATH_NAME_DIR + "\\id_count.txt");
        if(f.createNewFile()){
            String s = "0";
            BufferedWriter buffer = new BufferedWriter(new FileWriter(f));
            buffer.write(s);
            buffer.flush();
            buffer.close();
        }
    }

    /**
     * The function extracts the sender and the recipient information
     * and save the email in the right directory of each one. It also updates the value of the id_counter.
     * @param email email to save
     * @param single_recipient recipient of the email (only for the email sent to only one person).
     * @throws IOException;
     * @return true if the call to the method ended correctly, false otherwise.
     */
    public boolean saveEmail(Email email, String single_recipient) throws IOException {
        boolean saved = false;
        email.setId(emailId_count.getAndIncrement());

        File id = new File(Support.PATH_NAME_DIR+"\\id_count.txt");
        FileOutputStream fos = new FileOutputStream(id,false);
        fos.write(emailId_count.toString().getBytes());
        fos.close();

        String path_sender = Support.PATH_NAME_DIR + "\\" + email.getFrom() +"\\sent";
        String path_recipient = Support.PATH_NAME_DIR + "\\" + single_recipient + "\\received";

        try{
            File rcvd = new File(path_recipient + "\\" +  email.getId() + ".txt");
            File sent = new File(path_sender + "\\" +  email.getId() + ".txt");

            if(rcvd.createNewFile() && sent.createNewFile()){
                SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String content =  email.getId()+"\n"+email.getFrom()+"\n"+email.getTo()+"\n"+email.getObject()+"\n"+email.getText().replaceAll("\n","@#%")+"\n"+ DateFor.format(email.getDate());

                BufferedWriter buffer = new BufferedWriter(new FileWriter(rcvd));
                buffer.write(content);
                buffer.flush();
                buffer.close();

                buffer = new BufferedWriter(new FileWriter(sent));
                buffer.write(content);
                buffer.flush();
                buffer.close();

                this.mailboxes.get(getIndexByName(email.getFrom())).setMailSent(email);
                this.mailboxes.get(getIndexByName(single_recipient)).setMailRcvd(email);
                saved = true;
            }
        } catch (IOException e) {e.printStackTrace();}

        return saved;
    }

    /**
     * It scrolls through all the folders of each account saved locally and for each account
     * and for each folder of the emails received, sent and deleted, loads the emails in the list corresponding
     * to each account. Forward also loads the current value of the id_count.
     * @throws IOException;
     * @throws ParseException;
     */
    public void loadEmailFromLocal() throws IOException, ParseException {
        File file = new File(Support.PATH_NAME_DIR);
        for (File main_dir : Objects.requireNonNull(file.listFiles())){
            System.out.println(main_dir);
            if(main_dir.getName().equals("id_count.txt")){
                BufferedReader reader = new BufferedReader(new FileReader(main_dir.getAbsolutePath()));
                String id_value = reader.readLine();
                emailId_count.set(Integer.parseInt(id_value));
                reader.close();
                break;
            }
            for(File list : Objects.requireNonNull(main_dir.listFiles())){
                System.out.println(list);
                switch (list.getName()){
                case "deleted":
                    for (File email: Objects.requireNonNull(list.listFiles())){
                        if(!email.isDirectory()){
                            BufferedReader reader = new BufferedReader(new FileReader(email.getAbsolutePath()));
                            String line = reader.readLine();
                            ArrayList<String> email_string_array = new ArrayList<>();
                            while(line != null){
                                email_string_array.add(line);
                                line = reader.readLine();
                            }
                            Email email_to_load = new Email(Integer.parseInt(email_string_array.get(0)), email_string_array.get(1), email_string_array.get(2), email_string_array.get(3), email_string_array.get(4).replaceAll("@#%","\n"), new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(email_string_array.get(5)));
                            this.mailboxes.get(this.getIndexByName(main_dir.getName())).setMail_del(email_to_load);
                            reader.close();
                        }
                    }
                    break;
                case "received":
                    for (File email: Objects.requireNonNull(list.listFiles())){
                        if(!email.isDirectory()){
                            BufferedReader reader = new BufferedReader(new FileReader(email.getAbsolutePath()));
                            String line = reader.readLine();
                            ArrayList<String> email_string_array = new ArrayList<>();
                            while(line != null){
                                email_string_array.add(line);
                                line = reader.readLine();
                            }
                            Email email_to_load = new Email(Integer.parseInt(email_string_array.get(0)), email_string_array.get(1), email_string_array.get(2), email_string_array.get(3), email_string_array.get(4).replaceAll("@#%","\n"), new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(email_string_array.get(5)));
                            this.mailboxes.get(this.getIndexByName(main_dir.getName())).setMailRcvd(email_to_load);
                            reader.close();
                        }
                    }
                    break;
                case "sent":
                    for (File email: Objects.requireNonNull(list.listFiles())){
                        if(!email.isDirectory()){
                            BufferedReader reader = new BufferedReader(new FileReader(email.getAbsolutePath()));
                            String line = reader.readLine();
                            ArrayList<String> email_string_array = new ArrayList<>();
                            while(line != null) {
                                email_string_array.add(line);
                                line = reader.readLine();
                            }
                            Email email_to_load = new Email(Integer.parseInt(email_string_array.get(0)), email_string_array.get(1), email_string_array.get(2), email_string_array.get(3), email_string_array.get(4).replaceAll("@#%", "\n"), new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(email_string_array.get(5)));
                            this.mailboxes.get(this.getIndexByName(main_dir.getName())).setMailSent(email_to_load);
                            reader.close();
                        }
                    }
                    break;
                }
            }
        }
        System.out.println("All the mailboxes were loaded successfully from local directory");
    }


    /**It's called when we want to delete all the email in the deleted mail list of the account.
     * @param account_name the account name of the mail client
     */
    public void clearDelEmail(String account_name){
        String path = Support.PATH_NAME_DIR + "\\" + account_name +"\\deleted";
        File file = new File(path);
        for(File email : Objects.requireNonNull(file.listFiles())){
            email.delete();
        }
    }

    /**
     * Move an Email from rcvd/sent to deleted.
     */
    public void deleteEmailRcvd(String account_name, int id) throws IOException {
        String path_rcvd = Support.PATH_NAME_DIR + "\\" + account_name +"\\received\\" + id + ".txt";
        String path_del = Support.PATH_NAME_DIR + "\\" + account_name +"\\deleted\\" + id + ".txt";
        Path rcvd = Paths.get(path_rcvd);
        Path del = Paths.get(path_del);
        Files.move(rcvd,del);
    }

    public void deleteEmailSent(String account_name, int id) throws IOException {
        String path_sent = Support.PATH_NAME_DIR +"\\"+ account_name +"\\sent\\" + id + ".txt";
        String path_del = Support.PATH_NAME_DIR + "\\" + account_name +"\\deleted\\" + id + ".txt";
        Path sent = Paths.get(path_sent);
        Path del = Paths.get(path_del);
        Files.move(sent, del);
    }

    private String getNameByIndex(Integer i){
        return mailboxes.get(i).getAccount_name();
    }

    public int getIndexByName(String account){
        for (int i = 0; i < mailboxes.size(); i++){
            if(account.equals(mailboxes.get(i).getAccount_name())){
                return i;
            }
        }
        return -1;
    }
    public synchronized void incrClient(){this.setNUM_CLIENT(this.getNUM_CLIENT()+1);}

    public void addMailBox(Mailbox mailbox){
        this.mailboxes.add(mailbox);
    }

    public List<Mailbox> getMailboxes() {return mailboxes;}

    public ListProperty<String> logsProperty(){
        return logs;
    }

    public Boolean existAccount(String account_name){
        boolean exist = false;
        for (Mailbox mailbox : this.mailboxes) {
            if (mailbox.getAccount_name().equals(account_name)) {
                exist = true;
                break;
            }
        }
        return exist;
    }

    public void addLog(String log){
        this.logs_content.add(log);
    }

    @Override
    public String toString() {
        return "MailServer{`\n" +
                "   mailboxes= " + mailboxes +
                '}';
    }
}
