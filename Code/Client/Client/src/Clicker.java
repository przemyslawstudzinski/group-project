import java.awt.*;
import java.awt.event.InputEvent;

public class Clicker   {

    private static Robot bot;

    public Clicker() throws AWTException {
        bot = new Robot();
    }
    public void click(int x, int y, boolean isDouble)
    {
        try
        {
            bot.mouseMove(x, y);
            bot.mousePress(InputEvent.BUTTON1_MASK);
            bot.mouseRelease(InputEvent.BUTTON1_MASK);
            //System.out.println("Delay: " + delay);
            if (isDouble)
            {
                System.out.println("double click");
                //Thread.sleep(1);
                bot.mousePress( InputEvent.BUTTON1_MASK );
                bot.mouseRelease( InputEvent.BUTTON1_MASK );
            }
            //System.out.println("Replayed click: " + x + " " + y);
            //Thread.sleep(delay);
        }
        catch ( Exception e) {
            e.printStackTrace();
        }
    }

}
