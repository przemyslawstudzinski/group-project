package server.utils;

import java.io.Console;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class Server extends Thread {

    private ServerSocket listener;

    public static Map<String, ClientHandler> connectedClientsMap = new HashMap();

    public boolean running = true;

    private OutputConsole outputConsole;

    public Server(OutputConsole console) {
        try {
            this.outputConsole = console;
            listener = new ServerSocket(9090);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            new Thread(new Runnable() {
                private OutputConsole console;

                public Runnable init(OutputConsole c) {
                    this.console = c;
                    return this;
                }

                @Override
                public void run() {
                    try {
                        while (true) {
                            ClientHandler handler = new ClientHandler(listener.accept(), outputConsole);
                            String clientIP = handler.getSocket().getRemoteSocketAddress().toString().replaceAll("/", "").split(":")[0];
                            handler.setIP(clientIP);
                            connectedClientsMap.put(clientIP, handler);
                            handler.start();
                            com.sun.javafx.application.PlatformImpl.startup(new Runnable() {
                                public void run() {
                                    console.writeLine("Klient o adresie IP " + clientIP + " właśnie podłączył się do serwera!");
                                }
                            });
                        }
                    } catch (SocketException e) {
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.init(outputConsole)).start();

            // run server as long as server window won't be closed
            while (running)
                Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (!listener.isClosed())
                for (ClientHandler handler : connectedClientsMap.values()) {
                    handler.running = false;
                }
            try {
                listener.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}