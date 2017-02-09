package com.rabigol.mgnt;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.sql.*;

import static com.rabigol.mgnt.DbHelper.addFieldsToDb;
import static com.rabigol.mgnt.DbHelper.dropAndCreateTable;
import static com.rabigol.mgnt.DbHelper.getFields;

public class App {
    private static int n;
    private static long resultSum;
    private static String url;
    private static String login;
    private static String password;
    private static String workingDir = System.getProperty("user.dir");

    public static void main(String[] args) {
        long startTimeStamp = System.currentTimeMillis() / 1000;
        long endTimeStamp;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        DocumentBuilderFactory documentBuilderFactory = null;
        DocumentBuilder docBuilder = null;

        try {
            n = Integer.parseInt(args[0]);
            url = args[1];
            login = args[2];
            password = args[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.getStackTrace();
            System.out.println("Illegal run arguments. For example: \"hostname:port/dbname login password\"");
        }

        System.out.println("-------- Application starts ------------");

        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + url,
                    login,
                    password);
            // Drop and create table
            preparedStatement = connection.prepareStatement(dropAndCreateTable);
            preparedStatement.executeUpdate();

            // Generating fields and insert to DataBase
            preparedStatement = connection.prepareStatement(addFieldsToDb(n));
            preparedStatement.executeUpdate();

            // Getting resultset with fields from DataBase
            preparedStatement = connection.prepareStatement(getFields);
            ResultSet resultSet = preparedStatement.executeQuery();

            //Creating XML file
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilder = documentBuilderFactory.newDocumentBuilder();

            // Creating XML file. Root element
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("entries");
            doc.appendChild(rootElement);

            // Creating XML file. Entries elements
            while (resultSet.next()) {
                String fieldValue = String.valueOf(resultSet.getInt("field"));

                Element entry = doc.createElement("entry");
                rootElement.appendChild(entry);

                Element field = doc.createElement("field");
                entry.appendChild(field);

                field.appendChild(doc.createTextNode(fieldValue));
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(workingDir + "\\1.xml"));

            transformer.transform(source, result);
            System.out.println("1.xml file saved here " + workingDir);

            // Converting XML file
            Source xslDoc = new StreamSource(workingDir + "\\convert.xsl");
            Source xmlDoc = new StreamSource(workingDir + "\\1.xml");
            String outputFileName = workingDir + "\\2.xml";

            transformer = transformerFactory.newTransformer(xslDoc);
            result = new StreamResult(new File(outputFileName));
            transformer.transform(xmlDoc, result);
            System.out.println("2.xml file saved here " + workingDir);

            // Getting resultSum
            doc = docBuilder.parse(workingDir + "\\2.xml");
            NodeList nodeList = doc.getElementsByTagName("entry");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node a = nodeList.item(i).getAttributes().getNamedItem("field");
                int b = Integer.parseInt(a.getNodeValue());
                resultSum = resultSum + b;
            }
            System.out.println(resultSum);

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();

        } catch (ParserConfigurationException e) {
            System.out.println("Document creating failed!");
            e.printStackTrace();

        } catch (TransformerException e) {
            e.printStackTrace();

        } catch (SAXException e) {
            System.out.println("2.xml parsing error!");
            e.printStackTrace();

        } catch (IOException e) {
            System.out.println("2.xml not found!");
            e.printStackTrace();

        } finally {
            endTimeStamp = System.currentTimeMillis() / 1000;
            System.out.println("Runtime " + (endTimeStamp - startTimeStamp) + " seconds");
        }
    }
}
