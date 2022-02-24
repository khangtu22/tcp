package fontend;

import models.Message;
import models.Type;
import models.User;

public class Main {
    public static void main(String[] args) {
        User user = new User();
        Message<User> message = new Message<>(Type.ECHO, user);
    }
}
