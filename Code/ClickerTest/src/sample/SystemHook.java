package sample;

import lc.kra.system.mouse.GlobalMouseHook;
import lc.kra.system.mouse.event.GlobalMouseAdapter;
import lc.kra.system.mouse.event.GlobalMouseEvent;

public class SystemHook extends Thread {

    private static boolean run = true;
    private long clickTime = 0;

    public void attachMouseListener()
    {
        GlobalMouseHook mouseHook = new GlobalMouseHook(); // add true to the constructor, to switch to raw input mode
        System.out.println("Global mouse hook successfully started, press [middle] mouse button to shutdown.");

        mouseHook.addMouseListener(new GlobalMouseAdapter() {
            @Override public void mousePressed(GlobalMouseEvent event)  {
                if ((event.getButtons()&GlobalMouseEvent.BUTTON_LEFT) !=GlobalMouseEvent.BUTTON_NO)
                {
                    CoordsContainer.coordsMap.put(new CoordsContainer().new Coords(event.getX(),event.getY()),System.currentTimeMillis() - clickTime);
                    clickTime = System.currentTimeMillis();
                }

                else if((event.getButtons()&GlobalMouseEvent.BUTTON_LEFT)!=GlobalMouseEvent.BUTTON_NO
                        && (event.getButtons()&GlobalMouseEvent.BUTTON_RIGHT)!=GlobalMouseEvent.BUTTON_NO)
                    System.out.println("Both mouse buttons are currenlty pressed!");
                else if(event.getButton()==GlobalMouseEvent.BUTTON_MIDDLE)
                    run = false;

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
}