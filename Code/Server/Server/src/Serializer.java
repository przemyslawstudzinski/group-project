import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

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
        Element click = doc.createElement("Click");
        click.appendChild(doc.createTextNode(resp));
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
        DOMSource src = new DOMSource(this.doc);
        StreamResult res = new StreamResult(new File(String.valueOf(num) + ".xml"));

        try {
            t.transform(src, res);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
