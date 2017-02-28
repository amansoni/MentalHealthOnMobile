package com.example.theom.mmha;

/**
 * Created by theom on 15/02/2017.
 */


import android.content.Context;
import android.support.v4.app.FragmentActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XML_parser extends FragmentActivity{

    public static void main(String argv[]) {

    }

    public NodeList parseXML(Context ctx){
        NodeList nList = null;
        try {
            try {
                InputStream XMLin = ctx.getResources().openRawResource(R.raw.qt);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(new InputSource(XMLin));
                doc.getDocumentElement().normalize();
                nList = doc.getElementsByTagName("node");
            } catch (Exception e) {
                System.out.println("XML Pasing Excpetion = " + e);
            }


           /*
            //File fXmlFile = new File("lib/src/main/java/files/staff.xml");
            File question_tree = new File("app/src/main/res/raw/qt.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(question_tree);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();
            nList = doc.getElementsByTagName("node");
*/
        /*Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("What's the question code? ");
        String question = reader.next();
        getQuestionText(question);*/

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
            if (e.getAttribute("code").equals(search_attribute)) {
                questionText =  e.getAttribute("question");
                System.out.println("Question: " + questionText);
            }
        }
        return questionText;
    }
}
