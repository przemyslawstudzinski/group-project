package server;

import guiproject.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader input;
    private ArrayList<Node> recordedClicks;

    ClientHandler(Socket s) {
        this.socket = s;
        this.recordedClicks = new ArrayList<>();
    }

    public Socket getSocket() {
        return this.socket;
    }

    public ArrayList<Node> getRecordedClicks() {
        return this.recordedClicks;
    }

    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                try {
                    String response = input.readLine();
                    if (response != null) {
                        // save recorded clicks
                        String[] data = response.split(" ");
                        Node node = new Node(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
                        recordedClicks.add(node);
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