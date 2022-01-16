package com.unito.prog3.fmail.server;

import com.unito.prog3.fmail.model.MailServer;
import com.unito.prog3.fmail.support.Support;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MailServerController implements Initializable{

    private static MailServer server;

    @FXML
    ListView<String> logs;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        server = new MailServer();
        server.addMailBox(Support.daniele);
        server.addMailBox(Support.danieleSer);
        server.addMailBox(Support.gabriele);
        logs.itemsProperty().bind(server.logsProperty());
        try {
            server.create_dirs();
            server.loadEmailFromLocal();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        System.out.println(server.toString());
    }

    public void connect(ActionEvent event) {
        ExecutorService exc = Executors.newFixedThreadPool(10);
        try {
            ServerSocket server_socket = new ServerSocket(Support.port);
            server.addLog(new Date() + " : Server connected.");
            //TODO: Questo while fa impallare  la grafica
            /*while (!server_socket.isClosed()) {
                Socket incoming = server_socket.accept();
                Runnable connectionHandle = new ThreadConnectionHandle(server,incoming);
                exc.execute(connectionHandle);
            }

             */
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
