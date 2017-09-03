import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client extends Thread {

    private static Socket socket;
    private BufferedReader input;
    private SystemHook mouseListener;

    Client() {
        try {
            socket = new Socket("127.0.0.1", 9090);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                String response = input.readLine();
                if (response != null) {
                    if (response.equals("record")) {
                        System.out.println(response);
                        mouseListener = new SystemHook(socket);
                        mouseListener.start();
                    }
                    if (response.equals("stoprecord")) {
                        System.out.println(response);
                        mouseListener.interrupt();
                    }

                    if (response.contains("replay")) {
                        System.out.println(response);
                    }
                }
            }
        } catch (IOException e) {
            return;
        } finally {
            try {
                System.out.println("ffffff");
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
