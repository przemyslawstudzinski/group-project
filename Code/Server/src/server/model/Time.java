package server.model;

import javafx.beans.property.SimpleIntegerProperty;
import server.controller.StudyWindowController;

public class Time {

    private SimpleIntegerProperty seconds;

    public Time(StudyWindowController controller) {
        seconds = new SimpleIntegerProperty(0);
        seconds.addListener(controller);
    }

    @Override
    public String toString() {
        String seconds = String.valueOf(this.seconds.get() % 60);
        String minutes = String.valueOf(this.seconds.get() / 60 % 60);
        String hours = String.valueOf(this.seconds.get() / 60 / 60);
        seconds = seconds.length() == 1 ? "0" + seconds : seconds;
        minutes = minutes.length() == 1 ? "0" + minutes : minutes;
        hours = hours.length() == 1 ? "0" + hours : hours;
        return hours + ":" + minutes + ":" + seconds;
    }

    public void addSeconds(int i) {
        seconds.set(seconds.get() + i);
    }
}
