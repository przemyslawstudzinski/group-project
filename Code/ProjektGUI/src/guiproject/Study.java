package guiproject;

import java.util.List;

public class Study {

    String name;

    String lastName;

    String age;

    String teacher;

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
}
