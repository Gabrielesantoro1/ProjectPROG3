package com.unito.prog3.fmail.client;

import com.unito.prog3.fmail.ClientMain;
import com.unito.prog3.fmail.model.Email;
import com.unito.prog3.fmail.model.MailClient;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.shape.Box;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.swing.*;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static com.unito.prog3.fmail.support.Support.alertMethod;

public class HomeController implements Initializable {
    private MailClient client;

    private ObservableList<Email> email_rcvd;

    private ObservableList<Email> email_sent;

    private ObservableList<Email> email_del;

    @FXML
    private TextField account_name_text;
    @FXML
    private ListView<Email> ListView_rcvd;
    @FXML
    private ListView<Email> ListView_sent;
    @FXML
    private ListView<Email> ListView_del;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ListView_rcvd.setOrientation(Orientation.VERTICAL);
        ListView_rcvd.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ListView_rcvd.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Email>() {
            @Override
            public void changed(ObservableValue<? extends Email> observableValue, Email email, Email t1) {
                FXMLLoader viewLoader = new FXMLLoader(ClientMain.class.getResource("ViewmailPage.fxml"));
                Parent root = null;
                try {
                    root = viewLoader.load();
                } catch (IOException e) {e.printStackTrace();}
                ViewPageController viewPageController= viewLoader.getController();
                viewPageController.initModel(client);
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        });
        ListView_rcvd.setCellFactory(new Callback<ListView<Email>, ListCell<Email>>() {
            @Override
            public ListCell<Email> call(ListView<Email> emailListView) {
                return new cellVisual();
            }
        });
    }

    private class cellVisual extends ListCell<Email>{
        @Override
        protected void updateItem(Email item, boolean empty) {
            super.updateItem(item, empty);
            if(item != null) {
                setText(item.getObject());
                setGraphic(new Box());

            }
        }
    }

    public void initModel(MailClient client){
        this.client = client;
        this.client.automaticUpdate();
        account_name_text.setText(client.getMailbox().getAccount_name());

        email_rcvd = FXCollections.observableList(client.getMailbox().getAllMailRcvd());
        ListView_rcvd.setItems(email_rcvd);
        email_sent = FXCollections.observableList(client.getMailbox().getAllMailSent());
        ListView_sent.setItems(email_sent);
        email_del = FXCollections.observableList(client.getMailbox().getAllMailDel());
        ListView_del.setItems(email_del);
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
     *If the server is offline, a popup is sent. Otherwise, the updateMailbox() function is called.
     */
    public void updateButton(ActionEvent event) {
        if (client.isConnect()) {
            if (client.requestAction("update")) {
                alertMethod("Mailbox has been updated successfully");
                System.out.println(client.getMailbox().toString());
                
                email_rcvd = FXCollections.observableList(client.getMailbox().getAllMailRcvd());
                ListView_rcvd.setItems(email_rcvd);
                ListView_sent.setItems(email_sent);
                ListView_del.setItems(email_del);
            } else {
                alertMethod("An error occurred updating the mailbox");
            }
        } else {
            alertMethod("The server is momentarily offline, please try again in a while");
        }
    }
    public void deleteButton(ActionEvent event) {}
}
