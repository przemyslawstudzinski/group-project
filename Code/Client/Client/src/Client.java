import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.ServerError;
import java.rmi.ServerException;

public class Client extends Thread {

    private Socket socket;
    private BufferedReader input;
    private SystemHook mouseListener;
    private boolean running = true;

    private Thread serverListener = new Thread() {
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
            } catch (SocketException e) {
                System.out.println("Zamykam socket serverlistenera");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private Thread checkIfAlive = new Thread() {
        public void run() {
            try {
                while (true) {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("check");
                }
            } catch (IOException e) {
                running = false;
            } catch (NullPointerException ex) {
                running = false;
            }
        }
    };

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
            checkIfAlive.start();
            serverListener.start();
            while (running) {
                Thread.sleep(0);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (!socket.isClosed()) {
                try {
                    System.out.println("zamykam klienta");
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
