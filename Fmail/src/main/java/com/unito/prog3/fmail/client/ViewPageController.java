package com.unito.prog3.fmail.client;

import com.unito.prog3.fmail.model.MailClient;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewPageController implements Initializable {
    private MailClient cliet;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    public void initModel(MailClient client){
        this.cliet = client;
    }
}
