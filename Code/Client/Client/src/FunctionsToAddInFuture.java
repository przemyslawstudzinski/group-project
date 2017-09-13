import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.awt.*;
import java.io.File;
import java.io.IOException;


public class FunctionsToAddInFuture {


    private SystemHook mouseListener;
    private Clicker clicker;
    private Process lockerProcess = null;

    @FXML protected void lockKeyboard(ActionEvent event) throws InterruptedException, AWTException, IOException {
        ProcessBuilder lockerProcessBuilder = new ProcessBuilder("Dependencies" + File.separator + "Keyboard And Mouse Locker" + File.separator + "KeyFreeze_x64.exe");
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
        c.click(0,0,false);
    }
}
