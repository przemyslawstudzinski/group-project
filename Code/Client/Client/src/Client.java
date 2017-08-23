import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client extends Thread {

    private static Socket s;
    private BufferedReader input;

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
                if (response != null && response.equals("record")) {
                    System.out.println(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
