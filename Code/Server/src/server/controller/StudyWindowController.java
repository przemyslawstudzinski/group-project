package server.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import server.model.Receiver;
import server.model.Study;
import server.model.Time;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.*;

public class StudyWindowController implements Initializable, ChangeListener<Number> {

    @FXML
    private Label timeLabel;

    @FXML
    private Button stopStudyButton;

    private Time timeRemaining;

    private Thread timerThread;

    private Study study;

    public void setStudy(Study study) {
        this.study = study;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        timeRemaining = new Time(this);

        timerThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                Platform.runLater(() -> timeRemaining.addSeconds(1));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        timerThread.start();
    }

    @Override
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        timeLabel.setText(timeRemaining.toString());
    }

    private void unlockPeripherals() throws IOException {
        if (study.blockPeripherals) {
            for (Receiver receiver : study.blockedPeripheralsOnReceivers) {
                PrintWriter out = new PrintWriter(MainWindowController.server.connectedClientsMap.get(
                        receiver.getIpAddress()).getSocket().getOutputStream(), true);
                if (out != null) {
                    out.println("unlockMouseAndKeyboard");
                }
            }
        }
    }

    @FXML
    public void stopStudy() throws IOException {
        Stage stage = (Stage) stopStudyButton.getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        unlockPeripherals();
    }

    public void shutdown() {
        if (timerThread != null) {
            timerThread.interrupt();
            timerThread = null;
        }
    }
}
