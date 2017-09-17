import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Client extends Thread {

    private Socket socket;
    private BufferedReader input;
    private SystemHook mouseListener;
    private boolean running = true;
    private Process lockerProcess = null;
    private boolean closeSystem = false;
    private String serverIP = "";
    private static final String configDirectory = "Config" + File.separator;
    private static final String serverIPFilename = configDirectory + File.separator + "server_ip.ini";

    private void lockPeripherals(boolean lockKeyboard, boolean lockMouseAndKeyboard) throws InterruptedException, AWTException, IOException {
        String lockerPath = "Dependencies" + File.separator + "Keyboard And Mouse Locker" + File.separator;
        ProcessBuilder lockerProcessBuilder = new ProcessBuilder(lockerPath + "KeyFreeze_x64.exe");
        File configSource = null;
        if (lockKeyboard)
            configSource = new File(lockerPath + "BlockKeyboard.ini");
        else if (lockMouseAndKeyboard)
            configSource = new File(lockerPath + "BlockMouseAndKeyboard.ini");
        File configDestination = new File(lockerPath + "KeyFreeze.ini");
        if (configDestination.exists())
            configDestination.delete();
        FileUtils.copyFile(configSource, configDestination);
        lockerProcess = lockerProcessBuilder.start();
    }

    private void unlockPeripherals() throws InterruptedException, AWTException {
        if (lockerProcess != null)
            lockerProcess.destroy();
    }

    private void replayAction() throws IOException, AWTException, InterruptedException {
        Clicker clicker = new Clicker();
        while (true) {
            String response = input.readLine();
            if (response.equals("lockKeyboard"))
                lockPeripherals(/*lockKeyboard*/ true, /*lockMouseAndKeyboard*/ false);
            else if (response.equals("closeSystem"))
                closeSystem = true;
            else if (response.equals("stopreplay")) {
                System.out.println(response);
                unlockPeripherals();
                return;
            } else if (response.contains("click")) {
                String[] data = response.split(" ");
                clicker.click(Integer.parseInt(data[1]), Integer.parseInt(data[2]), Boolean.parseBoolean(data[3]));
                Thread.sleep(Long.parseLong(data[4]));
            }
        }
    }


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
                        if (response.equals("replay")) {
                            System.out.println(response);
                            replayAction();
                        }
                        if (response.equals("lockMouseAndKeyboard")) {
                            System.out.println(response);
                            lockPeripherals(/*lockKeyboard*/ false, /*lockMouseAndKeyboard*/ true);
                        }
                    }
                }
            } catch (SocketException e) {
                System.out.println("Zamykam socket serverlistenera");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AWTException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
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

    private void loadConfigFiles() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(serverIPFilename), Charset.forName("UTF-8"));
            serverIP = lines.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Client() {
        try {
            loadConfigFiles();
            socket = new Socket(serverIP, 9090);
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
                    System.out.println("Closing client");
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
