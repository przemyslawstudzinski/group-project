package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class Server extends Thread {
    private ServerSocket listener;
    public ClientHandler client;                    // communicate with specified client
    public ArrayList<ClientHandler> allHandlers;    // communicate with all clients
    public boolean running = true;

    // in this thread we are waiting for clients to connect
    private Thread serverListener = new Thread() {
        public void run() {
            try {
                while(true) {
                    ClientHandler handler = new ClientHandler(listener.accept());
                    allHandlers.add(handler);
                    client = handler;
                    handler.start();
                }
            } catch(SocketException e) {
                System.out.println("Zamykam socket serverlistenera");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public Server() {
        try {
            listener = new ServerSocket(9090);
            allHandlers = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            serverListener.start();
            // run server as long as gui window won't be closed
            while (running) {
                Thread.sleep(0);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (!listener.isClosed())
                for (ClientHandler handler : allHandlers) {
                    handler.running = false;
                }
                System.out.println("zamykam serwer");
                try {
                    listener.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }
    }
}