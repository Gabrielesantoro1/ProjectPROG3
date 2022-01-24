package com.unito.prog3.fmail;

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
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("ConnectionClient.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        /*window.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent windowEvent) {
                            System.exit(0);
                        }
                    });*/
        stage.show();
    }

    public static void main(String[] args){
        launch();
    }
}
