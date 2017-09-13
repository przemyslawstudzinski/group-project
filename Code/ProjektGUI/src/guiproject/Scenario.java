package guiproject;

import javax.xml.bind.annotation.XmlRootElement;
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

    public Scenario(String name, String description, List<Action> choosenActions) {
        this.name = name;
        this.description = description;
        this.chosenActions = choosenActions;
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
}
