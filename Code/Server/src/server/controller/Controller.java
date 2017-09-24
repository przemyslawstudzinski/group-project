package server.controller;

import com.pixelduke.javafx.validation.RequiredField;
import server.utils.OutputConsole;
import server.model.Scenario;
import server.utils.StudyThread;
import server.model.Action;
import server.model.Receiver;
import server.model.Study;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.ToggleSwitch;
import server.utils.Server;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Controller implements Initializable {

    @FXML
    private Button startRecordingButton;

    @FXML
    private Button stopRecordingButton;

    @FXML
    private Button runScenarioButton;

    @FXML
    private Button infoButton;

    @FXML
    private Button moveRightButton;

    @FXML
    private Button moveLeftButton;

    @FXML
    private Button moveUpButton;

    @FXML
    private Button moveDownButton;

    @FXML
    private ListView<String> allScenariosListView;

    @FXML
    private ListView<String> allActionsListView;

    @FXML
    private ListView<String> chosenActionsListView;

    @FXML
    private ComboBox<Receiver> receiversComboBox;

    @FXML
    private TextField ageTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private ComboBox<String> teachersComboBox;

    @FXML
    private CheckBox closeSystemCheckBox;

    @FXML
    private TextField scenarioNameTextField;

    @FXML
    private TextField actionNameTextField;

    @FXML
    private TextArea actionDescriptionTextArea;

    @FXML
    private TextArea scenarioDescriptionTextArea;

    @FXML
    private ToggleSwitch blockPeripheralsSwitch;

    @FXML
    private CheckComboBox<Receiver> receiversToBlockMultiComboBox;

    @FXML
    private TextFlow textFlow;

    @FXML
    private ScrollPane logScrollPanel;

    private final ObservableList<String> allActions
            = FXCollections.observableArrayList();

    private final ObservableList<String> allScenarios
            = FXCollections.observableArrayList();

    private final ObservableList<String> allTeachers
            = FXCollections.observableArrayList();

    private final ObservableList<String> chosenActions
            = FXCollections.observableArrayList();

    private final ObservableList<Receiver> allReceivers
            = FXCollections.observableArrayList();

    private final ObservableList<Receiver> receiversToBlock
            = FXCollections.observableArrayList();

    private static final String configDirectory = "Config" + File.separator;
    private static final String fileNameOfReceivers = configDirectory + File.separator + "receivers.ini";
    private static final String fileNameOfTeachers = configDirectory + File.separator + "teachers.ini";

    private static final String actionsPath = configDirectory + File.separator + "Actions" + File.separator;
    private static final String scenariosPath = configDirectory + File.separator + "Scenarios" + File.separator;

    private final Map<String, Action> availableActions = new HashMap();
    private final Map<String, Scenario> availableScenarios = new HashMap();

    private OutputConsole outputConsole;

    private boolean isRecording;

    public static Server server;

    public String actionClient = "";

    public RequiredField requiredNameTextField;
    public RequiredField requiredLastNameTextField;
    public RequiredField requiredAgeTextField;

    public RequiredField requiredScenarioNameTextField;
    public RequiredField requiredScenarioDescriptionTextArea;
    public RequiredField requiredChosenActionsListView;


    public RequiredField requiredActionNameTextField;
    public RequiredField requiredActionDescriptionTextArea;

    public void shutdown() throws InterruptedException, IOException {
        server.running = false;
    }

    private static void updateTooltipBehavior(double openDelay, double visibleDuration, double closeDelay, boolean hideOnExit) {
        try {
            Field fieldBehavior = Tooltip.class.getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(null);
            Constructor<?> constructor = objBehavior.getClass().getDeclaredConstructor(
                    Duration.class, Duration.class, Duration.class, boolean.class
            );
            constructor.setAccessible(true);
            Object tooltipBehavior = constructor.newInstance(
                    new Duration(openDelay), new Duration(visibleDuration),
                    new Duration(closeDelay), hideOnExit
            );
            fieldBehavior.set(null, tooltipBehavior);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    private void assignTooltips() {
        updateTooltipBehavior(50, 10000, 50, true);

        Tooltip tooltip = new Tooltip();
        tooltip.setText("Rozpocznij nagrywanie akcji");
        startRecordingButton.setTooltip(tooltip);
        tooltip = new Tooltip();
        tooltip.setText("Zakończ nagrywanie akcji");
        stopRecordingButton.setTooltip(tooltip);
        tooltip = new Tooltip();
        tooltip.setText("Uruchom badanie");
        runScenarioButton.setTooltip(tooltip);
        tooltip = new Tooltip();
        tooltip.setText("StudiController v1.0 beta");
        infoButton.setTooltip(tooltip);

        tooltip = new Tooltip();
        tooltip.setText("Dodaj akcję do scenariusza");
        moveRightButton.setTooltip(tooltip);
        tooltip = new Tooltip();
        tooltip.setText("Usuń akcję ze scenariusza");
        moveLeftButton.setTooltip(tooltip);
        tooltip = new Tooltip();
        tooltip.setText("Przesuń akcję w górę");
        moveUpButton.setTooltip(tooltip);
        tooltip = new Tooltip();
        tooltip.setText("Przesuń akcję w dół");
        moveDownButton.setTooltip(tooltip);


        allScenariosListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            public ListCell<String> call(ListView<String> param) {
                Tooltip tooltip = new Tooltip();
                final ListCell<String> cell = new ListCell<String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item);
                            String tooltipText = "";
                            int index = 1;
                            for (Action a : availableScenarios.get(item).getChosenActions()) {
                                tooltipText += Integer.toString(index) + ". Akcja: " + a.getName() + ", odbiorca: " + a.getReceiver().getName() + "\n";
                                index++;
                            }
                            tooltip.setText(tooltipText);
                            setTooltip(tooltip);
                        }
                    }
                };
                return cell;
            }
        });
    }

