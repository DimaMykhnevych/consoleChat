package ua.nure.mykhnevych.task3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class ChatListener extends Thread {
    public Socket socket;
    public PrintWriter out;
    private BufferedReader in;
    private static final String SECRET_HASH = "098f6bcd";
    public static final Logger logger = Logger.getLogger(
            ChatListener.class.getName());

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
            logger.info("Chat socket was closed");
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void onUserExit(String username) {
        out.println(SECRET_HASH + ":" + username);
    }
}
