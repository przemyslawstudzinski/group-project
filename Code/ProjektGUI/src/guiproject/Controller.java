package guiproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
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
    private Slider onOffSlider;

    private final ObservableList<String> allStudies
            = FXCollections.observableArrayList("study1", "study2", "study3", "study4");

    private final ObservableList<String> allActions
            = FXCollections.observableArrayList("action1", "action2", "action3", "action4");

    private final ObservableList<String> chosenActions
            = FXCollections.observableArrayList();

    private static final String fileNameOfReceivers = "./src/guiproject/receivers";
    private ObservableList<String> receivers
            = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        allStudiesListView.setItems(allStudies);
        allActionListView.setItems(allActions);
        chosenActionsListView.setItems(chosenActions);

        //Receivers ComboBox
        readReceivers();
        receiversComboBox.setItems(receivers);
        receiversComboBox.getSelectionModel().selectFirst();

        //onOffSlider
        onOffSlider.setMin(0);
        onOffSlider.setMax(1);
        onOffSlider.setValue(0);
        onOffSlider.setShowTickLabels(false);
        onOffSlider.setShowTickMarks(false);
        onOffSlider.setMajorTickUnit(1);
        onOffSlider.setMinorTickCount(0);
        onOffSlider.setBlockIncrement(1);
        onOffSlider.setSnapToTicks(true);
    }

    private void readReceivers() {
        try (Stream<String> stream = Files.lines(Paths.get(fileNameOfReceivers))) {
            receivers = stream
                    .map(line -> line.substring(line.lastIndexOf(" ") + 1))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));

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
        if(chosenActionsListView.getSelectionModel().getSelectedItem() != null) {
            int index = chosenActions.indexOf(chosenActionsListView.getSelectionModel().getSelectedItem());
            if(index > 0) {
                Collections.swap(chosenActions, index, index - 1);
                chosenActionsListView.getSelectionModel().select(index - 1);
            }
        }
    }

    @FXML
    void moveActionToDown(ActionEvent event) {
        if(chosenActionsListView.getSelectionModel().getSelectedItem() != null) {
            int index = chosenActions.indexOf(chosenActionsListView.getSelectionModel().getSelectedItem());
            if(index < chosenActions.size() - 1) {
                Collections.swap(chosenActions, index, index + 1);
                chosenActionsListView.getSelectionModel().select(index + 1);
            }
        }
    }
}
