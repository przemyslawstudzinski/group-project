package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application {

    private static Image appIcon;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientWindow.fxml"));
        Parent root = loader.load();
        appIcon = new Image(getClass().getResourceAsStream("../resources/icon.png"));
        primaryStage.setTitle("Client");
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(appIcon);
        Scene scene = new Scene(root, 600, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
