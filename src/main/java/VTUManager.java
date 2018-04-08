import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.io.File;

public class VTUManager {
    public Node root;
    public void getVTU(File file) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            root = document.getDocumentElement();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace(System.out);
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        } catch (SAXException ex) {
            ex.printStackTrace(System.out);
        }
    }

    public String printXML() {
        StringBuilder str = null;
        NodeList text = root.getChildNodes();
        for (int i = 0; i < text.getLength(); i++) {
            Node infoString = text.item(i);
            str.append(infoString.getNodeName()).append(":").append(infoString.getChildNodes().item(0).getTextContent());
        }
        return str.toString();
    }

    VTUManager(File file) {
        getVTU(file);
    }

    VTUManager() {

    }
}
