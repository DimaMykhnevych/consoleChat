package ua.nure.mykhnevych.task3;

import ua.nure.mykhnevych.task3.Respnonses.AuthResponse;
import ua.nure.mykhnevych.task3.Utils.SerializationHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientInstance {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader reader;
    private String hostName;
    private int port;
    private boolean isLoggedIn;

    public ClientInstance(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
        try {
            socket = new Socket(hostName, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            reader = new BufferedReader(new InputStreamReader(System.in));
            startChatting();
        } catch (UnknownHostException e) {
            System.out.println("Invalid host");
        } catch (IOException e) {
            System.out.println("Server is unavailable");
        }
    }

    private void startChatting() throws IOException {
        authenticate();
        chat();
    }

    private void authenticate() throws IOException {
        System.out.println("Enter `exit` to exit the program");
        System.out.println("Please, enter credentials (username:password):");
        while (!isLoggedIn) {
            String word = reader.readLine();
            out.println(word);
            String answer = in.readLine();
            AuthResponse response = null;
            try {
                response = (AuthResponse) SerializationHelper.deserializeObject(answer);
            } catch (Exception e) {
                e.printStackTrace();
            }
            outputAuthResponse(response);
            isLoggedIn = response.isAuthenticated;
        }
    }

    private void chat() throws IOException {
        String answer = "";
        while (!answer.equals("exit")){
            System.out.print("Enter the message: ");
            String word = reader.readLine();
            out.println(word);
            answer = in.readLine();
        }
        System.out.println("Application was finished successfully");
    }

    private void outputAuthResponse(AuthResponse response) {
        if (response.isAuthenticated) {
            System.out.println("You are successfully logged in! Happy chatting");
        } else {
            System.out.println("Credentials are invalid, try again");
        }
    }
}
