import java.awt.*;
import java.awt.event.InputEvent;

public class Clicker {

    private static Robot bot;

    public Clicker() throws AWTException {
        bot = new Robot();
    }

    public void click(int x, int y, boolean isDouble) {
        try {
            bot.mouseMove(x, y);
            bot.mousePress(InputEvent.BUTTON1_MASK);
            bot.mouseRelease(InputEvent.BUTTON1_MASK);
            if (isDouble) {
                System.out.println("double click");
                bot.mousePress(InputEvent.BUTTON1_MASK);
                bot.mouseRelease(InputEvent.BUTTON1_MASK);
            }
            System.out.println("Replayed click: " + x + " " + y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
