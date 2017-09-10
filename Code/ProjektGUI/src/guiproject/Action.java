package guiproject;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class Action {

    String name;

    String description;

    Receiver receiver;

    List<Node> nodes;

    public Action() {
    }

    public Action(String name, String description, Receiver receiver, List<Node> nodes) {
        this.name = name;
        this.description = description;
        this.receiver = receiver;
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

    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        return name;
    }
}
