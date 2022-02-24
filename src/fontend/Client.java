package fontend;

import models.Message;
import models.Type;
import models.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

class Client {

    public static boolean checkPassword(String s) {
        if (s.length() < 8) {
            return false;
        }
        int t = 0, t1 = 0, t2 = 0;
        for (int count = 0; count < s.length(); count++) {
            if (s.charAt(count) > 64 && s.charAt(count) < 91) {
                t++;
                continue;
            }
            if (s.charAt(count) > 96 && s.charAt(count) < 123) {
                t1++;
                continue;
            }
            if (s.charAt(count) > 47 && s.charAt(count) < 58) {
                t2++;
                continue;
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
                System.out.println("\n-----MENU-----");
                System.out.println("""
                        1: Message
                        2: Login
                        3: Register
                        4: Exit""");
                int actionNumber = 0;
                String tempActionNumber = null;

                while (true) {
                    do {
                        System.out.print("Enter number 1 to 4: ");
                        tempActionNumber = sc.nextLine();
                    } while (!(tempActionNumber.matches("[0-9]+") && tempActionNumber.length() > 0));
                    actionNumber = Integer.parseInt(tempActionNumber);
                    if (actionNumber >= 1 && actionNumber <= 4) {
                        break;
                    }
                }
                if (actionNumber == 4) {
                    sc.close();
                    socket.close();
                    break;
                }

                switch (actionNumber) {
                    case 1 -> {
                        while (true) {
                            System.out.println("\n-----ECHO-----");
                            System.out.println("1: back");
                            Scanner sc1 = new Scanner(System.in);
                            String echoMessage = inputMessage(sc1);
                            if (echoMessage.equals("")) {
                                continue;
                            }
                            if (echoMessage.equalsIgnoreCase("1")) {
                                break;
                            }

                            os.writeObject(new Message<>(Type.ECHO, echoMessage));
                            os.flush();

                            /*displaying server reply*/
                            Message returnMessage = (Message) is.readObject();
                            System.out.println("SERVER>>> " + returnMessage.getT());
                        }
                    }
                    case 2 -> {
                        while (true) {
                            System.out.println("\n-----LOGIN-----");

                            /*sending the user input to server*/
                            User member = new User();
                            member.inputUser();
                            os.writeObject(new Message<>(Type.LOGIN, member));
                            os.flush();

                            /*displaying server reply*/
                            String line1 = is.readUTF();
                            if (line1.equalsIgnoreCase("Login succeed")) {
                                System.out.println("Successful login");
                            } else {
                                System.out.println("Wrong username or password");
                            }
                            Scanner tempSc = new Scanner(System.in);
                            boolean isContinue = isContinue(tempSc);
                            if (!isContinue) {
                                break;
                            }
                        }
                    }
                    case 3 -> {
                        while (true) {
                            System.out.println("\n-----REGISTER-----");

                            /*sending the user input to server*/
                            User member = new User();

                            while (true) {
                                member.inputUser();
                                if (checkPassword(member.getPassword())) {
                                    break;
                                } else {
                                    System.out.println("Password must contain at least 8 characters, includes upper and lower case and number!!");
                                }
                            }
                            os.writeObject(new Message<User>(Type.REGISTER, member));
                            os.flush();

                            /*displaying server reply*/
                            String line1 = is.readUTF();
                            switch (line1) {
                                case "Username already existed" -> {
                                    System.out.println("Username was taken. You have to re-register!!");
                                }
                                case "success" -> {
                                    System.out.println("You have successfully registered.");
                                }
                                case "fail" -> {
                                    System.out.println("Something wrong when register!!");
                                }
                            }
                            Scanner tempSc = new Scanner(System.in);
                            boolean isContinue = isContinue(tempSc);
                            if (!isContinue) {
                                break;
                            }
                        }
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String inputMessage(Scanner sc) {
        System.out.print("CLIENT>>> ");
        return sc.nextLine();
    }

    public static boolean isContinue(Scanner sc) {
        String temp;
        while (true) {
            System.out.print("Do you want continue action (Y/N):  ");
            temp = sc.nextLine();
            boolean quit = temp.equalsIgnoreCase("y");
            boolean notQuit = temp.equalsIgnoreCase("n");
            if (quit || notQuit) {
                return quit;
            }
        }
    }
}