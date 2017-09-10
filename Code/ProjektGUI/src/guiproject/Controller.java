package guiproject;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.ToggleSwitch;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Controller implements Initializable {

    @FXML
    private ListView<String> allScenariosListView;

    @FXML
    private ListView<String> allActionsListView;

    @FXML
    private ListView<String> chosenActionsListView;

    @FXML
    private ComboBox<String> receiversComboBox;

    @FXML
    private TextField ageTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private ComboBox<String> teachersComboBox;

    @FXML
    private TextField scenarioNameTextField;

    @FXML
    private TextField actionNameTextField;

    @FXML
    private TextArea actionDescriptionTextArea;

    @FXML
    private ToggleSwitch blockPeripheralsSwitch;

    @FXML
    private CheckComboBox<String> receiversToBlockMultiComboBox;

    private final ObservableList<String> allActions
            = FXCollections.observableArrayList();

    private final ObservableList<String> allTeachers
            = FXCollections.observableArrayList();

    private final ObservableList<String> chosenActions
            = FXCollections.observableArrayList();

    private static final String fileNameOfReceivers = "./Config/receivers.ini";
    private static final String fileNameOfTeachers = "./Config/teachers.ini";

    private final Map<String, String> receivers = new HashMap<>();
    private final Map<String, File> availableActions = new HashMap();
    private final Map<String, File> availableScenarios = new HashMap();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            //load Receivers and Teachers from .ini file and add them to receivers comboBox
            //load Actions from XML files
            //load Scenarios fro XML file
            loadFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
        allActions.addAll(availableActions.keySet());
        allActionsListView.setItems(allActions);
        allScenariosListView.getItems().addAll(availableScenarios.keySet());
        chosenActionsListView.setItems(chosenActions);
        receiversToBlockMultiComboBox.getItems().addAll(receivers.keySet());
        receiversToBlockMultiComboBox.setDisable(true);
        //enable/disable possibility to block keyboard/mouse input on several receviers
        blockPeripheralsSwitch.selectedProperty().addListener(new ChangeListener< Boolean >() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue)
                    receiversToBlockMultiComboBox.setDisable(false);
                else
                    receiversToBlockMultiComboBox.setDisable(true);
                //get IP's of receivers which should have blocked mouse/keyboard during scenario execution
                for (String key : receiversToBlockMultiComboBox.getCheckModel().getCheckedItems())
                    System.out.println(receivers.get(key));
                //get IP of receiver on which we want to record the action
                System.out.println(receivers.get(receiversComboBox.getSelectionModel().getSelectedItem()));
                //get action Files that we want to run
                for (String key : chosenActionsListView.getItems())
                    System.out.println(availableActions.get(key));
                //get scenario Files that we want to run
                for (String key : allScenariosListView.getSelectionModel().getSelectedItems())
                    System.out.println(availableScenarios.get(key));
            }

        });
        
        //teachers ComboBox
        teachersComboBox.setItems(allTeachers);
        teachersComboBox.getSelectionModel().selectFirst();
        //name Text Field
        validateTextField(nameTextField,"textOnly");
        //last name Text Field
        validateTextField(lastNameTextField, "textOnly");
        //age Text Field
        validateTextField(ageTextField, "numberOnly");
        //scenario Name Text Field
        validateTextField(scenarioNameTextField, "filename");
        //action Name Text Field
        validateTextField(actionNameTextField, "filename");
    }


    void validateTextField(TextField field, String validationType)
    {
        if (validationType.equals("textOnly"))
        {
            field.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\sa-zA-Z*")) {
                    field.setText(newValue.replaceAll("[^\\sa-zA-Z]", ""));
                }
            });
        }
        else if (validationType.equals("numberOnly"))
        {
            field.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    field.setText(newValue.replaceAll("[^\\d]", ""));
                } else if (newValue.length() > 3) {
                    String tmp = newValue.substring(0, 3);
                    field.setText(tmp);
                } else if (!newValue.matches("")) {
                    if (Integer.valueOf(newValue) > 199) {
                        String tmp = newValue.substring(0, 2);
                        field.setText(tmp);
                    }
                }
            });
        }
        else if (validationType.equals("filename"))
        {
            field.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("[-_. A-Za-z0-9]")) {
                    field.setText(newValue.replaceAll("[^-_. A-Za-z0-9]", ""));
                }
            });
        }
    }

    private void loadXMLs(String path, Map<String,File> map) throws IOException {
        List<File> actionsFiles = Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());
        for (File f : actionsFiles)
            map.putIfAbsent(f.getName(), f);
    }

    private void loadFiles() throws IOException {
        //set Receivers
        try {
            Stream<String> lines = Files.lines(Paths.get(fileNameOfReceivers));
            lines.filter(line -> line.contains(" ")).forEach(line -> receivers.putIfAbsent(line.split(" ")[1], line.split(" ")[0]));
            ObservableList<String> receiversList = FXCollections.observableArrayList(receivers.keySet());
            receiversComboBox.setItems(receiversList);
            receiversComboBox.getSelectionModel().selectFirst();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //set Teachers
        try {
            Stream<String> stream = Files.lines(Paths.get(fileNameOfTeachers));
            stream.forEach(line -> allTeachers.add(line));
            teachersComboBox.setItems(allTeachers);
            teachersComboBox.getSelectionModel().selectFirst();
        } catch (IOException e) {
            e.printStackTrace();
        }

            //set Actions
            loadXMLs("Actions/", availableActions);
            //set Scenarios
            loadXMLs("Scenarios/", availableScenarios);
    }

    @FXML
    void moveActionToRight(ActionEvent event) {
        if (allActionsListView.getSelectionModel().getSelectedItem() != null) {
            chosenActions.add(allActionsListView.getSelectionModel().getSelectedItem());
            allActions.remove(allActionsListView.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    void moveActionToLeft(ActionEvent event) {
        if (chosenActionsListView.getSelectionModel().getSelectedItem() != null) {
            allActions.add(chosenActionsListView.getSelectionModel().getSelectedItem());
            chosenActions.remove(chosenActionsListView.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    void moveActionToUp(ActionEvent event) {
        if (chosenActionsListView.getSelectionModel().getSelectedItem() != null) {
            int index = chosenActions.indexOf(chosenActionsListView.getSelectionModel().getSelectedItem());
            if (index > 0) {
                Collections.swap(chosenActions, index, index - 1);
                chosenActionsListView.getSelectionModel().select(index - 1);
            }
        }
    }

    @FXML
    void moveActionToDown(ActionEvent event) {
        if (chosenActionsListView.getSelectionModel().getSelectedItem() != null) {
            int index = chosenActions.indexOf(chosenActionsListView.getSelectionModel().getSelectedItem());
            if (index < chosenActions.size() - 1) {
                Collections.swap(chosenActions, index, index + 1);
                chosenActionsListView.getSelectionModel().select(index + 1);
            }
        }
    }
}
