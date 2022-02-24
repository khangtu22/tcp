package fontend;//package com.company;

import models.Message;
import models.Type;
import models.User;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientTest {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 9091)) {
            System.out.print("Enter clientName: ");
            BufferedReader echoes = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter stringToEcho = new PrintWriter(socket.getOutputStream(), true);


            ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
            User user = new User("khang", "123456");

            Message<User> message = new Message<>(Type.LOGIN, user);
            // send action message

            os.writeObject(message);
            os.flush();
            Scanner scanner = new Scanner(System.in);

            String echoString;
            String response;

            do {
                echoString = scanner.nextLine();

                stringToEcho.println(echoString);
                if(!echoString.equals("exit")) {
                    response = echoes.readLine();
                    System.out.println(response);
                }
            } while(!echoString.equals("exit"));


        } catch (IOException e) {
            System.out.println("frontend.Client Error: " + e.getMessage());
        }
    }
}