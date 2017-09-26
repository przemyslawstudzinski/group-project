package server.utils;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class OutputConsole {

    private final String fontFamily = "Helvetica";

    private final int size = 15;

    private TextFlow console;

    public OutputConsole(ScrollPane scrollPane, TextFlow tf) {
        console = tf;
        console.getChildren().addListener(
                (ListChangeListener<Node>) ((change) -> {
                    console.layout();
                    scrollPane.layout();
                    scrollPane.setVvalue(1.0f);
                }));
        scrollPane.setContent(console);
    }

    public void writeLine(String s) {
        Text t = new Text("> " + s + "\n");
        t.setFont(Font.font(fontFamily, size));
        console.getChildren().add(t);
        console.requestFocus();
    }

    public void writeErrorLine(String s) {
        Text t = new Text("> " + s + "\n");
        t.setFont(Font.font(fontFamily, size));
        t.setFill(Color.RED);
        console.getChildren().add(t);
    }
}
