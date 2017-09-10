package guiproject;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Node {

    private int corX;

    private int corY;

    private int delay;

    public Node() {
    }

    public Node(int corX, int corY, int delay) {
        this.corX = corX;
        this.corY = corY;
        this.delay = delay;
    }

    public void setCorX(int corX) {
        this.corX = corX;
    }

    public void setCorY(int corY) {
        this.corY = corY;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getCorX() {
        return corX;
    }

    public int getCorY() {
        return corY;
    }

    public int getDelay() {
        return delay;
    }
}
