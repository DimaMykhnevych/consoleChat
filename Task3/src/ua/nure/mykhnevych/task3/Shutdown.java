package ua.nure.mykhnevych.task3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Shutdown {
    private static final String HOST_NAME = "localhost";
    private static final int PORT_NUMBER = 2222;
    private static Socket socket;
    public static PrintWriter out;
    private static BufferedReader in;
    private static BufferedReader reader;


    public static void main(String[] args) throws IOException {
        socket = new Socket(HOST_NAME, PORT_NUMBER);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter `stop` to stop the server");
        String word = reader.readLine();
        while (!word.equals("stop")){
            word = reader.readLine();
        }
        out.println("stop");
    }
}
