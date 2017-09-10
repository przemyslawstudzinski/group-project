package guiproject;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Receiver {

    String name;

    String ipAddress;

    boolean blockedPeripherals;

    public Receiver() {
    }

    public Receiver(String name, String ipAddress) {
        this.name = name;
        this.ipAddress = ipAddress;
    }

    public Receiver(String name, String ipAddress, boolean blockedPeripherals) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.blockedPeripherals = blockedPeripherals;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setBlockedPeripherals(boolean blockedPeripherals) {
        this.blockedPeripherals = blockedPeripherals;
    }

    public String getName() {
        return name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public boolean isBlockedPeripherals() {
        return blockedPeripherals;
    }

    @Override
    public String toString() {
        return name;
    }
}
