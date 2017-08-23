package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Controller {

    private static Robot bot;
    private SystemHook mouseListener;
    private Process lockerProcess = null;

    public Controller() throws AWTException {
        bot = new Robot();
        mouseListener = new SystemHook();
    }

    public static void click(int x, int y) throws AWTException, InterruptedException {
        bot.mouseMove(x, y);
        bot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(10);
        bot.mouseRelease(InputEvent.BUTTON1_MASK);
        System.out.println("Replayed click: " + x + " " + y);
    }

    @FXML protected void lockKeyboard(ActionEvent event) throws InterruptedException, AWTException, IOException {
        ProcessBuilder lockerProcessBuilder = new ProcessBuilder("Dependencies\\Keyboard And Mouse Locker\\KeyFreeze_x64.exe");
        lockerProcess = lockerProcessBuilder.start();
    }

    @FXML protected void unlockKeyboard(ActionEvent event) throws InterruptedException, AWTException {
        if (lockerProcess != null)
            lockerProcess.destroy();
    }

    @FXML protected void handleRecordButton(ActionEvent event) throws InterruptedException, AWTException {
        mouseListener.start();
        Thread.sleep(50);
    }

    @FXML protected void handleReplayButton(ActionEvent event) throws InterruptedException, AWTException {
        mouseListener.interrupt();
        LinkedHashMap<CoordsContainer.Coords, Long> coordsMapCopy = new LinkedHashMap<CoordsContainer.Coords, Long>();
        for (Map.Entry<CoordsContainer.Coords, Long> entry : CoordsContainer.coordsMap.entrySet())
        {
            coordsMapCopy.put(entry.getKey(),entry.getValue());
        }

        for (Map.Entry<CoordsContainer.Coords, Long> entry : coordsMapCopy.entrySet())
        {
            click(entry.getKey().x, entry.getKey().y);
            Thread.sleep(entry.getValue());
        }

    }
}
