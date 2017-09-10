package guiproject;

import java.util.List;

public class Scenario {

    String name;

    String description;

    List<Action> chosenActions;

    public Scenario() {
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

    public void setChoosenActions(List<Action> chosenActions) {
        this.chosenActions = chosenActions;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Action> getChoosenActions() {
        return chosenActions;
    }
}
