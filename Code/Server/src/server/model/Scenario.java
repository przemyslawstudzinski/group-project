package server.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class Scenario {

    private String name;

    private String description;

    private List<Action> chosenActions;

    public Scenario() {
        chosenActions = new ArrayList<>();
    }

    public Scenario(String name, String description, List<Action> chosenActions) {
        this.name = name;
        this.description = description;
        this.chosenActions = chosenActions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setChosenActions(List<Action> chosenActions) {
        this.chosenActions = chosenActions;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Action> getChosenActions() {
        return chosenActions;
    }

    @Override
    public String toString() {
        return name;
    }

    public void saveToFile(String scenariosPath) throws IOException {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(scenariosPath + this.getName() + ".cfg"), "utf-8"))) {
            writer.write("nazwa:" + this.name + "\n" + "opis:" + this.description + "\n");
            for (Action i : this.chosenActions)
                writer.write("akcja:" + i.getName() + "\n");
        }
    }
}
