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
                Alert email_not_registered = new Alert(Alert.AlertType.NONE, "L'email inserita non risulta registrata, inserisci una mail valida",ButtonType.OK);
                email_not_registered.showAndWait();
            }
        }else{
            System.out.println("Email not correct");
            Alert email_not_match = new Alert(Alert.AlertType.NONE, "L'email inserita ha un formato non corretto, riprova", ButtonType.OK);
            email_not_match.showAndWait();
        }
    }


    public void SendPageButton(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(ClientMain.class.getResource("SendmailPage.fxml")));
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

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
                    System.out.println("Email to "+s+" sent successfully");
                }else{
                    recipients_failed.add(s);
                }
            }
            if(!recipients_failed.isEmpty()){
                String recipients_failed_string = "";
                for(String s : recipients_failed){
                    recipients_failed_string += s+"\n";
                }
                Alert emailnotsent = new Alert(Alert.AlertType.NONE, "La mail ai seguenti destinatari non é stata inviata: \n"+recipients_failed_string+"Ricontrolla i campi o riprova.", ButtonType.OK);
                emailnotsent.showAndWait();
                recipient_sendpage.clear();
            }else{
                Alert emailsent = new Alert(Alert.AlertType.NONE, "La mail é stata inviata a tutti i destinatari con successo.", ButtonType.OK);
                emailsent.showAndWait();
                Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                stage.close();
            }
        }else{
            Alert email_not_correct = new Alert(Alert.AlertType.NONE,"Ricontrolla le Email inserite.",ButtonType.OK);
            email_not_correct.showAndWait();
        }
    }

    public void updateButton(ActionEvent event) {
        if(client.updateMailbox()){
            Alert email_updated = new Alert(Alert.AlertType.NONE, "La lista delle mail é stata aggiornata con successo",ButtonType.OK);
            email_updated.showAndWait();
        }else{
            Alert email_not_updated = new Alert(Alert.AlertType.NONE, "Errore nell'aggiornare le mail, riprova.",ButtonType.OK);
            email_not_updated.showAndWait();
        }
    }

    public void deleteButton(ActionEvent event) {
        if(client.deleteMailBox()){
            Alert email_deleted = new Alert(Alert.AlertType.NONE, "La lista delle email eliminate é stata svuotata definitivamente", ButtonType.OK);
            email_deleted.showAndWait();
        }else{
            Alert email_not_deleted = new Alert(Alert.AlertType.NONE, "Si é presentato un errore, riprova", ButtonType.OK);
            email_not_deleted.showAndWait();
        }
    }
}



