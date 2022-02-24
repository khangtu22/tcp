package fontend;

import models.Message;
import models.Type;
import models.User;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

class Client {
    public static boolean Check(String s) {
        if (s.length() < 8) {
            return false;
        }
        int t = 0, t1 = 0, t2 = 0;
        for (int count = 0; count < s.length(); count++) {
            if (s.charAt(count) > 'A' && s.charAt(count) < 'Z') {
                t++;
            }
            if (s.charAt(count) > 'a' && s.charAt(count) < 'z') {
                t1++;
            }
            if (s.charAt(count) > '0' && s.charAt(count) < '9') {
                t2++;
            }
        }
        return t > 0 && t1 > 0 && t2 > 0;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket("localhost", 9091)) {

            // writing to server
            System.out.println("Client connected");
            ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());

            // reading from server
            ObjectInputStream is = new ObjectInputStream(socket.getInputStream());

            // object of scanner class
            Scanner sc = new Scanner(System.in);
            String line = null;
            while (true) {
                System.out.println("Pick Action:");
                System.out.println("""
                        1: Message
                        2: Login
                        3: Register
                        4: Exit""");
                int actionNumber = 0;
                while (true){
                    System.out.print("Enter number 1 to 4: ");
                    actionNumber = sc.nextInt();
                    if (actionNumber >= 1 && actionNumber <= 4){
                        break;
                    }
                }

                switch (actionNumber){
                    case 1 -> {
                        while (true){
                            System.out.println("-----ECHO-----");
                            System.out.println("1: back");
                            Message<String> news = new Message<>();
                            System.out.print("Enter Message : ");
                            Scanner sc2 = new Scanner(System.in);
                            String echoMessage = sc2.nextLine();
                            if (echoMessage.equalsIgnoreCase("1")) {
                                break;
                            }
                            news.setT(echoMessage);
                            news.setType(Type.ECHO);

                            os.writeObject(news);
                            os.flush();
                            /*displaying server reply*/
                            Message returnMessage = (Message) is.readObject();
                            System.out.println("SERVER>>>" + returnMessage.getT());
                        }


                    }
                    case 2 -> {
                        while (true) {
                            System.out.println("-----LOGIN-----");
                            /*sending the user input to server*/
                            User member = new User();
                            member.inputUser();
                            os.writeObject(new Message<User>(Type.REGISTER, member));
                            os.flush();

                            /*displaying server reply*/
                            String line1 = is.readUTF();
                            if (line1.equalsIgnoreCase("Login succeed")) {
                                System.out.println("Successful login");
                                break;
                            } else {
                                System.out.println("Wrong username or password");
                            }
                        }
                    }
                    case 3 -> {
                        while (true) {
                            System.out.println("-----REGISTER-----");
                            /*sending the user input to server*/
                            User member = new User();
                            do {
                                member.inputUser();
                            } while (!Check(member.getPassword()));
                            os.writeObject(new Message<User>(Type.REGISTER, member));
                            os.flush();

                            /*displaying server reply*/
                            String line1 = is.readUTF();
                            switch (line1) {
                                case "Username already existed" -> {
                                    System.out.println("Username was taken. You have to re-register!!");
                                    continue;
                                }
                                case "success" -> {
                                    System.out.println("You have successfully registered.");
                                }
                                case "fail" -> {
                                    System.out.println("Something wrong when register!!");
                                }
                            }
                            break;
                        }
                    }
                    case 4 -> {
                        sc.close();
                        socket.close();
                        continue;
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}