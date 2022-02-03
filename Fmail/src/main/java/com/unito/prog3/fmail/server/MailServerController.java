package com.unito.prog3.fmail.server;

import com.unito.prog3.fmail.model.MailServer;
import com.unito.prog3.fmail.support.Support;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.text.Annotation;
import java.text.ParseException;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class MailServerController implements Initializable{
    @FXML
    ListView<String> logs;
    @FXML
    private Text clock;
    @FXML
    private Text numClient;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        MailServer server = new MailServer();
        server.addMailBox(Support.daniele);
        server.addMailBox(Support.danieleSer);
        server.addMailBox(Support.gabriele);

        logs.itemsProperty().bind(server.logsProperty());
        numClient.textProperty().bind(Bindings.convert((server.NUM_CLIENT())));

        Timeline timer = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalTime currentTime = LocalTime.now();
            clock.setText(currentTime.getHour() + ":" + currentTime.getMinute() + ":" + currentTime.getSecond());
        }),new KeyFrame(Duration.seconds(1))
        );
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();

        try {
            server.create_dirs();
            server.loadEmailFromLocal();
        } catch (IOException | ParseException e) {e.printStackTrace();}
        System.out.println(server);
            Thread start_connection = new Thread(new StartConnectionHandle(server));
            start_connection.start();
    }

}
