package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Main extends Application {

    private static Controller controller;
    private static Image appIcon;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        appIcon = new Image(getClass().getResourceAsStream("Images" + File.separator + "icon.png"));
        primaryStage.setTitle("Serwer");
        primaryStage.getIcons().add(appIcon);
        primaryStage.setScene(new Scene(root, 800, 700));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            try {
                controller.shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
