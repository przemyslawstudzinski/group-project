package server.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Receiver {

    private String name;

    private String ipAddress;

    public Receiver() {
    }

    public Receiver(String name, String ipAddress) {
        this.name = name;
        this.ipAddress = ipAddress;
    }

    public Receiver(String name, String ipAddress, boolean blockedPeripherals) {
        this.name = name;
        this.ipAddress = ipAddress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getName() {
        return name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Receiver))
            return false;
        Receiver r = (Receiver) obj;
        return this.name.equals(r.name);
    }
}
