package server.utils;

import javafx.scene.image.Image;
import server.controller.StudyWindowController;
import server.model.Study;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StudyThread implements Runnable {

    private static StudyWindowController studyController;

    private final Study study;

    private Stage stage;

    public OutputConsole console;

    public static Boolean canRunStudy;

    public StudyThread(Study study, Stage stage, OutputConsole console) {
        this.study = study;
        this.stage = stage;
        this.console = console;
        canRunStudy = true;
    }

    private void closeCurrentWindow() {
        stage.hide();
    }

    @Override
    public void run() {

                try {
                    study.setStartTime(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
                    study.runThisStudy(console);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (canRunStudy) {
                    closeCurrentWindow();
                    createStudyWindow(study, console);
                }
                canRunStudy = true;
    }



    public void createStudyWindow(Study study, OutputConsole console) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    ".." + File.separator + "view" + File.separator + "StudyWindow.fxml"));
            Parent root = loader.load();
            Image appIcon = new Image(getClass().getResourceAsStream(
                    ".." + File.separator + "view" + File.separator + "Images" + File.separator + "icon.png"));
            studyController = loader.getController();
            studyController.setStudy(study);
            Stage newStage = new Stage();
            newStage.setTitle("Badanie");
            newStage.setResizable(false);
            newStage.getIcons().add(appIcon);
            newStage.setScene(new Scene(root, 600, 340));
            newStage.show();
            newStage.setOnCloseRequest(event -> {
                newStage.close();
                studyController.shutdown();
                stage.show();
                console.writeLine("Zakończono badanie!");
                study.setEndTime(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
                study.saveLog();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
