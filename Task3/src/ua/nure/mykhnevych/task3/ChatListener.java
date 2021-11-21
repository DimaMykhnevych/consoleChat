package ua.nure.mykhnevych.task3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatListener extends Thread {
    private Socket socket;
    public PrintWriter out;
    private BufferedReader in;
    private static final String SECRET_HASH = "098f6bcd";

    public ChatListener(Socket socket) throws IOException {
        this.socket = socket;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        start();
    }

    public void run() {
        String str = "";
        try {
            while (!str.equals("exit")) {
                str = in.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void onUserExit(String username) {
        out.println(SECRET_HASH + ":" + username);
    }
}
