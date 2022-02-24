package models;

import java.io.Serial;
import java.io.Serializable;
import java.util.Scanner;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = -6500665823330706018L;
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                '}';
    }

    public void inputUser() {
        Scanner myObj = new Scanner(System.in);
        System.out.println("Enter username");
        this.username = myObj.nextLine();
        System.out.println("Enter password:");
        this.password = myObj.nextLine();
    }
}


