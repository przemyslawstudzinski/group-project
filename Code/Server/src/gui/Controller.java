package gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.TextFlow;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.ToggleSwitch;
import server.Server;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
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

    private static final String configDirectory = "Config" + File.separator;
    private static final String fileNameOfReceivers = configDirectory + File.separator + "receivers.ini";
    private static final String fileNameOfTeachers = configDirectory + File.separator + "teachers.ini";

    private static final String actionsPath = configDirectory + File.separator + "Actions" + File.separator;
    private static final String scenariosPath = configDirectory + File.separator + "Scenarios" + File.separator;
    private static final String studiesPath = configDirectory + File.separator + "Studies" + File.separator;

    private final Map<String, Action> availableActions = new HashMap();
    private final Map<String, Scenario> availableScenarios = new HashMap();

    private OutputConsole outputConsole;

    public static Server server;

    public String actionClient = "";

    public void shutdown() throws InterruptedException, IOException {
        server.running = false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        server = new Server();
        server.start();

        outputConsole = new OutputConsole(textFlow);

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
        receiversToBlockMultiComboBox.getItems().addAll(allReceivers);
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

        //logScrollPanel.vvalueProperty().bind(textFlow.heightProperty());
    }

    @FXML
    void clearConsole()
    {
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


    @FXML
    void saveScenario(ActionEvent event) throws IOException {
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
            // output pretty printed
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
    void stopRecording(ActionEvent event) throws IOException {
        if (actionClientAvailable()) {
            PrintWriter out = new PrintWriter(server.connectedClientsMap.get(actionClient).getSocket().getOutputStream(), true);
            out.println("stoprecord");
            saveAction();
        } else
            outputConsole.writeErrorLine("Wybrany odbiorca nie jest aktualnie połączony z serwerem!");
    }

    @FXML
    void startRecording(ActionEvent event) throws IOException {
        if (actionClientAvailable()) {
            PrintWriter out = new PrintWriter(server.connectedClientsMap.get(actionClient).getSocket().getOutputStream(), true);
            out.println("record");
        } else
            outputConsole.writeErrorLine("Wybrany odbiorca nie jest aktualnie połączony z serwerem!");
    }

    void prepareStudy(Study study) {
        study.setName(nameTextField.getText());
        study.setLastName(lastNameTextField.getText());
        study.setAge(ageTextField.getText());
        study.setCloseSystem(closeSystemCheckBox.isSelected());
        study.setTeacher(teachersComboBox.getSelectionModel().getSelectedItem());
        String scenarioToRun = allScenariosListView.getSelectionModel().getSelectedItem();
        study.setChosenScenario(availableScenarios.get(scenarioToRun));
        study.setBlockPeripherals(blockPeripheralsSwitch.isSelected());
        study.setBlockedPeripheralsOnReceivers(receiversToBlockMultiComboBox.getCheckModel().getCheckedItems());
    }

    void finishStudy() {
        nameTextField.clear();
        lastNameTextField.clear();
        ageTextField.clear();
        closeSystemCheckBox.selectedProperty().setValue(false);
        teachersComboBox.getSelectionModel().selectFirst();
        allScenariosListView.getSelectionModel().clearSelection();
        receiversToBlockMultiComboBox.getCheckModel().clearChecks();
        blockPeripheralsSwitch.setSelected(false);
        allScenariosListView.getSelectionModel().selectFirst();
    }

    @FXML
    void runStudy(ActionEvent event) throws IOException, InterruptedException {
        final Study study = new Study();
        prepareStudy(study);
        StudyThread studyThread = new StudyThread(study, server);
        studyThread.run();
        finishStudy();
    }

}