private void updateReceiversToBlock()
{
    receiversToBlockMultiComboBox.getItems().clear();
    receiversToBlock.clear();
    for (Action a : availableScenarios.get(allScenariosListView.getSelectionModel().getSelectedItem()).getChosenActions()) {
        receiversToBlock.add(a.getReceiver());
    }
    receiversToBlockMultiComboBox.getItems().addAll(receiversToBlock);
}
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        outputConsole = new OutputConsole(logScrollPanel, textFlow);
        server = new Server(outputConsole);
        server.start();

        try {
            //load Receivers and Teachers from .ini file and add them to receivers comboBox
            loadConfigFiles();
            //load Actions from XML files
            loadActionFiles(actionsPath, availableActions);
            allActions.addAll(availableActions.keySet());
            allActionsListView.setItems(allActions);
            allActionsListView.getSelectionModel().selectFirst();
            //load Scenarios from XML file
            loadScenarioFiles(scenariosPath, availableScenarios);
            allScenarios.addAll(availableScenarios.keySet());
            allScenariosListView.setItems(allScenarios);
            allScenariosListView.getSelectionModel().selectFirst();
        } catch (IOException e) {
            e.printStackTrace();
        }

        chosenActionsListView.setItems(chosenActions);

        allScenariosListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
               updateReceiversToBlock();
            }
        });

        receiversToBlockMultiComboBox.setDisable(true);
        actionClient = receiversComboBox.getSelectionModel().getSelectedItem().getIpAddress();


        receiversComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) ->
                actionClient = newValue.getIpAddress()
        );

        //enable/disable possibility to block keyboard/mouse input on several receivers
        blockPeripheralsSwitch.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue)
                    receiversToBlockMultiComboBox.setDisable(false);
                else
                    receiversToBlockMultiComboBox.setDisable(true);
            }
        });

        //teachers ComboBox
        teachersComboBox.setItems(allTeachers);
        teachersComboBox.getSelectionModel().selectFirst();
        //name Text Field
        validateTextField(nameTextField, "textOnly");
        //last name Text Field
        validateTextField(lastNameTextField, "textOnly");
        //age Text Field
        validateTextField(ageTextField, "numberOnly");
        //scenario Name Text Field
        validateTextField(scenarioNameTextField, "filename");
        //action Name Text Field
        validateTextField(actionNameTextField, "filename");

        updateReceiversToBlock();
        assignTooltips();
    }


    @FXML
    void clearConsole() {
        textFlow.getChildren().clear();
    }

    void validateTextField(TextField field, String validationType) {
        if (validationType.equals("textOnly")) {
            field.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\sa-zA-Z*")) {
                    field.setText(newValue.replaceAll("[^\\sa-zA-Z]", ""));
                }
            });
        } else if (validationType.equals("numberOnly")) {
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
        } else if (validationType.equals("filename")) {
            field.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("[-_. A-Za-z0-9]")) {
                    field.setText(newValue.replaceAll("[^-_. A-Za-z0-9]", ""));
                }
            });
        }
    }

    private void loadActionFiles(String path, Map<String, Action> map) throws IOException {
        List<File> actionsFiles = Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .filter(f -> f.getFileName().toString().endsWith(".xml"))
                .map(Path::toFile)
                .collect(Collectors.toList());
        for (File file : actionsFiles) {
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(Action.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                Action action = (Action) jaxbUnmarshaller.unmarshal(file);
                int pos = file.getName().lastIndexOf(".");
                String filename = pos > 0 ? file.getName().substring(0, pos) : file.getName();
                map.putIfAbsent(filename, action);

            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadScenarioFiles(String path, Map<String, Scenario> map) throws IOException {
        List<File> scenarioFiles = Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .filter(f -> f.getFileName().toString().endsWith(".cfg"))
                .map(Path::toFile)
                .collect(Collectors.toList());
        for (File file : scenarioFiles) {
            Scenario scenario = new Scenario();
            String line;

            InputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);

            while ((line = br.readLine()) != null) {
                String textToSet = line.split(":", 0)[1];
                switch (line.split(":", 0)[0]) {
                    case "nazwa":
                        scenario.setName(textToSet);
                        break;
                    case "opis":
                        scenario.setDescription(textToSet);
                        break;
                    case "akcja":
                        scenario.getChosenActions().add(availableActions.get(textToSet));
                }
            }
            int pos = file.getName().lastIndexOf(".");
            String filename = pos > 0 ? file.getName().substring(0, pos) : file.getName();
            map.putIfAbsent(filename, scenario);
        }
    }

    private void loadConfigFiles() throws IOException {
        //set Receivers
        try {
            Stream<String> lines = Files.lines(Paths.get(fileNameOfReceivers));
            lines.filter(line -> line.contains(" ")).forEach(
                    line -> allReceivers.add(new Receiver(line.split(" ")[1], line.split(" ")[0])));
            receiversComboBox.setItems(allReceivers);
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
    }

    @FXML
    void moveActionToRight(ActionEvent event) {
        if (allActionsListView.getSelectionModel().getSelectedItem() != null) {
            chosenActions.add(allActionsListView.getSelectionModel().getSelectedItem());
            outputConsole.writeLine("Dodano akcję " + allActionsListView.getSelectionModel().getSelectedItem() + " do scenariusza!");
            allActions.remove(allActionsListView.getSelectionModel().getSelectedItem());

        }
    }

    @FXML
    void moveActionToLeft(ActionEvent event) {
        if (chosenActionsListView.getSelectionModel().getSelectedItem() != null) {
            allActions.add(chosenActionsListView.getSelectionModel().getSelectedItem());
            outputConsole.writeLine("Usunięto akcję " + chosenActionsListView.getSelectionModel().getSelectedItem() + " ze scenariusza!");
            chosenActions.remove(chosenActionsListView.getSelectionModel().getSelectedItem());

        }
    }

    @FXML
    void moveActionToUp(ActionEvent event) {
        // move up only if there are min. 2 actions
        if (chosenActionsListView.getSelectionModel().getSelectedItem() != null && chosenActions.size() > 1) {
            int index = chosenActions.indexOf(chosenActionsListView.getSelectionModel().getSelectedItem());
            if (index > 0) {
                Collections.swap(chosenActions, index, index - 1);
                chosenActionsListView.getSelectionModel().select(index - 1);
                outputConsole.writeLine("Zmieniono kolejność akcji: przesunięto w górę");
            }
        }
    }

    @FXML
    void moveActionToDown(ActionEvent event) {
        // move up only if there are min. 2 actions
        if (chosenActionsListView.getSelectionModel().getSelectedItem() != null && chosenActions.size() > 1) {
            int index = chosenActions.indexOf(chosenActionsListView.getSelectionModel().getSelectedItem());
            if (index < chosenActions.size() - 1) {
                Collections.swap(chosenActions, index, index + 1);
                chosenActionsListView.getSelectionModel().select(index + 1);
                outputConsole.writeLine("Zmieniono kolejność akcji: przesunięto w dół");
            }
        }
    }

    boolean validateEmptyFieldsOnScenarioTab() {
        requiredScenarioNameTextField.eval();
        requiredScenarioDescriptionTextArea.eval();
        if (chosenActionsListView.getItems().size() == 0) {
            requiredChosenActionsListView.setHasErrors(true);
        } else {
            requiredChosenActionsListView.setHasErrors(false);
        }

        if (requiredScenarioNameTextField.getHasErrors()
                || requiredScenarioDescriptionTextArea.getHasErrors()
                || requiredChosenActionsListView.getHasErrors()) {
            return false;
        }
        return true;
    }

    @FXML
    void saveScenario(ActionEvent event) throws IOException {
        if (validateEmptyFieldsOnScenarioTab()) {
            Scenario scenario = new Scenario();
            scenario.setName(scenarioNameTextField.getText());
            scenario.setDescription(scenarioDescriptionTextArea.getText());
            List<Action> actionsForScenario = new ArrayList<Action>();
            for (String chosenAction : chosenActions)
                actionsForScenario.add(availableActions.get(chosenAction));
            scenario.setChosenActions(actionsForScenario);
            scenario.saveToFile(scenariosPath);
            loadScenarioFiles(scenariosPath, availableScenarios);
            allScenarios.add(scenario.getName());
            scenarioNameTextField.clear();
            scenarioDescriptionTextArea.clear();
            allActions.addAll(chosenActions);
            chosenActions.removeAll();
            chosenActionsListView.getItems().clear();
            allActionsListView.getSelectionModel().selectFirst();
        }
    }

    boolean validateEmptyFieldsOnActionTab() {
        requiredActionNameTextField.eval();
        requiredActionDescriptionTextArea.eval();
        if (requiredActionNameTextField.getHasErrors()
                || requiredActionDescriptionTextArea.getHasErrors()) {
            return false;
        }
        return true;
    }

    void saveAction() throws IOException {
        Action action = new Action();
        action.setName(actionNameTextField.getText());
        action.setDescription(actionDescriptionTextArea.getText());
        action.setReceiver(receiversComboBox.getSelectionModel().getSelectedItem());
        action.setNodes(server.connectedClientsMap.get(actionClient).getRecordedClicks());

        String filename = actionNameTextField.getText();
        try {
            File file = new File(actionsPath + filename + ".xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(Action.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(action, file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        loadActionFiles(actionsPath, availableActions);
        allActions.add(action.getName());
        actionNameTextField.clear();
        actionDescriptionTextArea.clear();
        server.connectedClientsMap.get(actionClient).getRecordedClicks().clear();
        receiversComboBox.getSelectionModel().selectFirst();
    }

    boolean actionClientAvailable() {
        if (server.connectedClientsMap.containsKey(actionClient))
            return true;
        return false;
    }

    @FXML
    void startRecording(ActionEvent event) throws IOException {
        if (actionClientAvailable()) {
            if (validateEmptyFieldsOnActionTab()) {
                PrintWriter out = new PrintWriter(server.connectedClientsMap.get(actionClient).getSocket().getOutputStream(), true);
                out.println("record");
                isRecording = true;
            }
        } else
            outputConsole.writeErrorLine("[Nagrywanie] Wybrany odbiorca nie jest aktualnie połączony z serwerem!");
    }

    @FXML
    void stopRecording(ActionEvent event) throws IOException {
        if (isRecording) {
            if (actionClientAvailable()) {
                PrintWriter out = new PrintWriter(server.connectedClientsMap.get(actionClient).getSocket().getOutputStream(), true);
                out.println("stoprecord");
                saveAction();
                isRecording = false;
            } else
                outputConsole.writeErrorLine("[Koniec nagrywania] Wybrany odbiorca nie jest aktualnie połączony z serwerem!");
        }
    }


    void prepareStudy(Study study) {
        study.setName(nameTextField.getText());
        study.setLastName(lastNameTextField.getText());
        study.setAge(ageTextField.getText());
        study.setTeacher(teachersComboBox.getSelectionModel().getSelectedItem());
        study.setChosenScenario(availableScenarios.get(allScenariosListView.getSelectionModel().getSelectedItem()));
        study.setBlockPeripherals(blockPeripheralsSwitch.isSelected());
        study.setBlockedPeripheralsOnReceivers(receiversToBlockMultiComboBox.getCheckModel().getCheckedItems());
    }

    void clearStudyFields() {
        nameTextField.clear();
        lastNameTextField.clear();
        ageTextField.clear();
        teachersComboBox.getSelectionModel().selectFirst();
        receiversToBlockMultiComboBox.getCheckModel().clearChecks();
        blockPeripheralsSwitch.setSelected(false);
        allScenariosListView.getSelectionModel().selectFirst();
    }

    boolean validateEmptyFieldsOnStudyTab() {
        requiredNameTextField.eval();
        requiredLastNameTextField.eval();
        requiredAgeTextField.eval();
        if (requiredNameTextField.getHasErrors()
                || requiredLastNameTextField.getHasErrors()
                || requiredAgeTextField.getHasErrors()) {
            return false;
        }
        return true;
    }

    @FXML
    void runStudy() throws IOException, InterruptedException {
        if (validateEmptyFieldsOnStudyTab()) {
            Stage primaryStage = (Stage) runScenarioButton.getScene().getWindow();

            final Study study = new Study();
            prepareStudy(study);
            StudyThread studyThread = new StudyThread(study, primaryStage, outputConsole);
            studyThread.run();
            clearStudyFields();
        }
    }
}
