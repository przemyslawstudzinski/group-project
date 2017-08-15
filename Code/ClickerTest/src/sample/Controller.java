package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.ArrayList;

public class Controller {

    @FXML
    private Button replayButton;
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

    @FXML protected void handleReplayButton(ActionEvent event) throws InterruptedException, AWTException {
        ArrayList<CoordsContainer.Coords> coords = new ArrayList<>();
        for (CoordsContainer.Coords c : CoordsContainer.coordsList)
        {
            coords.add(c);
        }
        for (CoordsContainer.Coords c : coords)
        {
            click(c.x, c.y);
            //
            //
            //IMPORTANT TODO - HANDLE DOUBLECLICK (CURRENTLY 2000 MS DELAY PREVENTS US FROM RECORDING DOUBLECLICK)
            //
            //
            Thread.sleep(2000);
        }

    }
}
