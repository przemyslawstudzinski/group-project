import lc.kra.system.mouse.GlobalMouseHook;
import lc.kra.system.mouse.event.GlobalMouseAdapter;
import lc.kra.system.mouse.event.GlobalMouseEvent;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SystemHook extends Thread {

    private static boolean run = true;
    private static Socket clientSocket;
    private long clickTime = 0;

    SystemHook(Socket s) {
        this.clientSocket = s;
    }

    public void attachMouseListener() {
        GlobalMouseHook mouseHook = new GlobalMouseHook();

        mouseHook.addMouseListener(new GlobalMouseAdapter() {
            @Override
            public void mousePressed(GlobalMouseEvent event) {
                if ((event.getButtons() & GlobalMouseEvent.BUTTON_LEFT) != GlobalMouseEvent.BUTTON_NO) {
                    int x = event.getX();
                    int y = event.getY();
                    Long delay = System.currentTimeMillis() - clickTime;
                    System.out.println("Recorded click: x: " + x + " y: " + y + " delay " + delay);
                    PrintWriter out = null;
                    try {
                        out = new PrintWriter(clientSocket.getOutputStream(), true);
                        out.println(x + " " + y + " " + delay);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    clickTime = System.currentTimeMillis();
                }
            }
        });

        try {
            while (run) Thread.sleep(10);
        } catch (InterruptedException e) {
        } finally {
            mouseHook.shutdownHook();
        }
    }

    public void run() {
        clickTime = System.currentTimeMillis();
        attachMouseListener();
    }
}