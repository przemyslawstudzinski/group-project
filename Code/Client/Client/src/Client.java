import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client extends Thread {

    private static Socket s;
    private BufferedReader input;
    private SystemHook mouseListener;

    Client() {
        try {
            s = new Socket("127.0.0.1", 9090);
            input = new BufferedReader(new InputStreamReader(s.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while(true){
            try {
                String response = input.readLine();
                if (response != null) {
                    if (response.equals("record")) {
                        System.out.println(response);
                        mouseListener = new SystemHook();
                        mouseListener.start();
                    }
                    if (response.equals("stoprecord")) {
                        System.out.println(response);
                        // wyślij koordy które zebrał do serwera
                        try {
                            mouseListener.printcoords();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mouseListener.interrupt();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
