import java.io.IOException;
import java.net.ServerSocket;

public class Server extends Thread {
    private ServerSocket listener;
    public ServerHandler activeHandler;

    Server() {
        try {
            listener = new ServerSocket(9090);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                ServerHandler handler = new ServerHandler(listener.accept());
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
