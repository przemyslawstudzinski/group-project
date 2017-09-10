package guiproject;

import java.util.List;

public class Action {

    String name;

    String description;

    Receiver receiver;

    boolean closeProgram;

    boolean closeSystem;

    List<Node> nodes;

    public Action() {
    }

    public Action(String name, String description, Receiver receiver, boolean closeProgram, boolean closeSystem, List<Node> nodes) {
        this.name = name;
        this.description = description;
        this.receiver = receiver;
        this.closeProgram = closeProgram;
        this.closeSystem = closeSystem;
        this.nodes = nodes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public void setCloseProgram(boolean closeProgram) {
        this.closeProgram = closeProgram;
    }

    public void setCloseSystem(boolean closeSystem) {
        this.closeSystem = closeSystem;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public boolean isCloseProgram() {
        return closeProgram;
    }

    public boolean isCloseSystem() {
        return closeSystem;
    }

    public List<Node> getNodes() {
        return nodes;
    }
}
