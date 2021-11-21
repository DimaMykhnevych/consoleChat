package ua.nure.mykhnevych.task3;

public class Chat {
    private static final String HOST_NAME = "localhost";
    private static final int PORT_NUMBER = 3333;

    public static void main(String[] args) {
        new ChatInstance(HOST_NAME, PORT_NUMBER, args[0]);
    }
}
