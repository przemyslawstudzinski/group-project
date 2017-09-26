package server.model;

import server.controller.Controller;
import server.utils.OutputConsole;
import server.utils.Server;
import server.utils.StudyThread;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class Study {

    private String name;

    private String lastName;

    private String age;

    private String teacher;

    private String startTime;

    private String endTime;

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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getAge() {
        return age;
    }

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        return name;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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
            console.writeLine("Uruchomiono badanie!");
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

    public void saveLog() {
        try {
            String startDate = startTime.replace(".", "_");
            String endDate = endTime.replace(".", "_");
            String[] startDates = startDate.split("_", 6);
            String[] endDates = endDate.split("_", 6);
            PrintStream out = new PrintStream(new FileOutputStream(Controller.studiesPath + File.separator + this.name + "_" + this.lastName + "_" +
                    startDates[0] + "_" + startDates[1] + "_" + startDates[2] + ".txt"));
            String log = "----------------------- LOG BADANIA ----------------------- \n" +
                    "Data rozpoczęcia: " + startDates[0] + "." + startDates[1] + "." + startDates[2] + "\n" +
                    "Godzina rozpoczęcia: " + startDates[3] + "." + startDates[4] + "." + startDates[5] + "\n" +
                    "Opiekun badania: " + this.teacher + "\n" +
                    "Badana osoba: " + this.name + " " + this.lastName + ", " + this.age + " lat \n" +
                    "Scenariusz: " + this.chosenScenario.getName() + "\n" +
                    "Data zakończenia: " + endDates[0] + "." + endDates[1] + "." + endDates[2] + "\n" +
                    "Godzina zakończenia: " + endDates[3] + "." + endDates[4] + "." + endDates[5] + "\n" +
                    "----------------------- LOG BADANIA -----------------------";
            out.write(log.getBytes());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
