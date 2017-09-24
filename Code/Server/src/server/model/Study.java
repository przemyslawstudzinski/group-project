package server.model;

import server.controller.Controller;
import server.utils.OutputConsole;
import server.utils.Server;
import server.utils.StudyThread;

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

    private Scenario chosenScenario;

    public boolean blockPeripherals;

    public List<Receiver> blockedPeripheralsOnReceivers;

    public Study() {
        this.blockedPeripheralsOnReceivers = new ArrayList<Receiver>();
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

    public Scenario getChosenScenario() {
        return chosenScenario;
    }

    private boolean allReceiversAvailable() {
        for (Action a : this.getChosenScenario().getChosenActions()) {
            Receiver r = a.getReceiver();
            if (!Controller.server.connectedClientsMap.containsKey(r.getIpAddress()))
                return false;
        }
        return true;
    }


    private void sendDataToReceivers(OutputConsole console) throws IOException {
        if (allReceiversAvailable()) {
            for (Action a : this.getChosenScenario().getChosenActions()) {
                {
                    PrintWriter out = new PrintWriter(Controller.server.connectedClientsMap.get(a.getReceiver().getIpAddress()).getSocket().getOutputStream(), true);
                    out.println("replay");
                    out.println("lockKeyboard");
                    for (Node n : a.getNodes()) {
                        out.println("click" + " " + n.getCorX() + " " + n.getCorY() + " " + n.getIsDouble() + " " + n.getDelay());
                    }
                    out.println("stopreplay");
                }
            }
            sendLockPeripheralsSignals();
            console.writeErrorLine("Uruchomiono badanie!");
        } else {
            StudyThread.canRunStudy = false;
            console.writeErrorLine("Nie można uruchomić badania, gdyż jeden z odbiorców akcji nie jest połączony z serwerem");
        }
    }

    private void sendLockPeripheralsSignals() throws IOException {
        if (this.blockPeripherals) {
            for (Receiver r : this.blockedPeripheralsOnReceivers) {
               PrintWriter out = new PrintWriter(Controller.server.connectedClientsMap.get(r.getIpAddress()).getSocket().getOutputStream(), true);
                out.println("lockMouseAndKeyboard");
            }
        }
    }

    public void runThisStudy(OutputConsole console) throws IOException, InterruptedException {
        sendDataToReceivers(console);
    }
}
