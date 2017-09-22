package gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.Server;

import java.io.IOException;

public class StudyThread implements Runnable {
    private final Study study;
    private Server server;
    private Stage stage;

    public StudyThread(Study study, Server server, Stage stage) {
        this.study = study;
        this.server = server;
        this.stage = stage;
    }

    @Override
    public void run() {
        try {
            study.runThisStudy(server);
            createStudyWindow();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createStudyWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StudyWindow.fxml"));
            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setTitle("Study");
            newStage.setScene(new Scene(root, 600, 340));
            newStage.show();

            newStage.setOnCloseRequest(event -> {
                    newStage.hide();
                    stage.show();
            });
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
