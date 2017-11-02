package server.controller;

import com.pixelduke.javafx.validation.RequiredField;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import server.model.*;
import server.utils.*;
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
import server.validator.ExistNameValidator;
import org.apache.commons.io.FilenameUtils;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
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

public class MainWindowController implements Initializable {

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
    private TableView<NodeTableData> nodeTableView;

    @FXML
    private TableColumn<NodeTableData, Long> delayColumn;

    @FXML
    private ComboBox<Receiver> receiversComboBox;

    @FXML
    private TextField ageTextField;

    @FXML
    private TextField codeTextField;

    @FXML
    private Label sexErrorLabel;

    @FXML
    private RadioButton sexRadioButtonWomen;

    @FXML
    private RadioButton sexRadioButtonMen;

    @FXML
    private ComboBox<String> teachersComboBox;

    @FXML
    private Button showTeacherFieldButton;

    @FXML
    private Button addTeacherButton;

    @FXML
    private TextField newTeacherTextField;

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

    @FXML
    private Button chooseActionButton;

    @FXML
    private Label chosenActionLabel;

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

    private static final String configDirectory = "config" + File.separator;

    private static final String fileNameOfReceivers = configDirectory + "receivers.ini";

    private static final String fileNameOfTeachers = configDirectory + "teachers.ini";

    private static final String actionsPath = configDirectory + "actions" + File.separator;

    private static final String scenariosPath = configDirectory + "scenarios" + File.separator;

    private static final String scenarioFileExtension = ".cfg";

    private final Map<String, Action> availableActions = new HashMap();

    private final Map<String, Scenario> availableScenarios = new HashMap();

    private OutputConsole outputConsole;

    private boolean isRecording;

    public static Server server;

    public String actionClient = "";

    public RequiredField requiredCodeTextField;

    public RequiredField requiredAgeTextField;

    public RequiredField requiredTeacherField;

    public RequiredField requiredScenarioNameTextField;

    public RequiredField requiredScenarioDescriptionTextArea;

    public RequiredField requiredChosenActionsListView;

    public RequiredField requiredActionNameTextField;

    public RequiredField requiredActionDescriptionTextArea;

    public ExistNameValidator existActionNameTextField;

    public ExistNameValidator existScenarioNameTextField;

    File chosenActionToEdit;

    List<File> actionsFiles;

    List<File> scenarioFiles;

    ObservableList<NodeTableData> nodes = FXCollections.observableArrayList();

    public static final String studiesPath = configDirectory + "studies" + File.separator;

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

