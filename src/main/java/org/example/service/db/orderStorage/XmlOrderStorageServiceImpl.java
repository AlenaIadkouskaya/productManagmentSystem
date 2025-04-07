package org.example.service.db.orderStorage;

import org.example.config.ConfigReader;
import org.example.model.Order;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class XmlOrderStorageServiceImpl implements OrderStorageService {
    private final String ordersDirectory;

    public XmlOrderStorageServiceImpl() {
        ConfigReader configReader = new ConfigReader("application.yml");
        this.ordersDirectory = configReader.getOrdersDirectory();
    }

    @Override
    public String addOrders(List<Order> ordersList) {
        try {
            Document document = createDocument();
            String fileName = generateFileName();
            File xmlFile = new File(ordersDirectory, fileName);

            File directory = new File(ordersDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            Element root = getRootElement(xmlFile, document);

            for (Order order : ordersList) {
                addOrderToDocument(order, document, root);
            }

            writeDocumentToFile(document, xmlFile);
            return "Utworzono plik z " + ordersList.size() + " zamówieniami: " + xmlFile.getAbsolutePath();

        } catch (Exception e) {
            throw new RuntimeException("Błąd przy zapisie zamówień do XML", e);
        }
    }

    private Document createDocument() throws Exception {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        return documentBuilder.newDocument();
    }

    private String generateFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return "orders_" + dateFormat.format(new Date()) + ".xml";
    }

    private Element getRootElement(File xmlFile, Document document) throws Exception {
        if (xmlFile.exists()) {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
            return document.getDocumentElement();
        } else {
            Element root = document.createElement("orders");
            document.appendChild(root);
            return root;
        }
    }

    private void addOrderToDocument(Order order, Document document, Element root) {
        Element orderElement = document.createElement("order");

        Element orderName = createElementWithText(document, "name", order.getName());
        Element orderDate = createElementWithText(document, "date", order.getDate());
        Element orderPriceUSD = createElementWithText(document, "price_in_usd", String.valueOf(order.getPriceInUSD()));
        Element orderPricePLN = createElementWithText(document, "price_in_pln", String.valueOf(order.getPriceInPLN()));

        orderElement.appendChild(orderName);
        orderElement.appendChild(orderDate);
        orderElement.appendChild(orderPriceUSD);
        orderElement.appendChild(orderPricePLN);

        root.appendChild(orderElement);
    }

    private Element createElementWithText(Document document, String tagName, String textContent) {
        Element element = document.createElement(tagName);
        element.appendChild(document.createTextNode(textContent));
        return element;
    }

    private void writeDocumentToFile(Document document, File xmlFile) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult result = new StreamResult(xmlFile);
        DOMSource source = new DOMSource(document);
        transformer.transform(source, result);
    }
}