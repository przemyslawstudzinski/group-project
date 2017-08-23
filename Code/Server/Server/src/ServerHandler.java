import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerHandler extends Thread {
    private Socket socket;

    ServerHandler(Socket s) {
        this.socket = s;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public void run() {
        while (true) {

        }
    }
}
