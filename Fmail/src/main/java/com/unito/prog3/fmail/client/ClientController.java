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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    @FXML
    TextField account_name;
    @FXML
    TableView Tableview_email_rcvd;

    @FXML
    public void getConnectionAction() throws IOException {
        if(Support.match_account(account_name.getText())){
            MailClient client = new MailClient(new Mailbox(account_name.getText()));
            if(client.getConnection()){
                System.out.println("Client connected");
                Parent root = FXMLLoader.load(Objects.requireNonNull(ClientMain.class.getResource("Home.fxml")));
                Stage window = (Stage) account_name.getScene().getWindow();
                window.setScene(new Scene(root));
                /*Tableview_email_rcvd.setEditable(false);
                Tableview_email_rcvd.getColumns().addAll()
                //TODO: Download di tutte le mail
                fillTableView(client.getMailbox().getMail_rcvd(), Tableview_email_rcvd);
                */
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

    //FUNZIONE IN W.I.P
    public void fillTableView(List<Email> emailList, TableView tableView){

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}



