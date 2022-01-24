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
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    /**
     * The function checks if the inserted email is of the correct format, calls the getConnection () function which returns a result of type String. If the result is "CC" then the client was able to connect successfully, then starts a backgroud thread that checks every 5000 milliseconds if the server is still connected and finally changes the scene. If the result is "CNR" it means that the email entered is not a registered email. Finally, if the result is "SNC" it means that we have not been able to establish a connection with the server because the server is probably offline.
     */
    @FXML
    public void getConnectionButton() throws IOException {
        if(Support.match_account(account_name.getText())){
            client = new MailClient(new Mailbox(account_name.getText()));
            String result = client.getConnection();
            System.out.println(result);
            switch (result) {
                //Client Connected
                case "CC" -> {
                    //HeartBeat to check every 5000ms if the server is still online
                    heartbeatThread = new Thread(() -> {
                        while(true){
                            try{
                                Socket client_socket = new Socket(client.getLocal(),Support.port);
                                client_socket.getOutputStream().write(666);
                                client.setConnect(true);
                                System.out.println("Still connected");
                                Thread.sleep(5000);
                            }catch (IOException | InterruptedException e) {
                                System.out.println("Server offline");
                                client.setConnect(false);
                            }
                        }
                    });
                    heartbeatThread.setDaemon(true);
                    heartbeatThread.start();
                    //Change scene
                    Parent root = FXMLLoader.load(Objects.requireNonNull(ClientMain.class.getResource("Home.fxml")));
                    Stage window = (Stage) account_name.getScene().getWindow();
                    window.setScene(new Scene(root));
                    /*window.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent windowEvent) {
                            System.exit(0);
                        }
                    });*/
                    client.automaticUpdate();
                }
                //Client Not Registered
                case "CNR" -> {
                    alertMethod("Email account inserted is not registered, try with another email account");
                }
                //Server Not Connected
                case "SNC" -> {
                    alertMethod("There was an error connecting to the server, please try again");
                }
            }
        }else{
            System.out.println("Email not correct");
            alertMethod("Email account inserted does not respect the format requested");
        }
    }

    /**
     * If the client is connected with the server, then if the server is not offline, the screen for compiling the email to be sent opens
     *
     */
    public void SendPageButton(ActionEvent event) throws IOException {
        if(client.isConnect()) {
            Parent root = FXMLLoader.load(Objects.requireNonNull(ClientMain.class.getResource("SendmailPage.fxml")));
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }else{
            alertMethod("The server is momentarily offline, please try again in a while");
        }
    }

    /**
     *If the server is offline, a popup is sent. Otherwise the inserted fields are analyzed, it is checked if there are more than one mail and the sendEmail () function is called.
     */
    public void SendmailButton(ActionEvent event) {
        if(client.isConnect()) {
            String recipient = recipient_sendpage.getText();
            String text = area_sendpage.getText();
            String object = object_sendpage.getText();

            //Split recipient if there are more than one
            String[] recipients = recipient.split(" ");
            boolean recipients_corrects = true;
            for (String s : recipients) {
                System.out.println(client.getMailbox().getAccount_name());
                if (!Support.match_account(s) || Objects.equals(client.getMailbox().getAccount_name(), s)) {
                    recipients_corrects = false;
                }
            }
            //Sends the email to all recipients
            List<String> recipients_failed = new ArrayList<>();
            if (recipients_corrects) {
                for (String s : recipients) {
                    if (!client.sendEmail(new Email(client.getMailbox().getAccount_name(), s, object, text))) {
                        recipients_failed.add(s);
                    }
                }
                //Check if there were any recipients to whom the email could not be sent
                if (!recipients_failed.isEmpty()) {
                    String recipients_failed_string = "";
                    for (String s : recipients_failed) {
                        recipients_failed_string += s + "\n";
                    }
                    alertMethod("Mail to the following recipients: " + recipients_failed_string + " have not been sent");
                    recipient_sendpage.clear();
                } else {
                    alertMethod("Mail sent successfully to all the recipients");
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.close();
                }
            } else {
                alertMethod("Check the mail account inserted");
            }
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
            } else {
                alertMethod("An error occurred updating the mailbox");
            }
        }else{
            alertMethod("The server is momentarily offline, please try again in a while");
        }
    }

    /**
     *If the server is offline, a popup is sent. Otherwise the deleteMails() function is called.
     */
    public void deleteButton(ActionEvent event) {
        if(client.isConnect()) {
            if (client.requestAction("delete")) {
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



