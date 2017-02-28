package com.example;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.*;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XML_parser {

    public static void main(String argv[]) {

    }

    public NodeList parseXML(){
        NodeList nList = null;
        try {
            //File fXmlFile = new File("lib/src/main/java/files/staff.xml");
            File question_tree = new File("lib/src/main/java/files/qt.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(question_tree);
            doc.getDocumentElement().normalize();
            nList = doc.getElementsByTagName("node");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return nList;
    }

    public String getQuestionText(NodeList nList, String questionID) {
        String questionText = "Couldn't find the question";
        for (int i = 0; i < nList.getLength(); i++) {
            Element e = (Element) nList.item(i);
            String search_attribute = questionID;
            //String search_attribute = questionCode;
            if (e.getAttribute("code").equals(search_attribute)) {
                questionText =  e.getAttribute("question");
                System.out.println("Question: " + questionText);
            }
        }
        return questionText;
    }
}
