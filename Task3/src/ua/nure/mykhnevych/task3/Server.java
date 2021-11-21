package ua.nure.mykhnevych.task3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Logger;

public class Server {
    private static ServerSocket clientsServerSocket;
    private static ServerSocket chatsServerSocket;
    private static ServerSocket adminServerSocket;
    private static final int CLIENTS_PORT_NUMBER = 4444;
    private static final int CHATS_PORT_NUMBER = 3333;
    private static final int ADMIN_PORT_NUMBER = 2222;
    private static LinkedList<ClientListener> clientsList = new LinkedList<>();
    private static LinkedList<ChatListener> chatsList = new LinkedList<>();
    public static final Logger logger = Logger.getLogger(
            Server.class.getName());

    public static void main(String[] args) throws InterruptedException {
        Thread clientsListeningThread = new Thread() {
            public void run() {
                try {
                    clientsServerSocket = new ServerSocket(CLIENTS_PORT_NUMBER);
                    while (true) {
                        Socket clientSocket = clientsServerSocket.accept();
                        clientsList.add(new ClientListener(clientSocket, chatsList));
                        logger.info("Client was added");
                    }
                } catch (IOException e) {
                    logger.info("Client server socket was closed");
                }
            }
        };
        Thread chatsListeningThread = new Thread() {
            public void run() {
                try {
                    chatsServerSocket = new ServerSocket(CHATS_PORT_NUMBER);
                    while (true) {
                        Socket chatSocket = chatsServerSocket.accept();
                        chatsList.add(new ChatListener(chatSocket));
                        logger.info("Chat was added");
                    }
                } catch (IOException e) {
                    logger.info("Chat server socket was closed");
                }
            }
        };
        Thread adminListeningThread = new Thread() {
            public void run() {
                try {
                    adminServerSocket = new ServerSocket(ADMIN_PORT_NUMBER);
                    while (true) {
                        Socket adminSocket = adminServerSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(adminSocket.getInputStream()));
                        String info = in.readLine();
                        if(info.equals("stop")) {
                            for (ChatListener chatListener : chatsList) {
                                chatListener.sendMessage("Server is stopped");
                                chatListener.socket.close();
                            }
                            for(ClientListener clientListener : clientsList){
                                clientListener.socket.close();
                            }
                            clientsServerSocket.close();
                            chatsServerSocket.close();
                            adminServerSocket.close();
                            break;
                        }
                    }
                } catch (IOException e) {
                    logger.info("Admin server socket was closed");
                }
            }
        };
        clientsListeningThread.start();
        chatsListeningThread.start();
        adminListeningThread.start();

        clientsListeningThread.join();
        chatsListeningThread.join();
        adminListeningThread.join();
    }
}
