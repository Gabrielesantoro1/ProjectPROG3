package com.unito.prog3.fmail.client;

import com.unito.prog3.fmail.ClientMain;
import com.unito.prog3.fmail.model.Email;
import com.unito.prog3.fmail.model.MailClient;
import com.unito.prog3.fmail.support.Support;
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
        ListView_rcvd.getSelectionModel().selectedItemProperty().addListener((observableValue, email, t1) -> {
            FXMLLoader viewLoader = new FXMLLoader(ClientMain.class.getResource("ViewmailPage.fxml"));
            Parent root = null;
            try {
                root = viewLoader.load();
                ViewPageController viewPageController= viewLoader.getController();
                viewPageController.initModel(client, t1);
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {e.printStackTrace();}
        });
        ListView_rcvd.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Email> call(ListView<Email> emailrcvdView) {return new Support.cellVisual();}
        });

        ListView_sent.setOrientation(Orientation.VERTICAL);
        ListView_sent.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ListView_sent.getSelectionModel().selectedItemProperty().addListener((observableValue, email, t1) -> {
            FXMLLoader viewSentLoader = new FXMLLoader(ClientMain.class.getResource("ViewmailPageSent.fxml"));
            Parent root = null;
            try {
                root = viewSentLoader.load();
                ViewPageSentController viewPageSentController = viewSentLoader.getController();
                viewPageSentController.initModel(client, t1);
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {e.printStackTrace();}
        });
        ListView_sent.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Email> call(ListView<Email> emailsentView) {return new Support.cellVisual();}
        });
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
            if (client.updateAction()) {
                alertMethod("Mailbox has been updated successfully");
                System.out.println(client.getMailbox().toString());

                /*
                *Prima versione dell'aggiornamento automatico.
                *Ogni volta che si preme il bottone di aggiornamento chiamiamo la funzione checkNewEmail().
                *(vedere documentazione della funzione in MailClient.java)
                *Il carattere che ritorna indica qual è la lista che ha cambiato dimensione.
                *Cicliamo sulla lista partendo dalla vecchia misura massima fino alla nuova.
                *
                *CAPIRE SE E' EFFICIENTE E SE E' NECESSARIO SPOSTARE ANCHE QUESTA PARTE NEL CLIENT
                *PER RISPETTARE IL PATTERN MVC.
                * */

                char c = client.checkNewEmail(email_rcvd.size(),email_sent.size(),email_del.size());

                switch (c){
                    case 'r':
                        for(int i = email_rcvd.size(); i < client.getMailbox().getAllMailRcvd().size();i++){
                            email_rcvd.add(client.getMailbox().getAllMailRcvd().get(i));
                        }
                    case 's':
                        for(int i = email_sent.size(); i < client.getMailbox().getAllMailSent().size();i++) {
                            email_sent.add(client.getMailbox().getAllMailSent().get(i));
                        }
                    case 'd':
                        for(int i = email_del.size(); i < client.getMailbox().getAllMailDel().size();i++) {
                            email_del.add(client.getMailbox().getAllMailDel().get(i));
                        }
                            default:
                                System.out.println("An error occurred");
                }
            } else {
                alertMethod("An error occurred updating the mailbox");
            }
        } else {
            alertMethod("The server is momentarily offline, please try again in a while");
        }
    }

    private boolean checkNewEmail(){
        int old_size = email_rcvd.size();
        int new_size = client.getMailbox().getAllMailRcvd().size();

        if(old_size!=new_size){
            return true;
        }else{
            return false;
        }
    }




    /**
     *If the server is offline, a popup is sent. Otherwise the deleteMails() function is called.
     */
    public void deleteButton(ActionEvent event) {
        if(client.isConnect()) {
            if (client.deleteAction("delete_all","","")) {
                alertMethod("Mails deleted have been completely erased");
            } else {
                alertMethod("An error occurred while deleting the emails");
            }
        }else{
            alertMethod("The server is momentarily offline, please try again in a while");
        }
    }
}
