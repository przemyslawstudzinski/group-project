package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Controller {


    private SystemHook mouseListener;
    private Clicker clicker;
    private Process lockerProcess = null;

    public Controller() throws AWTException {
        mouseListener = new SystemHook();
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
        Thread.sleep(1);
    }

    @FXML protected void handleReplayButton(ActionEvent event) throws InterruptedException, AWTException {
        mouseListener.interrupt();
        Clicker c = new Clicker();
        List<ClicksContainer.Click> clicksListCopy = new ArrayList<ClicksContainer.Click>();
        for (ClicksContainer.Click cl : ClicksContainer.clicksList)
        {
            clicksListCopy.add(cl);
        }

        for (ClicksContainer.Click clk : clicksListCopy)
        {
            Thread.sleep(clk.delay);
            c.click(clk);
        }

    }
}
