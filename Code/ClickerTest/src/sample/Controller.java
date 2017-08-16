package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Controller {

    @FXML
    private Button recordButton;
    @FXML
    private Button replayButton;
    @FXML
    private static Robot bot;

    public Controller() throws AWTException {
        bot = new Robot();
    }

    public static void click(int x, int y) throws AWTException, InterruptedException {
        bot.mouseMove(x, y);
        bot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(10);
        bot.mouseRelease(InputEvent.BUTTON1_MASK);
        System.out.println("Replay: " + x + " " + y);
    }

    @FXML protected void handleRecordButton(ActionEvent event) throws InterruptedException, AWTException {
        SystemHook lis = new SystemHook();
        lis.start();
        Thread.sleep(50);
    }

    @FXML protected void handleReplayButton(ActionEvent event) throws InterruptedException, AWTException {
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
