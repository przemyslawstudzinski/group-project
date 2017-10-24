package server.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;

import java.util.UUID;

public class NodeTableData {

    private UUID id;

    private SimpleIntegerProperty corX;

    private SimpleIntegerProperty corY;

    private SimpleLongProperty delay;

    private SimpleBooleanProperty isDouble;

    public NodeTableData(Node node) {
        this.corX = new SimpleIntegerProperty(node.getCorX());
        this.corY = new SimpleIntegerProperty(node.getCorY());
        this.delay = new SimpleLongProperty(node.getDelay());
        this.isDouble = new SimpleBooleanProperty(node.getIsDouble());
        this.id = node.getId();
    }

    public UUID getId() {
        return this.id;
    }

    public Node toNode() {
        return new Node(this.corX.intValue(), this.corY.intValue(),
                this.delay.longValue(), this.isDouble.getValue(), this.id);
    }

    public void setCorX(int corX) {
        this.corX.set(corX);
    }

    public void setCorY(int corY) {
        this.corY.set(corY);
    }

    public void setDelay(long delay) {
        this.delay.set(delay);
    }

    public void setIsDouble(boolean isDouble) {
        this.isDouble.set(isDouble);
    }

    public int getCorX() {
        return corX.get();
    }

    public SimpleIntegerProperty corXProperty() {
        return corX;
    }

    public int getCorY() {
        return corY.get();
    }

    public SimpleIntegerProperty corYProperty() {
        return corY;
    }

    public long getDelay() {
        return delay.get();
    }

    public SimpleLongProperty delayProperty() {
        return delay;
    }

    public boolean isIsDouble() {
        return isDouble.get();
    }

    public SimpleBooleanProperty isDoubleProperty() {
        return isDouble;
    }
}
