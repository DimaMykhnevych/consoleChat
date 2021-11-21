package ua.nure.mykhnevych.task3;

public class Client {
    private static final String HOST_NAME = "localhost";
    private static final int PORT_NUMBER = 4444;

    public static void main(String[] args) throws Exception {
        new ClientInstance(HOST_NAME, PORT_NUMBER);
    }
}
