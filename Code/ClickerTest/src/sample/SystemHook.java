package sample;

import lc.kra.system.mouse.GlobalMouseHook;
import lc.kra.system.mouse.event.GlobalMouseAdapter;
import lc.kra.system.mouse.event.GlobalMouseEvent;

public class SystemHook extends Thread {

    private static boolean run = true;
    private long clickTime = 0;

    public void attachMouseListener()
    {
        GlobalMouseHook mouseHook = new GlobalMouseHook();
        System.out.println("Global mouse hook successfully started, press [middle] mouse button to shutdown.");

        mouseHook.addMouseListener(new GlobalMouseAdapter() {
            @Override public void mousePressed(GlobalMouseEvent event)  {
                if ((event.getButtons()&GlobalMouseEvent.BUTTON_LEFT) !=GlobalMouseEvent.BUTTON_NO)
                {
                    long delay = System.currentTimeMillis() - clickTime;
                    System.out.println("Recorded click at: " + event.getX() + " " + event.getY() + " " + delay);
                    if (delay > 600)
                        ClicksContainer.clicksList.add(new ClicksContainer().new Click(event.getX(),event.getY(),delay,false));
                    else
                    {
                        ClicksContainer.Click doubleClick = ClicksContainer.clicksList.remove(ClicksContainer.clicksList.size() - 1);
                        doubleClick.isDouble = true;
                        ClicksContainer.clicksList.add(doubleClick);
                    }
                    clickTime = System.currentTimeMillis();
                }
            }
        });

        try {
            while(run) Thread.sleep(1);
        } catch(InterruptedException e) { /* nothing to do here */ }
        finally {
            mouseHook.shutdownHook();
            run = false;
        }
    }

    public void run(){
        System.out.println("SystemHook thread running");
        clickTime = System.currentTimeMillis();
        attachMouseListener();
    }
}