package ua.nure.mykhnevych.task3;

import ua.nure.mykhnevych.task3.Respnonses.AuthResponse;
import ua.nure.mykhnevych.task3.Utils.SerializationHelper;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Logger;

public class ClientListener extends Thread {
    public Socket socket;
    private LinkedList<ChatListener> chats;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isLoggedIn;
    private static final String PATH_TO_CREDENTIALS = "users.properties";
    private String username;
    public static final Logger logger = Logger.getLogger(
            ClientListener.class.getName());

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
            logger.info(username + " successfully authorized");
            startChat();
        } catch (IOException e) {
            logger.info("Socket for client " +username + " was closed");
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
                    logger.info(username + " exited chat");
                    break;
                }
                for (ChatListener chat : chats) {
                    chat.sendMessage(username + ": " + info);
                }
                logger.info("Sending following info to the chats: " + info);
                out.println();
            }
        } catch (IOException e) {
            logger.info("Socket for client " +username + " was closed");
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
        logger.info("Opening chat for client " + username);
        try {
            Runtime.getRuntime().exec(
                    new String[]
                            {"cmd.exe", "/c", "start \"\" createChat.bat",
                                    username
                            });
        } catch (IOException e) {
            logger.warning("Failed to open chat for " + username);
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
            logger.warning("File with user credentials wasn't found");
        } catch (IOException e) {
            logger.info("Socket for client " +username + " was closed");
        }
        return false;
    }
}
