package gui.utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class Server extends Thread {
    private ServerSocket listener;
    public Map<String, ClientHandler> connectedClientsMap = new HashMap();
    public boolean running = true;

    // in this thread we are waiting for clients to connect
    private Thread serverListener = new Thread() {
        public void run() {
            try {
                while (true) {
                    ClientHandler handler = new ClientHandler(listener.accept());
                    String clientIP = handler.getSocket().getRemoteSocketAddress().toString().replaceAll("/","").split(":")[0];
                    connectedClientsMap.put(clientIP, handler);
                    handler.start();
                }
            } catch (SocketException e) {
                System.out.println("Closing serverlistener socket");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public Server() {
        try {
            listener = new ServerSocket(9090);
            //allHandlers = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            serverListener.start();
            // run server as long as gui window won't be closed
            while (running) {
                Thread.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (!listener.isClosed())
                for (ClientHandler handler : connectedClientsMap.values()) {
                    handler.running = false;
                }
            System.out.println("Closing server");
            try {
                listener.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}