package models;

import java.io.Serial;
import java.io.Serializable;
import java.util.Scanner;

public class Message<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -6500665823330706018L;
    private Type type;
    private T t;

    public Message() {
    }

    public Message(Type type, T t) {
        this.type = type;
        this.t = t;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}
