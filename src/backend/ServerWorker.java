package backend;//package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerWorker implements Runnable {
    private ServerSocket serverSocket;
    private ExecutorService executor;
    public static final int NUM_OF_THREAD = 4;
    public final static int SERVER_PORT = 9091;

    public ServerWorker() {
    }

    public void init(int port) throws IOException {
        System.out.println("Binding to port " + SERVER_PORT + ", please wait  ...");
        serverSocket = new ServerSocket(port);
        System.out.println("Server started: " + serverSocket);
        System.out.println("Waiting for a client ...");
        executor = Executors.newFixedThreadPool(NUM_OF_THREAD);
    }

    public void loop() throws IOException {
        Socket client;
        while (true) {
            client = serverSocket.accept();
            System.out.println("Got connection from: " + client.getInetAddress());

            ServerThread serverThread = new ServerThread(client);
            executor.execute(serverThread);
        }
    }

    public void run() {
        try {
            init(SERVER_PORT);
            loop();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
