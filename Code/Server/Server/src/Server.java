import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server extends Thread {
    private ServerSocket listener;
    public ServerHandler activeHandler;             // communicate with specified client
    public ArrayList<ServerHandler> allHandlers;    // communicate with all clients

    Server() {
        try {
            listener = new ServerSocket(9090);
            allHandlers = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                ServerHandler handler = new ServerHandler(listener.accept());
                allHandlers.add(handler);
                activeHandler = handler;
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                listener.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
