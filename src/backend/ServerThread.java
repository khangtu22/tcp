package backend;

import backend.connect.DBConnection;
import models.Message;
import models.Type;
import models.User;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ServerThread implements Runnable {

    public static final String USER_CREATE = "INSERT INTO user (name, password) VALUES (?, ?);";
    private static final ArrayList<ServerThread> instances = new ArrayList<>();
    private static final String USER_LOGIN = "SELECT * FROM user WHERE name = ? and password = ?";
    private static final String GET_USER_BY_USERNAME = "SELECT * FROM user WHERE name = ?";
    private final Socket clientSocket;

    public ServerThread(Socket socket) {
        clientSocket = socket;
        addInstance();
    }

    private synchronized void addInstance() {
        instances.add(this);
    }

    private synchronized void removeInstance() {
        instances.remove(this);
    }

    private synchronized String handleMessageEcho(String message) {
        message = message.replaceAll("[^a-zA-Z0-9]", " ");
        return message;
    }

    private synchronized User checkLogin(User user) throws SQLException {
        try (DBConnection dbHelper = DBConnection.getDBHelper();
             Connection connection = dbHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(USER_LOGIN)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            ResultSet result = statement.executeQuery();
            User existedUser = null;
            if (result.next()) {
                existedUser = new User();
                existedUser.setUsername(result.getString("name"));
            }
            return existedUser;
        }
    }


    private synchronized User isUserExisted(User user) throws SQLException {
        User existedUser = null;
        try (DBConnection dbHelper = DBConnection.getDBHelper();
             Connection connection = dbHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_USER_BY_USERNAME)) {
            statement.setString(1, user.getUsername());
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                existedUser = new User();
                existedUser.setUsername(results.getString("name"));
            }
            return existedUser;
        }
    }


    private synchronized boolean createUser(User user) throws SQLException {
        boolean rowUpdated = false;
        try (DBConnection dbHelper = DBConnection.getDBHelper();
             Connection connection = dbHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(USER_CREATE)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            rowUpdated = statement.executeUpdate() > 0;

            return rowUpdated;
        }
    }

    @Override
    public void run() {
        InputStream inputStream;
        OutputStream outputStream;
        try {
            inputStream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            ObjectOutputStream oss = new ObjectOutputStream(outputStream);
            Message message;

            while (true) {
                message = (Message) ois.readObject();
                Type action = message.getType();

                System.out.println("Action: " + action);
                switch (action) {
                    case LOGIN -> {
                        User user = (User) message.getT();
                        System.out.println("Inside login: " + user.toString());
                        User existedUser = checkLogin(user);
                        if (existedUser != null) {
                            System.out.println("Login successful!");
                            oss.writeUTF("Login succeed");
                        } else {
                            System.out.println("Fail to login!");
                            oss.writeUTF("Wrong username or password");
                        }
                        oss.flush();
                    }

                    case REGISTER -> {
                        User registerUser = (User) message.getT();
                        User isUserExisted = isUserExisted(registerUser);
                        if (isUserExisted == null) {
                            boolean isCreated = createUser(registerUser);
                            if (isCreated) {
                                System.out.println("Create user successful!");
                                oss.writeUTF("success");
                            } else {
                                System.out.println("Fail to create user!");
                                oss.writeUTF("fail");
                            }
                        } else {
                            oss.writeUTF("Username already existed");
                        }
                        oss.flush();
                    }
                    case ECHO -> {
                        String messageToEcho = (String) message.getT();
                        System.out.printf("Received: '%s'.\n", messageToEcho);
                        messageToEcho = handleMessageEcho(messageToEcho);
                        oss.writeObject(new Message<String>(Type.ECHO, messageToEcho));
                        oss.flush();
                    }
                }
                /*ois.close();
                clientSocket.close();
                System.out.println("Client " + clientSocket.getRemoteSocketAddress() + " disconnect from server...");*/
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }
}
