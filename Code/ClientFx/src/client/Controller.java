package client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private static final String connectTrue = "Połączono z serwerem.";
    private static final String connectFalse = "Brak połączenia z serwerem.";

    private static final String configDirectory = "config" + File.separator;

    private static final String serverIPFilename = configDirectory + File.separator + "server_ip.ini";

    private String serverIP = "";

    @FXML
    private Button connectButton;

    @FXML
    private TextField ipTextField;

    @FXML
    private Label connectionLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadConfigFiles();

        ipTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("(\\d{3}\\.){4}")) {
                ipTextField.setText(newValue.replaceAll("[^(\\d\\.){4}]", ""));
            }
        });
    }

    public void connect()   {
        if (!ipTextField.getText().equalsIgnoreCase(serverIP)) {
            serverIP = ipTextField.getText();
            try {
                Files.write(Paths.get(serverIPFilename), serverIP.getBytes(), StandardOpenOption.WRITE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Client client = null;
        try {
            client = new Client(serverIP);
        } catch (IOException e) {
            connectionLabel.setText(connectFalse);
            connectionLabel.setTextFill(Color.valueOf("RED"));
            e.printStackTrace();
        }

        client.start();

        if (client.isConnected()) {
            connectionLabel.setText(connectTrue);
            connectionLabel.setTextFill(Color.valueOf("GREEN"));
        }
    }

    private void loadConfigFiles() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(serverIPFilename), Charset.forName("UTF-8"));
            serverIP = lines.get(0);
            if (serverIP != "") {
                ipTextField.setText(serverIP);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
