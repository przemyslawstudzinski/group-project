package guiproject;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class Study {

    String name;

    String lastName;

    String age;

    String teacher;

    boolean closeSystem;

    Scenario chosenScenario;

    List<Receiver> blockedPeripheralsOnReceivers;

    public Study() {
    }

    public Study(String name, String lastName, String age, String teacher, Scenario chosenScenario) {
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

    public List<Receiver> getBlockedPeripheralsOnReceivers() {
        return blockedPeripheralsOnReceivers;
    }

    public boolean isCloseSystem() {
        return closeSystem;
    }
}
