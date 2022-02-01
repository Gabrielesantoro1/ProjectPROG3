package com.unito.prog3.fmail.model;

import com.unito.prog3.fmail.support.Support;
import javafx.beans.property.ListProperty;
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

import static javax.swing.UIManager.get;

public class MailServer{
    private static AtomicInteger emailId_count;
    private List<Mailbox> mailboxes;
    private ListProperty<String> logs;
    private ObservableList<String> logs_content;

    /**
     *   {@code MailServer} Constructor
     **/
    public MailServer() {
        this.mailboxes = new ArrayList<>();
        emailId_count = new AtomicInteger();
        this.logs_content = FXCollections.observableList(new LinkedList<>());
        this.logs = new SimpleListProperty<>();
        this.logs.set(logs_content);
    }

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
     * The function automatically extracts the sender and the recipient information
     * and save the email in the right directory of each one. It also updates the value of the id_counter.
     * @param email_to_write Object Email to write
     */
    public boolean saveEmail(Email email_to_write, String single_recipient) throws IOException {
        boolean saved = false;
        email_to_write.setId(emailId_count.getAndIncrement());

        File id = new File(Support.PATH_NAME_DIR+"\\id_count.txt");
        FileOutputStream fos = new FileOutputStream(id,false);
        fos.write(emailId_count.toString().getBytes());

        String path_sender = Support.PATH_NAME_DIR + "\\" + email_to_write.getFrom() +"\\sent";
        String path_recipient = Support.PATH_NAME_DIR + "\\" + single_recipient + "\\received";
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
                buffer.close();

                this.mailboxes.get(getIndexByName(email_to_write.getFrom())).setMailSent(email_to_write);
                this.mailboxes.get(getIndexByName(single_recipient)).setMailRcvd(email_to_write);
                saved = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return saved;
    }

    /**
     * The function scrolls through all the folders of each account saved locally and for each account
     * and for each folder of the emails received, sent and deleted, loads the emails in the list corresponding
     * to each account. Forward also loads the current value of the id_count
     */
    public void loadEmailFromLocal() throws IOException, ParseException {
        File file = new File(Support.PATH_NAME_DIR);
        for (File account : Objects.requireNonNull(file.listFiles())){
            if(account.getName().equals("id_count.txt")){
                BufferedReader reader = new BufferedReader(new FileReader(account.getAbsolutePath()));
                String id_value = reader.readLine();
                emailId_count.set(Integer.parseInt(id_value));
                return;
            }
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
                                this.mailboxes.get(this.getIndexByName(account.getName())).setMail_del(email_to_load);
                                reader.close();
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
                                this.mailboxes.get(this.getIndexByName(account.getName())).setMailSent(email_to_load);
                                reader.close();
                            }
                        }
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
                                this.mailboxes.get(this.getIndexByName(account.getName())).setMailRcvd(email_to_load);
                                reader.close();
                            }
                        }
                    default:
                        break;
                }
            }
        }
        System.out.println("All the mailboxes were loaded successfully from local directory");
    }

    public void clearDelEmail(String account_name){
        String path = Support.PATH_NAME_DIR + "\\" + account_name +"\\deleted";
        File file = new File(path);
        for(File emails : file.listFiles()){
            emails.delete();
        }
    }

    public void deleteEmail_rcvd(String account_name, int id) throws IOException {
        String path_rcvd = Support.PATH_NAME_DIR + "\\" + account_name +"\\received\\" + id + ".txt";
        String path_del = Support.PATH_NAME_DIR + "\\" + account_name +"\\deleted\\" + id + ".txt";
        Path rcvd = Paths.get(path_rcvd);
        Path del = Paths.get(path_del);
        System.out.println(Files.move(rcvd,del));
    }

    public void deleteEmail_sent(String account_name, int id) throws IOException {
        String path_sent = Support.PATH_NAME_DIR +"\\"+ account_name +"\\sent\\" + id + ".txt";
        String path_del = Support.PATH_NAME_DIR + "\\" + account_name +"\\deleted\\" + id + ".txt";
        Path sent = Paths.get(path_sent);
        Path del = Paths.get(path_del);
        Files.move(sent, del);
    }

    private String getNameByIndex(Integer i){return mailboxes.get(i).getAccount_name();}

    public int getIndexByName(String account){
        for (int i = 0; i < mailboxes.size(); i++){
            if(account.equals(mailboxes.get(i).getAccount_name())){
                return i;
            }
        }
        return -1;
    }

    public void addMailBox(Mailbox mailbox){this.mailboxes.add(mailbox);}

    public List<Mailbox> getMailboxes() {return mailboxes;}

    public ListProperty<String> logsProperty(){return logs;}

    public Boolean existAccount(String account_name){
        boolean exist = false;
        for(int i = 0; i < mailboxes.size() && !exist; i++){
            if(mailboxes.get(i).getAccount_name().equals(account_name)){
                exist = true;
            }
        }
        return  exist;
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
