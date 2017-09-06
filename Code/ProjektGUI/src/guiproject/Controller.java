package guiproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ListView<String> allStudiesListView;

    @FXML
    private ListView<String> allActionListView;

    @FXML
    private ListView<String> chosenActionsListView;

    private final ObservableList<String> allStudies
            = FXCollections.observableArrayList("study1", "study2", "study3", "study4");

    private final ObservableList<String> allActions
            = FXCollections.observableArrayList("action1", "action2", "action3", "action4");

    private final ObservableList<String> chosenActions
            = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        allStudiesListView.setItems(allStudies);
        allActionListView.setItems(allActions);
        chosenActionsListView.setItems(chosenActions);
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

    }

    @FXML
    void moveActionToDown(ActionEvent event) {

    }
}