        //setTooltipToScenarios();
    }

    private void setTooltipToScenarios() {
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

    private void updateReceiversToBlock() {
        receiversToBlockMultiComboBox.getItems().clear();
        receiversToBlock.clear();
        String selected = allScenariosListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            for (Action a : availableScenarios.get(selected).getChosenActions()) {
                if (!receiversToBlock.contains(a.getReceiver()))
                    receiversToBlock.add(a.getReceiver());
            }
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
        newTeacherTextField.setVisible(false);
        addTeacherButton.setVisible(false);
        validateTextField(newTeacherTextField, "withoutNumbers");
        //code Text Field
        validateTextField(codeTextField, "textNumber");
        //sex Radio Buttons
        sexErrorLabel.setTextFill(Color.RED);
        sexErrorLabel.setStyle("-fx-font-size:12;");
        sexErrorLabel.setVisible(false);
        sexRadioButtonWomen.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) sexRadioButtonMen.setSelected(false);
        });
        sexRadioButtonMen.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) sexRadioButtonWomen.setSelected(false);
        });
        //code Text Field
        validateTextField(codeTextField, "textNumber");
        //age Text Field
        validateTextField(ageTextField, "numberOnly");
        //scenario Name Text Field
        validateTextField(scenarioNameTextField, "filename");
        //action Name Text Field
        validateTextField(actionNameTextField, "filename");

        updateReceiversToBlock();
        nodeTableView.setPlaceholder(new Label("Brak zdefiniowanych kliknięć"));
        assignTooltips();
    }

    @FXML
    void clearConsole() {
        textFlow.getChildren().clear();
    }

    void validateTextField(TextField field, String validationType) {
        if (validationType.equals("textNumber")) {
            field.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("[A-Za-z0-9]")) {
                    field.setText(newValue.replaceAll("[^A-Za-z0-9]", ""));
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
        } else if (validationType.equals("withoutNumbers")) {
            field.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("[^0-9]")) {
                    field.setText(newValue.replaceAll("[0-9]", ""));
                }
            });
        }
    }

    private void loadActionFiles(String path, Map<String, Action> map) throws IOException {
        actionsFiles = Files.walk(Paths.get(path))
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
        scenarioFiles = Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .filter(f -> f.getFileName().toString().endsWith(scenarioFileExtension))
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
            fis.close();
            isr.close();
            br.close();
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

    boolean validateTheSameNameOfScenarios() {
        existScenarioNameTextField.setColection(allScenarios);
        existScenarioNameTextField.eval();
        if (existScenarioNameTextField.getHasErrors() == true) {
            return false;
        }
        return true;
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

    boolean validateTheSameNameOfActions() {
        existActionNameTextField.setColection(allActions);
        existActionNameTextField.eval();
        if (existActionNameTextField.getHasErrors() == true) {
            return false;
        }
        return true;
    }

    boolean validateEmptyFieldsOnStudyTab() {
        requiredCodeTextField.eval();
        requiredAgeTextField.eval();
        sexErrorLabel.setVisible(false);
        if (!sexRadioButtonMen.isSelected() && !sexRadioButtonWomen.isSelected()) {
            sexErrorLabel.setVisible(true);
            return false;
        }
        if (requiredCodeTextField.getHasErrors() || requiredAgeTextField.getHasErrors()) {
            return false;
        }
        return true;
    }

    @FXML
    void saveScenario(ActionEvent event) throws IOException {
        boolean validateEmptyFields = validateEmptyFieldsOnScenarioTab();
        boolean validateTheSameNames = validateTheSameNameOfScenarios();
        if (validateEmptyFields && validateTheSameNames) {
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
            outputConsole.writeLine("[Zapisywanie scenariusza] Zapisano scenariusz: " + scenario.getName() + ".");

        }
    }

    void saveAction() throws IOException {
        Action action = new Action();
        action.setName(actionNameTextField.getText());
        action.setDescription(actionDescriptionTextArea.getText());
        action.setReceiver(receiversComboBox.getSelectionModel().getSelectedItem());
        action.setNodes(server.connectedClientsMap.get(actionClient).getRecordedClicks());

        String filename = actionNameTextField.getText();
        saveActionToFile(action, filename);
        loadActionFiles(actionsPath, availableActions);
        allActions.add(action.getName());
        actionNameTextField.clear();
        actionDescriptionTextArea.clear();
        server.connectedClientsMap.get(actionClient).getRecordedClicks().clear();
        receiversComboBox.getSelectionModel().selectFirst();
    }

    private void saveActionToFile(Action action, String filename) {
        try {
            File file = new File(actionsPath + filename + ".xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(Action.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(action, file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    boolean actionClientAvailable() {
        if (server.connectedClientsMap.containsKey(actionClient))
            return true;
        return false;
    }

    @FXML
    void startRecording(ActionEvent event) throws IOException {
        if (actionClientAvailable()) {
            boolean validateEmptyFields = validateEmptyFieldsOnActionTab();
            boolean validateTheSameNames = validateTheSameNameOfActions();
            if (validateEmptyFields && validateTheSameNames) {
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

    @FXML
    void showTeacherField(ActionEvent event) {
        newTeacherTextField.setVisible(true);
        addTeacherButton.setVisible(true);
    }

    @FXML
    void addTeacher(ActionEvent event) throws IOException {
        requiredTeacherField.eval();
        if (!requiredTeacherField.getHasErrors()) {
            allTeachers.add(newTeacherTextField.getText());
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileNameOfTeachers, true));
            bw.newLine();
            bw.write(newTeacherTextField.getText());
            bw.close();
            newTeacherTextField.setText("");
            newTeacherTextField.setVisible(false);
            addTeacherButton.setVisible(false);
            outputConsole.writeLine("[Dodawanie opiekuna] Pomyślnie dodano nowego opiekuna");
        }
    }

    void prepareStudy(Study study) {
        study.setCode(codeTextField.getText());
        if (sexRadioButtonMen.isSelected())
            study.setSex("Mężczyzna");
        else
            study.setSex("Kobieta");
        study.setAge(ageTextField.getText());
        study.setTeacher(teachersComboBox.getSelectionModel().getSelectedItem());
        study.setChosenScenario(availableScenarios.get(allScenariosListView.getSelectionModel().getSelectedItem()));
        study.setBlockPeripherals(blockPeripheralsSwitch.isSelected());
        ArrayList<Receiver> copy = new ArrayList<>();
        for (Receiver r : receiversToBlockMultiComboBox.getCheckModel().getCheckedItems()) {
            copy.add(r);
        }
        study.setBlockedPeripheralsOnReceivers(copy);
    }

    void clearStudyFields() {
        codeTextField.clear();
        sexRadioButtonWomen.setSelected(false);
        sexRadioButtonMen.setSelected(false);
        ageTextField.clear();
        teachersComboBox.getSelectionModel().selectFirst();
        receiversToBlockMultiComboBox.getCheckModel().clearChecks();
        blockPeripheralsSwitch.setSelected(false);
        allScenariosListView.getSelectionModel().selectFirst();
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

    @FXML
    void chooseActionToEdit(ActionEvent e) throws IOException {
        clearEditActonTab();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose action to edit");
        fileChooser.setInitialDirectory(new File(actionsPath));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
        Node source = (Node) e.getSource();
        Window stage = source.getScene().getWindow();
        chosenActionToEdit = fileChooser.showOpenDialog(stage);

        if (chosenActionToEdit != null && chosenActionToEdit.exists()) {
            String actionName = FilenameUtils.removeExtension(chosenActionToEdit.getName());

            if (actionsFiles.stream().filter(
                    x -> x.getName().equals(chosenActionToEdit.getName())).findFirst().isPresent()) {
                chosenActionLabel.setText(chosenActionToEdit.getName());
                Action action = availableActions.get(actionName);
                List<server.model.Node> nodesList;
                if (action != null) {
                    nodesList = action.getNodes();
                    nodesList.forEach(node -> nodes.add(new NodeTableData(node)));
                    nodeTableView.setItems(nodes);
                }
                nodeTableView.setEditable(true);
                setupDelayColumn();
                outputConsole.writeLine("[Edycja akcji] Wczytano akcję: " + actionName + " do edycji");
            }
        }
    }

    private void setupDelayColumn() {
        delayColumn.setCellFactory(MyEditCell.<NodeTableData, Long>forTableColumn(new MyLongStringConverter()));
        // updates the delay time field on the NodeTableData
        delayColumn.setOnEditCommit(event -> {
            final Long value = event.getNewValue() != null
                    ? event.getNewValue() : event.getOldValue();
            NodeTableData node =  event.getTableView().getItems()
                    .get(event.getTablePosition().getRow());
            node.setDelay(value);
            nodeTableView.refresh();
        });
    }

    private void removeEditActionFile() {
        Action action = availableActions.get(chosenActionLabel.getText());
        allActions.remove(action);
        chosenActions.remove(action);
        availableActions.remove(action);
        actionsFiles.remove(chosenActionToEdit);
        chosenActionToEdit.delete();
        allActionsListView.refresh();
        chosenActionsListView.refresh();
    }

    @FXML
    private void removeNode() throws IOException {
        NodeTableData nodeTableData = nodeTableView.getSelectionModel().getSelectedItem();
        if (nodeTableData != null) {
            Action action = availableActions.get(FilenameUtils.removeExtension(chosenActionLabel.getText()));
            nodes.removeIf(x -> x.getId().equals(nodeTableData.getId()));

            nodeTableView.refresh();
            outputConsole.writeLine("[Edycja akcji] Usunięto kliknięcie z akcji: " + action.getName());
        }
    }

    @FXML
    private void editAction() throws IOException {
        Action action = availableActions.get(FilenameUtils.removeExtension(chosenActionLabel.getText()));
        if (action != null) {
            List<NodeTableData> nodesTableData = nodeTableView.getItems();
            action.getNodes().clear();
            nodesTableData.forEach(x -> action.getNodes().add(x.toNode()));

            removeEditActionFile();
            saveActionToFile(action, action.getName());
            loadActionFiles(actionsPath, availableActions);

            clearEditActonTab();
            outputConsole.writeLine("[Edycja akcji] Zapisano nową wersję akcji: " + action.getName());
        }
    }

    private void clearEditActonTab() {
        chosenActionLabel.setText("");
        nodes.clear();
        chosenActionToEdit = null;
        nodeTableView.refresh();
    }

    @FXML
    private void removeScenarioFile() throws IOException {
        String scenarioName = allScenariosListView.getSelectionModel().getSelectedItem();
        Scenario scenario = availableScenarios.get(scenarioName);

        File file = Paths.get(scenariosPath + scenarioName + scenarioFileExtension).toFile();
        if (scenarioName != null && scenario != null && file != null) {
            if (file.delete()) {
                scenarioFiles.remove(file);
                availableScenarios.remove(scenarioName, scenario);
                allScenarios.remove(scenarioName);

                allScenariosListView.refresh();
                outputConsole.writeLine("[Usuwanie scenariusza] Usunięto scenariusz: " + scenarioName + ".");
            }
        }
    }


}
