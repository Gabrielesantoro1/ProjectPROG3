package com.unito.prog3.fmail.client;

import com.unito.prog3.fmail.ClientMain;
import com.unito.prog3.fmail.model.Email;
import com.unito.prog3.fmail.model.MailClient;
import com.unito.prog3.fmail.model.Mailbox;
import com.unito.prog3.fmail.support.Support;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ClientController implements Initializable {
    private static MailClient client;

    //ConnectionClient.fxml
    @FXML
    TextField account_name;

    //Home.fxml
    @FXML
    private TextField account_name_text;

    //SendmailPage.fxml
    @FXML
    private TextArea Area_sendpage;
    @FXML
    private TextField Recipient_sendpage;
    @FXML
    private TextField Object_sendpage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void getConnectionAction() throws IOException {
        if(Support.match_account(account_name.getText())){
            client = new MailClient(new Mailbox(account_name.getText()));
            if(client.getConnection()){
                System.out.println("Client connected");
                Parent root = FXMLLoader.load(Objects.requireNonNull(ClientMain.class.getResource("Home.fxml")));
                Stage window = (Stage) account_name.getScene().getWindow();
                window.setScene(new Scene(root));
                //fill_TableView(client.getMailbox().getMail_rcvd());
            }else{
                Alert emailnotregistreder = new Alert(Alert.AlertType.NONE, "L'email inserita non risulta registrata, inserisci una mail valida",ButtonType.OK);
                emailnotregistreder.showAndWait();
            }
        }else{
            System.out.println("Email not correct");
            Alert emailnotmatch = new Alert(Alert.AlertType.NONE, "L'email inserita ha un formato non corretto, riprova", ButtonType.OK);
            emailnotmatch.showAndWait();
        }
    }

    /*
    public void fill_TableView(List<Email> email_list){
        ObservableList<Email> emails = FXCollections.observableArrayList();
        emails.addAll(email_list);
        From_rcvd.setCellValueFactory(new PropertyValueFactory<Email, String>("from"));
        Object_rcvd.setCellValueFactory(new PropertyValueFactory<Email, String>("object"));
        Date_rcvd.setCellValueFactory(new PropertyValueFactory<Email, Date>("date"));
        Tableview_email_rcvd.getItems().setAll(email_list);
        System.out.println("Tabella riempita");
    }

    */

    public void openSendPage(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(ClientMain.class.getResource("SendmailPage.fxml")));
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void SendmailAction(ActionEvent event) {
        String recipient = Recipient_sendpage.getText();
        String text = Area_sendpage.getText();
        String object = Object_sendpage.getText();

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
                Recipient_sendpage.clear();
            }else{
                Alert emailsent = new Alert(Alert.AlertType.NONE, "La mail é stata inviata a tutti i destinatari con successo.", ButtonType.OK);
                emailsent.showAndWait();
                Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                stage.close();
            }
        }else{
            Alert emailsnotcorrect = new Alert(Alert.AlertType.NONE,"Ricontrolla le Email inserite.",ButtonType.OK);
            emailsnotcorrect.showAndWait();
        }
    }


    public void updateMailbox(ActionEvent event) {
        if(client.refresh_listEmail()){
            Alert emailsrefreshed = new Alert(Alert.AlertType.NONE, "La lista delle mail é stata aggiornata con successo",ButtonType.OK);
            emailsrefreshed.showAndWait();
        }else{
            Alert emailsNotrefreshed = new Alert(Alert.AlertType.NONE, "Errore nell'aggiornare le mail, riprova.",ButtonType.OK);
            emailsNotrefreshed.showAndWait();
        }
    }

    public void deleteMails(ActionEvent event) {
        if(client.del_listEmaildel()){
            Alert emailsdeleted = new Alert(Alert.AlertType.NONE, "La lista delle email eliminate é stata svuotata definitivamente", ButtonType.OK);
            emailsdeleted.showAndWait();
        }else{
            Alert emailsnotdeleted = new Alert(Alert.AlertType.NONE, "Si é presentato un errore, riprova", ButtonType.OK);
            emailsnotdeleted.showAndWait();
        }
    }
}



