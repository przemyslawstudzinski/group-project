package server;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Deserializer {

    private int num;

    Deserializer(int n) {
        this.num = n;
    }

    public NodeList loadFile() throws ParserConfigurationException, IOException, SAXException {
        if (Files.exists(Paths.get("Actions"))) {
            File xmlFile = new File("Actions" + File.separator + String.valueOf(num) + ".xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList nodesList = doc.getElementsByTagName("Click");
            return nodesList;
        }
        return null;
    }
}
