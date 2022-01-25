package com.unito.prog3.fmail.client;

import com.unito.prog3.fmail.ClientMain;
import com.unito.prog3.fmail.model.MailClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.unito.prog3.fmail.support.Support.alertMethod;

public class HomeController implements Initializable {
    private MailClient client;
    @FXML
    private TextField account_name_text;
    @FXML
    private ListView<String> ListView_rcvd;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void initModel(MailClient client){
        this.client = client;
        this.client.automaticUpdate();
        account_name_text.setText(client.getMailbox().getAccount_name());
    }

    /**
     * If the client is connected with the server, then if the server is not offline, the screen for compiling the email to be sent opens
     *
     */
    public void SendPageButton(ActionEvent event) throws IOException {
        if(client.isConnect()) {
            FXMLLoader sendloader = new FXMLLoader(ClientMain.class.getResource("SendmailPage.fxml"));
            Parent root = sendloader.load();
            SendPageController sendPageController = sendloader.getController();
            sendPageController.initModel(client);
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }else{
            alertMethod("The server is momentarily offline, please try again in a while");
        }
    }

    /**
     *If the server is offline, a popup is sent. Otherwise the updateMailbox() function is called.
     */
    public void updateButton(ActionEvent event) {
        if(client.isConnect()) {
            if (client.requestAction("update")) {
                alertMethod("Mailbox has been updated successfully");
                System.out.println(client.getMailbox().toString());
            } else {
                alertMethod("An error occurred updating the mailbox");
            }
        }else{
            alertMethod("The server is momentarily offline, please try again in a while");
        }
    }

    public void deleteButton(ActionEvent event) {
    }
}
