package com.unito.prog3.fmail.client;

import com.unito.prog3.fmail.model.MailClient;
import com.unito.prog3.fmail.support.Support;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;
public class ViewPageController implements Initializable {
    private MailClient client;

    @FXML
    TextField email_text;
    @FXML
    Text from_text;
    @FXML
    Text to_text;
    @FXML
    Text object_text;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.email_text.setText("provaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        this.from_text.setText("pepinoaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        this.to_text.setText("aoushdoasdiasdasiodoas");
        this.object_text.setText("Prova");
    }

    public void initModel(MailClient client){
        this.client = client;
    }

    public void ReplyButton(ActionEvent event) {
    }

    public void ReplyAllBUtton(ActionEvent event) {
    }

    public void ForwardButton(ActionEvent event) {
    }

    public void DeleteButton(ActionEvent event) {
        if(client.isConnect()){
            if(client.requestAction("delete_single",""/*TODO:aggiungere ID email*/)){
                Support.alertMethod("Email moved to deleted mails");
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            }else{
                Support.alertMethod("An error occurred, try later.");
            }
        }
    }
}
