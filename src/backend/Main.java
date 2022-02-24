package backend;//package com.company;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ServerWorker server = new ServerWorker();
        server.run();
    }
}
