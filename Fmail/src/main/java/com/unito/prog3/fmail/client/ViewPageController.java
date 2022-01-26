package com.unito.prog3.fmail.client;

import com.unito.prog3.fmail.model.Email;
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
public class ViewPageController {
    private MailClient client;
    private Email email;

    @FXML
    private TextField email_text;
    @FXML
    private Text from_text;
    @FXML
    private Text to_text;
    @FXML
    private Text object_text;

    public void initModel(MailClient client, Email email){
        this.client = client;
        this.email = email;
        this.from_text.setText(email.getFrom());
        this.email_text.setText(email.getText());
        this.to_text.setText(email.getTo());
        this.object_text.setText(email.getObject());
    }

    public void ReplyButton(ActionEvent event) {
    }

    public void ReplyAllButton(ActionEvent event) {
    }

    public void ForwardButton(ActionEvent event) {
    }

    public void DeleteButton(ActionEvent event) {
        if(client.isConnect()){
            if(client.deleteAction("delete_single", Integer.toString(email.getId()))){
                Support.alertMethod("Email moved to deleted mails");
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            }else{
                Support.alertMethod("An error occurred, try later.");
            }
        }
    }

    public void ReplyAllBUtton(ActionEvent event) {
    }
}
