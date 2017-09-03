import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Serializer {

    private int num;
    public Document doc;
    public Element root;

    Serializer(int n) {
        this.num = n;
    }

    public void createDoc() {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        this.doc = docBuilder.newDocument();
        this.root = doc.createElement("Action");
        root.setAttribute("id", String.valueOf(num));
        doc.appendChild(root);
    }

    public void addChild(String resp) {

        String[] responseTokens = resp.split(" ");
        Element click = doc.createElement("Click");
        Element xCord = doc.createElement("X");
        xCord.appendChild(doc.createTextNode(responseTokens[0]));
        Element yCord = doc.createElement("Y");
        yCord.appendChild(doc.createTextNode(responseTokens[1]));
        Element delay = doc.createElement("Delay");
        delay.appendChild(doc.createTextNode(responseTokens[2]));

        click.appendChild(xCord);
        click.appendChild(yCord);
        click.appendChild(delay);
        root.appendChild(click);
    }

    public void saveFile() {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = null;
        try {
            t = tf.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource src = new DOMSource(this.doc);
        if (!Files.exists(Paths.get("Actions"))) {
            try {
                File dir = new File("Actions");
                dir.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        StreamResult res = new StreamResult(new File("Actions" + File.separator + String.valueOf(num) + ".xml"));

        try {
            t.transform(src, res);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public void loadFile()
    {

    }
}
