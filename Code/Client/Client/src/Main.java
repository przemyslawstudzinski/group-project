import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class Main {

    private static SystemTray systemTray = SystemTray.getSystemTray();
    private static Client client = new Client();
    private static Image appIcon;

    private static void addTrayIcon() {
        appIcon = Toolkit.getDefaultToolkit().getImage("Dependencies" + File.separator + "Images" + File.separator + "icon.png");
        PopupMenu trayPopupMenu = new PopupMenu();
        MenuItem close = new MenuItem("Close");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        trayPopupMenu.add(close);
        TrayIcon trayIcon = new TrayIcon(appIcon, "Client", trayPopupMenu);
        trayIcon.setImageAutoSize(true);
        try {
            systemTray.add(trayIcon);
        } catch (AWTException awtException) {
            awtException.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        addTrayIcon();
        client.start();
    }
}
