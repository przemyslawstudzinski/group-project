package guiproject;

import server.Server;

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

    public void runThisStudy(Server server) throws IOException, InterruptedException {
        PrintWriter out = new PrintWriter(server.client.getSocket().getOutputStream(), true);
        for (Action a : this.getChosenScenario().getChosenActions()) {
            out.println("replay");
            //check if we want to block peripherials on client where action will run
            if (this.blockKeyboard)
                out.println("lockKeyboard");
            if (this.closeSystem)
                out.println("closeSystem");
            for (Node n : a.getNodes()) {
                out.println("click" + " " + n.getCorX() + " " + n.getCorY() + " " + n.getIsDouble());
                Thread.sleep(n.getDelay());
            }
            out.println("stopreplay");
            if (this.blockPeripherals && this.blockedPeripheralsOnReceivers.contains(a.getReceiver()))
                out.println("lockMouseAndKeyboard");
        }
        //save/log study
    }
}
