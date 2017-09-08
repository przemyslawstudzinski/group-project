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

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Stream;


public class Controller implements Initializable {

    @FXML
    private ListView<String> allStudiesListView;

    @FXML
    private ListView<String> allActionListView;

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
    private ToggleSwitch blockPeripherialsSwitch;

    @FXML
    private CheckComboBox<String> receiversToBlockMultiComboBox;

    private final ObservableList<String> allStudies
            = FXCollections.observableArrayList("study1", "study2", "study3", "study4");

    private final ObservableList<String> allActions
            = FXCollections.observableArrayList("action1", "action2", "action3", "action4");

    private final ObservableList<String> allTeachers
            = FXCollections.observableArrayList("dr inż. Michał Wróbel", "dr inż. Michał NieWróbel");

    private final ObservableList<String> chosenActions
            = FXCollections.observableArrayList();

    private static final String fileNameOfReceivers = "./Config/receivers.ini";
    private final Map<String, String> receivers = new HashMap<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        allStudiesListView.setItems(allStudies);
        allActionListView.setItems(allActions);
        chosenActionsListView.setItems(chosenActions);
        //load Receivers from .ini file and add them to receivers comboBox
        setReceivers();
        receiversToBlockMultiComboBox.getItems().addAll(receivers.keySet());
        receiversToBlockMultiComboBox.setDisable(true);
        //enable/disable possibility to block keyboard/mouse input on several receviers
        blockPeripherialsSwitch.selectedProperty().addListener(new ChangeListener< Boolean >() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue)
                    receiversToBlockMultiComboBox.setDisable(false);
                else
                    receiversToBlockMultiComboBox.setDisable(true);
                /*//get IP's of receivers which should have blocked mouse/keyboard during scenario execution
                for (String key : receiversToBlockMultiComboBox.getCheckModel().getCheckedItems())
                    System.out.println(receivers.get(key));
                //get IP of receiver on which we want to record the action
                System.out.println(receivers.get(receiversComboBox.getSelectionModel().getSelectedItem()));*/
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

    private void setReceivers() {
        try {
            Stream<String> lines = Files.lines(Paths.get(fileNameOfReceivers));
            lines.filter(line -> line.contains(" ")).forEach(line -> receivers.putIfAbsent(line.split(" ")[1], line.split(" ")[0]));
            ObservableList<String> receiversList = FXCollections.observableArrayList(receivers.keySet());
            receiversComboBox.setItems(receiversList);
            receiversComboBox.getSelectionModel().selectFirst();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void moveActionToRight(ActionEvent event) {
        if (allActionListView.getSelectionModel().getSelectedItem() != null) {
            chosenActions.add(allActionListView.getSelectionModel().getSelectedItem());
            allActions.remove(allActionListView.getSelectionModel().getSelectedItem());
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
