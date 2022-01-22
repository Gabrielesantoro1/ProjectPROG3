package com.unito.prog3.fmail.client;

import com.unito.prog3.fmail.ClientMain;
import com.unito.prog3.fmail.model.Email;
import com.unito.prog3.fmail.model.MailClient;
import com.unito.prog3.fmail.model.Mailbox;
import com.unito.prog3.fmail.support.Support;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ClientController implements Initializable {
    private static MailClient client;

    //ConnectionClient.fxml
    @FXML
    private TextField account_name;

    //Home.fxml
    @FXML
    private TextField account_name_text;
    @FXML
    private ListView<String> ListView_rcvd;

    //SendmailPage.fxml
    @FXML
    private TextArea area_sendpage;
    @FXML
    private TextField recipient_sendpage;
    @FXML
    private TextField object_sendpage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    public void getConnectionButton() throws IOException {
        if(Support.match_account(account_name.getText())){
            client = new MailClient(new Mailbox(account_name.getText()));
            if(client.getConnection()){
                System.out.println("Client connected");
                Parent root = FXMLLoader.load(Objects.requireNonNull(ClientMain.class.getResource("Home.fxml")));
                Stage window = (Stage) account_name.getScene().getWindow();
                window.setScene(new Scene(root));
                client.automaticUpdate();
            }else{
                alertMethod("Email account inserted is not registered, try with another email account");
            }
        }else{
            System.out.println("Email not correct");
            alertMethod("Email account inserted does not respect the format requested");
        }
    }

    public void SendPageButton(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(ClientMain.class.getResource("SendmailPage.fxml")));
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    //TODO Il metodo funziona ma penso si possano evitare alcuni passaggi, magari alla fine capiamo un attimo come.
    public void SendmailButton(ActionEvent event) {
        String recipient = recipient_sendpage.getText();
        String text = area_sendpage.getText();
        String object = object_sendpage.getText();

        String[] recipients = recipient.split(" ");
        boolean recipients_corrects = true;
        for(String s : recipients){
            System.out.println(client.getMailbox().getAccount_name());
            if(!Support.match_account(s) || Objects.equals(client.getMailbox().getAccount_name(), s)){
                recipients_corrects = false;
            }
        }
        List<String> recipients_failed = new ArrayList<>();
        if(recipients_corrects){
            for (String s : recipients){
                if(client.sendEmail(new Email(client.getMailbox().getAccount_name(), s, object,text))){
                    //System.out.println("Email to " + s + " sent successfully");
                }else{
                    recipients_failed.add(s);
                }
            }
            if(!recipients_failed.isEmpty()){
                String recipients_failed_string = "";
                for(String s : recipients_failed){
                    recipients_failed_string += s+"\n";
                }
                alertMethod("Mail to the following recipients: " + recipients_failed_string + " have not been sent");
                recipient_sendpage.clear();
            }else{
                alertMethod("Mail sent successfully to all the recipients");
                Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                stage.close();
            }
        }else{
            alertMethod("Check the mail account inserted");
        }
    }

    public void updateButton(ActionEvent event) {
        if(client.updateMailbox()){
            alertMethod("Mailbox has been updated successfully");
        }else{
            alertMethod("An error occurred updating the mailbox");
        }
    }

    public void deleteButton(ActionEvent event) {
        if(client.deleteMails()){
            alertMethod("Mails deleted have been completely erased");
        }else{
            alertMethod("An error occurred while deleting the emails");
        }
    }

    private void alertMethod(String alert_string){
        Alert alert = new Alert(Alert.AlertType.NONE,alert_string, ButtonType.OK);
        alert.showAndWait();
    }
}



