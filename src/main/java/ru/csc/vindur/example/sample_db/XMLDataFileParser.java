package ru.csc.vindur.example.sample_db;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Andrey Kokorev Created on 28.11.2014.
 */
public class XMLDataFileParser {
    List<Map<String, List<Object>>> result = new ArrayList<>();

    public XMLDataFileParser(String file) {
        parse(file);
    }

    private void parse(String file) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document document = builder.parse(new File(file));
            Element root = document.getDocumentElement();
            NodeList nodes = root.getElementsByTagName("offer");

            if (nodes != null) {
                for (int i = 0, len = nodes.getLength(); i < len; i++) {
                    parseElement((Element) nodes.item(i));
                }
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseElement(Element element) {
        Map<String, List<Object>> elementMap = new HashMap<>();
        String title = getValueOrDeafult(element, "title", "");
        String model = getValueOrDeafult(element, "model", "");
        String brand = getValueOrDeafult(element, "marka", "");

        String categoryName = getValueOrDeafult(element, "catalog", "Other");

        String priceLow = getValueOrDeafult(element, "price1", "0");
        String priceHigh = getValueOrDeafult(element, "price2",
                Integer.toString(Integer.MAX_VALUE));

        String description = getValueOrDeafult(element, "description", "0");

        elementMap.put("title", Arrays.asList(title));
        elementMap.put("model", Arrays.asList(model));
        elementMap.put("brand", Arrays.asList(brand));
        elementMap.put("categoryName", Arrays.asList(categoryName));
        elementMap.put("priceLow", Arrays.asList(Integer.parseInt(priceLow)));
        elementMap.put("priceHigh", Arrays.asList(Integer.parseInt(priceHigh)));
        elementMap.put("description", Arrays.asList(description));

        Element specs = (Element) element.getElementsByTagName("specification")
                .item(0);
        if (specs != null) {
            NodeList items = specs.getElementsByTagName("item");
            if (items != null) {
                for (int i = 0, len = items.getLength(); i < len; i++) {
                    Element elem = (Element) items.item(i);
                    String name = elem.getElementsByTagName("name").item(0)
                            .getFirstChild().getNodeValue();
                    String value = elem.getElementsByTagName("value").item(0)
                            .getFirstChild().getNodeValue();
                    elementMap.put(
                            name,
                            Arrays.asList(value.replaceAll("\\s+", " ").split(
                                    ",")));
                }
            }
        }

        result.add(elementMap);
    }

    private String getValueOrDeafult(Element element, String valueName,
            String def) {
        Node n = element.getElementsByTagName(valueName).item(0);
        if (n == null)
            return def;
        return n.getFirstChild().getNodeValue();
    }

    public List<Map<String, List<Object>>> getEntities() {
        return result;
    }
}
