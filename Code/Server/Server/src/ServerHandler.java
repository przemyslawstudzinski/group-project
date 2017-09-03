import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ServerHandler extends Thread {
    private Socket socket;
    private BufferedReader input;
    private ArrayList<String> recordedClicks;

    ServerHandler(Socket s) {
        this.socket = s;
        this.recordedClicks = new ArrayList<>();
    }

    public Socket getSocket() {
        return this.socket;
    }

    public ArrayList<String> getRecordedClicks() {
        return recordedClicks;
    }

    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                try {
                    String response = input.readLine();
                    if (response != null) {
                        System.out.println(response);
                        // save recorded clicks
                        recordedClicks.add(response);
                    }
                } catch (IOException e) {
                    return;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("zamykam socket klienta");
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
