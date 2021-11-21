package ua.nure.mykhnevych.task3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatInstance {
    private Socket socket;
    public PrintWriter out;
    public BufferedReader in;
    private String hostName;
    private int port;
    public String clientUsername;
    private static final String SECRET_HASH = "098f6bcd";
    private boolean shouldShutDown = false;

    public ChatInstance(String hostName, int port, String clientUsername) {
        this.hostName = hostName;
        this.port = port;
        this.clientUsername = clientUsername;
        try {
            socket = new Socket(hostName, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            while (!shouldShutDown) {
                String str = in.readLine();
                if (checkForExit(str)) {
                    out.println("exit");
                    break;
                }
                if (shouldOutputMessage(str)) {
                    System.out.println(str);
                }
            }
        } catch (UnknownHostException e) {
            System.out.println("Invalid host");
        } catch (IOException e) {
            System.out.println("Server is unavailable");
        }
    }

    private boolean shouldOutputMessage(String line) {
        if (line == null) {
            shouldShutDown = true;
            return false;
        }
        String[] splited = line.split(":");
        if (splited.length == 2 && splited[0].equals(SECRET_HASH)) {
            return false;
        }
        return true;
    }

    private boolean checkForExit(String line) {
        if (line == null) {
            shouldShutDown = true;
            return false;
        }
        String[] splited = line.split(":");
        if (splited.length != 2) {
            return false;
        }
        if (splited[0].equals(SECRET_HASH) && splited[1].equals(clientUsername)) {
            return true;
        }
        return false;
    }
}
