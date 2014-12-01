package ru.csc.vindur.example.sample_db;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Andrey Kokorev Created on 29.11.2014.
 */
public class Cleaner {
    public static void main(String[] args) throws IOException {

        Path data = Paths.get("example_data");
        Files.list(data)
                .forEach(
                        (x) -> {
                            System.out.println("parsing " + x.toString());
                            DocumentBuilderFactory factory = DocumentBuilderFactory
                                    .newInstance();
                            try {
                                DocumentBuilder builder = factory
                                        .newDocumentBuilder();
                                org.w3c.dom.Document doc = builder
                                        .parse(new File(x.toString()));
                                NodeList offers = doc
                                        .getElementsByTagName("offer");
                                if (offers != null) {
                                    for (int i = 0, len = offers.getLength(); i < len; i++) {
                                        Node pics = ((Element) offers.item(i))
                                                .getElementsByTagName(
                                                        "pictures").item(0);
                                        Node url = ((Element) offers.item(i))
                                                .getElementsByTagName("url")
                                                .item(0);
                                        offers.item(i).removeChild(pics);
                                        offers.item(i).removeChild(url);
                                    }
                                }

                                TransformerFactory transformerFactory = TransformerFactory
                                        .newInstance();
                                Transformer transformer = transformerFactory
                                        .newTransformer();
                                DOMSource source = new DOMSource(doc);
                                StreamResult streamResult = new StreamResult(
                                        new File(x.toString()));
                                transformer.transform(source, streamResult);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
    }
}
