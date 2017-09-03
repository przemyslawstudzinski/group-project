import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Controller {

    private static Robot bot;
    private static Server server;
    private Process lockerProcess;

    public Controller() throws AWTException {
        bot = new Robot();
        server = new Server();
        lockerProcess = null;

        server.start();
    }

    public static void click(int x, int y) throws AWTException, InterruptedException {
        bot.mouseMove(x, y);
        bot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(10);
        bot.mouseRelease(InputEvent.BUTTON1_MASK);
        System.out.println("Replayed click: " + x + " " + y);
    }

    @FXML protected void lockKeyboard(ActionEvent event) throws InterruptedException, AWTException, IOException {
        ProcessBuilder lockerProcessBuilder = new ProcessBuilder("Dependencies" + File.separator + "Keyboard And Mouse Locker" + File.separator + "KeyFreeze_x64.exe");
        lockerProcess = lockerProcessBuilder.start();
    }

    @FXML protected void unlockKeyboard(ActionEvent event) throws InterruptedException, AWTException {
        if (lockerProcess != null)
            lockerProcess.destroy();
    }

    @FXML protected void handleRecordButton(ActionEvent event) throws InterruptedException, IOException {
        //mouseListener.start();
        // server informs client about start of recording
        PrintWriter out = new PrintWriter(server.activeHandler.getSocket().getOutputStream(), true);
        out.println("record");
        Thread.sleep(50);
        server.activeHandler.getSerializer().createDoc();
    }

    @FXML protected void handleStopButton(ActionEvent event) throws IOException, InterruptedException {
        // co jak będzie na tym samym kompie? wtedy mouselistener klienta załapie też button z serwera (replay clicks)
        PrintWriter out = new PrintWriter(server.activeHandler.getSocket().getOutputStream(), true);
        out.println("stoprecord");
        Thread.sleep(50);
        server.activeHandler.getSerializer().saveFile();
    }

    @FXML protected void handleReplayButton(ActionEvent event) throws InterruptedException, IOException {
        // start scenario; send data to specified clients
        // now we send to all clients, in future clients will be specified in gui
        for (ServerHandler sh : server.allHandlers) {
            PrintWriter out = new PrintWriter(sh.getSocket().getOutputStream(), true);
            for (String s : sh.getRecordedClicks()) {
                out.println("replay " + s);
            }
            Thread.sleep(50);
        }
    }
}
