package server;

import server.controller.Controller;
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/MainWindow.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        appIcon = new Image(getClass().getResourceAsStream("view/Images" + File.separator + "icon.png"));
        primaryStage.setTitle("Serwer");
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(appIcon);
        Scene scene = new Scene(root, 800, 700);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("server/view/style.css").toExternalForm());
        primaryStage.setScene(scene);
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
