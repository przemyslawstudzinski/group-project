package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.ArrayList;

import lc.kra.system.mouse.GlobalMouseHook;
import lc.kra.system.mouse.event.GlobalMouseAdapter;
import lc.kra.system.mouse.event.GlobalMouseEvent;

public class Main extends Application {

    private static boolean run = true;


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public static class SystemHookListener extends Thread {

        public static void attachMouseListener()
        {
            GlobalMouseHook mouseHook = new GlobalMouseHook(); // add true to the constructor, to switch to raw input mode
            System.out.println("Global mouse hook successfully started, press [middle] mouse button to shutdown. Connected mice:");

            mouseHook.addMouseListener(new GlobalMouseAdapter() {
                @Override public void mousePressed(GlobalMouseEvent event)  {
                    //System.out.println(event);
                    if((event.getButtons()&GlobalMouseEvent.BUTTON_LEFT)!=GlobalMouseEvent.BUTTON_NO
                            && (event.getButtons()&GlobalMouseEvent.BUTTON_RIGHT)!=GlobalMouseEvent.BUTTON_NO)
                        System.out.println("Both mouse buttons are currenlty pressed!");
                    if(event.getButton()==GlobalMouseEvent.BUTTON_MIDDLE)
                        run = false;
                    CoordsContainer.coordsList.add(new CoordsContainer().new Coords(event.getX(),event.getY()));
                }
            });

            try {
                while(run) Thread.sleep(128);
            } catch(InterruptedException e) { /* nothing to do here */ }
            finally { mouseHook.shutdownHook(); }
        }

        public void run(){
            System.out.println("MyThread running");
           attachMouseListener();
        }
    }

    public static void main(String[] args) throws InterruptedException, AWTException {
        SystemHookListener lis = new SystemHookListener();
        lis.start();
        Thread.sleep(50);
        launch(args);
    }
}
