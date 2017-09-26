package server.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Node {

    private int corX;

    private int corY;

    private long delay;

    private boolean isDouble;

    public Node() {
    }

    public Node(int corX, int corY, long delay, boolean isDouble) {
        this.corX = corX;
        this.corY = corY;
        this.delay = delay;
        this.isDouble = isDouble;
    }

    public void setCorX(int corX) {
        this.corX = corX;
    }

    public void setCorY(int corY) {
        this.corY = corY;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void setIsDouble(boolean isDouble) {
        this.isDouble = isDouble;
    }

    public int getCorX() {
        return corX;
    }

    public int getCorY() {
        return corY;
    }

    public long getDelay() {
        return delay;
    }

    public boolean getIsDouble() {
        return isDouble;
    }
}
