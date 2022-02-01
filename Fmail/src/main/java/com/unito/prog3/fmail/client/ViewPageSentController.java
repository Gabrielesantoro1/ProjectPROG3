package com.unito.prog3.fmail.client;

import com.unito.prog3.fmail.ClientMain;
import com.unito.prog3.fmail.model.Email;
import com.unito.prog3.fmail.model.MailClient;
import com.unito.prog3.fmail.support.Support;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewPageSentController {
    private MailClient client;
    private Email email;

    @FXML
    private TextArea email_text;
    @FXML
    private Text from_text;
    @FXML
    private Text to_text;
    @FXML
    private Text object_text;

    public void initModel(MailClient client, Email email){
        this.client = client;
        if(email != null) {
            this.email = email;
            this.from_text.setText(email.getFrom());
            this.email_text.setText(email.getText());
            this.to_text.setText(email.getTo());
            this.object_text.setText(email.getObject());
        }
    }

    public void DeleteButton_sent(ActionEvent event) {
        if(client.isConnect()){
            if(client.deleteAction("delete_single", Integer.toString(email.getId()),"sent")){
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
                Support.alertMethod("Email moved to deleted mails");
            }else{
                Support.alertMethod("An error occurred, try later.");
            }
        }
    }
}
