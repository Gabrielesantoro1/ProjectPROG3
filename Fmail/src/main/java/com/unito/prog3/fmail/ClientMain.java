package com.unito.prog3.fmail;

import com.unito.prog3.fmail.client.ConnectionCrontoller;
import com.unito.prog3.fmail.model.MailClient;
import com.unito.prog3.fmail.support.Support;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Objects;

public class ClientMain extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader listLoader = new FXMLLoader(ClientMain.class.getResource("ConnectionClient.fxml"));
        Parent root = listLoader.load();
        ConnectionCrontoller connectionCrontoller = listLoader.getController();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        stage.show();
    }

    public static void main(String[] args){
        launch();
    }
}
