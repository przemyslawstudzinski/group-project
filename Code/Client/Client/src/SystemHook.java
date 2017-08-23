import lc.kra.system.mouse.GlobalMouseHook;
import lc.kra.system.mouse.event.GlobalMouseAdapter;
import lc.kra.system.mouse.event.GlobalMouseEvent;

import java.util.Map;

public class SystemHook extends Thread {

    private static boolean run = true;
    private long clickTime = 0;
    private CoordsContainer coords;

    public void attachMouseListener()
    {
        GlobalMouseHook mouseHook = new GlobalMouseHook();
        System.out.println("Global mouse hook successfully started, press [middle] mouse button to shutdown.");
        coords = new CoordsContainer();

        mouseHook.addMouseListener(new GlobalMouseAdapter() {
            @Override public void mousePressed(GlobalMouseEvent event)  {
                if ((event.getButtons()&GlobalMouseEvent.BUTTON_LEFT) != GlobalMouseEvent.BUTTON_NO)
                {
                    System.out.println("Recorded click at: " + event.getX() + " " + event.getY());
                    coords.coordsMap.put(new CoordsContainer().new Coords(event.getX(),event.getY()),System.currentTimeMillis() - clickTime);
                    clickTime = System.currentTimeMillis();
                }
            }
        });

        try {
            while(run) Thread.sleep(10);
        } catch(InterruptedException e) { /* nothing to do here */ }
        finally { mouseHook.shutdownHook(); }
    }

    public void run(){
        System.out.println("SystemHook thread running");
        clickTime = System.currentTimeMillis();
        attachMouseListener();
    }

    public void printcoords() throws InterruptedException {
        for (Map.Entry<CoordsContainer.Coords, Long> entry : coords.coordsMap.entrySet())
        {
            System.out.println(entry.getKey().x + "  " + entry.getKey().y);
            Thread.sleep(entry.getValue());
        }
    }
}