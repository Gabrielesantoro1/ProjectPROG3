package com.unito.prog3.fmail.server;

import com.unito.prog3.fmail.model.MailServer;
import com.unito.prog3.fmail.support.Support;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ResourceBundle;

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
            Thread start_connection = new Thread(new StartConnectionHandle(server));
            start_connection.start();
    }
}
