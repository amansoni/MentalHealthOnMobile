package com.example.theom.mmha.MySafety_Quiz;

/**
 * Created by theom on 15/02/2017.
 */


import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XML_parser extends FragmentActivity{

    String TAG = "XML_Parser";

    public static void main(String argv[]) {

    }

    public NodeList parseXML(Context ctx, Integer xml_file){
        NodeList nList = null;
        try {
            try {
                InputStream XMLin = ctx.getResources().openRawResource(xml_file);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(new InputSource(XMLin));
                doc.getDocumentElement().normalize();
                nList = doc.getElementsByTagName("node");
            } catch (Exception e) {
                System.out.println("XML Pasing Excpetion = " + e);
            }


           /*
            //File fXmlFile = new File("lib/src/fragment_question_scale/java/files/staff.xml");
            File question_tree = new File("app/src/fragment_question_scale/res/raw/qt.xml");
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

    public QuestionObject getQuestionText(NodeList nList, String questionID) {
        String questionText = "Couldn't find the question";
        String values = "Question type not found";
        String helpText= "No help info found";
        String scaleInformation = "No Information found";
        for (int i = 0; i < nList.getLength(); i++) {
            Element e = (Element) nList.item(i);
            String search_attribute = questionID;
            if (e.getAttribute("code").equals(search_attribute)) {
                questionText =  e.getAttribute("question");
                values = e.getAttribute("values");
                helpText = e.getAttribute("help");
                scaleInformation = e.getAttribute("scale-type");
                System.out.println("Question: " + questionText);
            }
        }
        QuestionObject question = new QuestionObject(questionText, values, questionID, false, helpText, scaleInformation);
        return question;
    }

    public QuestionObject getQuestionFormat(NodeList nList, String questionID, QuestionObject question) {
        String action = "Action not found";
        String value_mg = "Value_mg not found";
        Boolean valueFound = false;
       // while(valueFound == false) {
            for (int i = 0; i < nList.getLength(); i++) {
                Element e = (Element) nList.item(i);
                String search_attribute = questionID;
                if (e.getAttribute("code").equals(search_attribute)) {
                    action = e.getAttribute("action");
                    value_mg = e.getAttribute("value-mg");
                    question.setQuestionAction(action);
                    question.setQuestionMG(value_mg);
                    break;
                }
            }
       // }

        return question;
    }
}
