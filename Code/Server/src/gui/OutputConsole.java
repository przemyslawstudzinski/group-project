package gui;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class OutputConsole {
    private final String fontFamily = "Helvetica";
    private final int size = 15;

    private TextFlow console;

    OutputConsole(TextFlow tf) {
        console = tf;
    }

    void writeLine(String s) {
        Text t = new Text("> " + s + "\n");
        t.setFont(Font.font(fontFamily, size));
        console.getChildren().add(t);
    }

    void writeErrorLine(String s) {
        Text t = new Text("> " + s + "\n");
        t.setFont(Font.font(fontFamily, size));
        t.setFill(Color.RED);
        console.getChildren().add(t);
    }
}
