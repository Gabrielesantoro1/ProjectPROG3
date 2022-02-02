package com.unito.prog3.fmail.client;

import com.unito.prog3.fmail.ClientMain;
import com.unito.prog3.fmail.model.MailClient;
import com.unito.prog3.fmail.model.Mailbox;
import com.unito.prog3.fmail.support.Support;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import static com.unito.prog3.fmail.support.Support.alertMethod;

public class ConnectionCrontoller{
    private MailClient client;

    @FXML
    private TextField account_name;

    public void initModel(MailClient client){
        this.client = client;
    }

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
                    client.startBeat();
                    //Change scene
                    FXMLLoader homeloader = new FXMLLoader(ClientMain.class.getResource("Home.fxml"));
                    Parent root = homeloader.load();
                    HomeController homeController = homeloader.getController();
                    homeController.initModel(client);
                    Stage window = (Stage) account_name.getScene().getWindow();
                    window.setScene(new Scene(root));

                    //Close Windows EventHandler
                    window.setOnCloseRequest(windowEvent -> {
                        //TODO: ser il server è offline da errore
                        if(client.closeAction()) {
                            window.close();
                            System.exit(0);
                        }
                    });
                }
                //Client Not Registered
                case "CNR" -> alertMethod("Email account inserted is not registered, try with another email account");
                //Server Not Connected
                case "SNC" -> alertMethod("There was an error connecting to the server, please try again");
            }
        }else{
            System.out.println("Email not correct");
            alertMethod("Email account inserted does not respect the format requested");
        }
    }
}
