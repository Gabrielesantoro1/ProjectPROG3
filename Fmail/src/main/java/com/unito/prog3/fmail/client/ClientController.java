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
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class ClientController implements Initializable {
    private static MailClient client;
    private Thread heartbeatThread;

    //Home.fxml
    @FXML
    private TextField account_name_text;
    @FXML
    private ListView<String> ListView_rcvd;

    //SendmailPage.fxml


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    /**
     *If the server is offline, a popup is sent. Otherwise the deleteMails() function is called.
     */
    public void deleteButton(ActionEvent event) {
        if(client.isConnect()) {
            if (client.requestAction("delete_all","")) {
                alertMethod("Mails deleted have been completely erased");
            } else {
                alertMethod("An error occurred while deleting the emails");
            }
        }else{
            alertMethod("The server is momentarily offline, please try again in a while");
        }
    }

    /**
     * Create a new Alert pop-up
     * @param alert_string string to show
     */
    private void alertMethod(String alert_string){
        Alert alert = new Alert(Alert.AlertType.NONE,alert_string, ButtonType.OK);
        alert.showAndWait();
    }
}



