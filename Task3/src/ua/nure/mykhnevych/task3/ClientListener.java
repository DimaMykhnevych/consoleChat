package ua.nure.mykhnevych.task3;

import ua.nure.mykhnevych.task3.Respnonses.AuthResponse;
import ua.nure.mykhnevych.task3.Utils.SerializationHelper;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Properties;

public class ClientListener extends Thread {
    private Socket socket;
    private LinkedList<ChatListener> chats;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isLoggedIn;
    private static final String PATH_TO_CREDENTIALS = "users.properties";
    private String username;

    public ClientListener(Socket socket, LinkedList<ChatListener> chats) throws IOException {
        this.socket = socket;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.chats = chats;
        start();
    }

    public void run() {
        String info;
        try {
            while (!isLoggedIn) {
                info = in.readLine();
                boolean authenticated = authenticateClient(info);
                AuthResponse response = new AuthResponse();
                response.isAuthenticated = authenticated;
                out.println(SerializationHelper.serializeObject(response));
            }
            startChat();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startChat() {
        String info;
        openChatForClient();
        try {
            while (true) {
                info = in.readLine();
                if (info.equals("exit")) {
                    onClientExit();
                    break;
                }
                for (ChatListener chat : chats) {
                    chat.sendMessage(username + ": " + info);
                }
                out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onClientExit() {
        for (ChatListener chat : chats) {
            chat.sendMessage(username + " left the chat");
            chat.onUserExit(username);
        }
        out.println("exit");
    }

    private void openChatForClient() {
        try {
            Runtime.getRuntime().exec(
                    new String[]
                            {"cmd.exe", "/c", "start \"\" createChat.bat",
                                    username
                            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean authenticateClient(String credentials) {
        String[] splitCredentials = credentials.split(":");
        if (splitCredentials.length != 2) {
            return false;
        }
        String username = splitCredentials[0].trim();
        String password = splitCredentials[1].trim();
        try (FileInputStream propFile = new FileInputStream(PATH_TO_CREDENTIALS)) {
            Properties prop = new Properties();
            prop.load(propFile);
            String storedPassword = prop.getOrDefault(username, "").toString();
            if (storedPassword != "") {
                isLoggedIn = storedPassword.equals(password);
                this.username = username;
                return storedPassword.equals(password);
            }
            return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
