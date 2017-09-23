package gui.model;

import gui.controller.Controller;
import gui.utils.Server;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class Study {

    private String name;

    private String lastName;

    private String age;

    private String teacher;

    private boolean closeSystem;

    private Scenario chosenScenario;

    private boolean blockKeyboard = true;

    private boolean blockPeripherals;

    private List<Receiver> blockedPeripheralsOnReceivers;

    public Study() {
        this.blockedPeripheralsOnReceivers = new ArrayList<Receiver>();
    }


    public Study(String name, String lastName, String age, String teacher, Scenario chosenScenario) {
        this.blockedPeripheralsOnReceivers = new ArrayList<Receiver>();
        this.name = name;
        this.lastName = lastName;
        this.age = age;
        this.teacher = teacher;
        this.chosenScenario = chosenScenario;

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public void setChosenScenario(Scenario chosenScenario) {
        this.chosenScenario = chosenScenario;
    }

    public void setBlockPeripherals(boolean blockPeripherals) {
        this.blockPeripherals = blockPeripherals;
    }

    public void setBlockedPeripheralsOnReceivers(List<Receiver> blockedPeripheralsOnReceivers) {
        this.blockedPeripheralsOnReceivers = blockedPeripheralsOnReceivers;
    }

    public void setCloseSystem(boolean closeSystem) {
        this.closeSystem = closeSystem;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAge() {
        return age;
    }

    public String getTeacher() {
        return teacher;
    }

    public Scenario getChosenScenario() {
        return chosenScenario;
    }


    private boolean ReceiverAvailable(Receiver r) {
        if (Controller.server.connectedClientsMap.containsKey(r.getIpAddress()))
            return true;
        return false;
    }

    private void sendRunActionsSignals() throws IOException {
        for (Action a : this.getChosenScenario().getChosenActions()) {
            if (ReceiverAvailable(a.getReceiver())) {
                PrintWriter out = new PrintWriter(Controller.server.connectedClientsMap.get(a.getReceiver().getIpAddress()).getSocket().getOutputStream(), true);
                out.println("replay");
                if (this.blockKeyboard)
                    out.println("lockKeyboard");
                if (this.closeSystem)
                    out.println("closeSystem");
                for (Node n : a.getNodes()) {
                    out.println("click" + " " + n.getCorX() + " " + n.getCorY() + " " + n.getIsDouble() + " " + n.getDelay());
                }
                out.println("stopreplay");
                System.out.println("Details about action " + a.getName() + " have been sent to receiver " + a.getReceiver().getName() + "!");
            } else
                System.out.println("Can't run action " + a.getName() + " on receiver: " + a.getReceiver().getName() + "! Receiver is not connected to server!");
        }
    }

    private void sendLockPeripheralsSignals() throws IOException {
        if (this.blockPeripherals) {
            for (Receiver r : this.blockedPeripheralsOnReceivers) {
                if (ReceiverAvailable(r)) {
                    PrintWriter out = new PrintWriter(Controller.server.connectedClientsMap.get(r.getIpAddress()).getSocket().getOutputStream(), true);
                    out.println("lockMouseAndKeyboard");
                    System.out.println("LockPeripherals signal has been sent to receiver " + r.getName() + " !");
                } else
                    System.out.println("Can't send LockPeripherals signal to receiver " + r.getName() + "! Receiver is not connected to server!");
            }
        }
    }

    public void runThisStudy(Server server) throws IOException, InterruptedException {
        sendRunActionsSignals();
        sendLockPeripheralsSignals();
        //save/log study
    }
}
