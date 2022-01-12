package com.unito.prog3.fmail;

import com.unito.prog3.fmail.model.MailServer;
import com.unito.prog3.fmail.model.Mailbox;
import com.unito.prog3.fmail.server.ThreadConnectionHandle;
import com.unito.prog3.fmail.support.Support;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
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
        stage.show();
    }

    public static void main(String[] args) {
        ExecutorService exc = Executors.newFixedThreadPool(10);

        MailServer server = new MailServer();
        server.addMailBox(Support.daniele);
        server.addMailBox(Support.danieleSer);
        server.addMailBox(Support.gabriele);
        server.create_dirs();

        try {
            ServerSocket server_socket = new ServerSocket(Support.port);
            System.out.println("S0");
            while (true) {
                System.out.println("S1");
                Socket incoming = server_socket.accept();
                System.out.println("S2");
                Runnable connectionHandle = new ThreadConnectionHandle(server,incoming);
                System.out.println("S3");
                exc.execute(connectionHandle);
                System.out.println("S4");
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

    }
}



