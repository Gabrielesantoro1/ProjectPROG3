package com.unito.prog3.fmail;

import com.unito.prog3.fmail.model.Email;
import com.unito.prog3.fmail.model.MailServer;
import com.unito.prog3.fmail.model.Mailbox;
import com.unito.prog3.fmail.server.ThreadConnectionHandle;
import com.unito.prog3.fmail.support.Support;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ServerMain extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("ServerPage.fxml")));
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



