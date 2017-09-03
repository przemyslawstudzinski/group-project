import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server extends Thread {
    private static int counter;
    private ServerSocket listener;
    public ServerHandler activeHandler;             // communicate with specified client
    public ArrayList<ServerHandler> allHandlers;    // communicate with all clients

    Server() {
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
                ServerHandler handler = new ServerHandler(listener.accept(), counter);
                allHandlers.add(handler);
                activeHandler = handler;
                handler.start();

                counter++;
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
