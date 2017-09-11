package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server extends Thread {
    private static int counter;
    private ServerSocket listener;
    public ClientHandler client;                    // communicate with specified client
    public ArrayList<ClientHandler> allHandlers;    // communicate with all clients

    public Server() {
        try {
            counter = 1;
            listener = new ServerSocket(9090);
            allHandlers = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                ClientHandler handler = new ClientHandler(listener.accept());
                allHandlers.add(handler);
                client = handler;
                handler.start();

                counter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (listener != null)
                try {
                    listener.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
    }
}