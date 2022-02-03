package com.unito.prog3.fmail.server;

import com.unito.prog3.fmail.model.MailServer;
import com.unito.prog3.fmail.support.Support;
import javafx.application.Platform;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public record StartConnectionHandle(MailServer server) implements Runnable {
    @Override
    public void run() {
        ExecutorService exc = Executors.newFixedThreadPool(10);
        try {
            ServerSocket server_socket = new ServerSocket(Support.port);
            server.addLog(new Date() + " : Server connected.");
            while(true) {
                Socket incoming = server_socket.accept();
                Runnable connectionHandle = new ThreadConnectionHandle(server,incoming);
                exc.execute(connectionHandle);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
