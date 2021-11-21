package ua.nure.mykhnevych.task3;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server {
    private static ServerSocket clientsServerSocket;
    private static ServerSocket chatsServerSocket;
    private static final int CLIENTS_PORT_NUMBER = 4444;
    private static final int CHATS_PORT_NUMBER = 3333;
    private static LinkedList<ClientListener> clientsList = new LinkedList<>();
    private static volatile LinkedList<ChatListener> chatsList = new LinkedList<>();

    public static void main(String[] args) {
        Thread clientsListeningThread = new Thread() {
            public void run() {
                try {
                    clientsServerSocket = new ServerSocket(CLIENTS_PORT_NUMBER);
                    while (true) {
                        Socket clientSocket = clientsServerSocket.accept();
                        clientsList.add(new ClientListener(clientSocket, chatsList));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        clientsListeningThread.start();
        chatsListeningThread.start();
    }
}
