package com.example.theom.mmha.Assessment;

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

//Class to parse the XML data held in qt.XML and cat.XML
public class XML_parser extends FragmentActivity{

    String TAG = "XML_Parser";

    public static void main(String argv[]) {

    }

    //Parse the XML data
    public NodeList parseXML(Context ctx, Integer xml_file){
        NodeList nList = null;
        try {
            try {
                //Read the XML input into the application
                InputStream XMLin = ctx.getResources().openRawResource(xml_file);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(new InputSource(XMLin));
                doc.getDocumentElement().normalize();
                nList = doc.getElementsByTagName("node");
            } catch (Exception e) {
                Log.i(TAG,"XML Parsing Exception = " + e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nList;
    }

    //Extract the question text and scale type from qt.xml
    public QuestionObject getQuestionText(NodeList nList, String questionID) {
        //If any of the extractions fail, there is still a value to be returned
        String questionText = "Couldn't find the question";
        String values = "Question type not found";
        String helpText= "No help info found";
        String scaleInformation = "No Information found";
        //For as many elements as there are in qt.xml
        for (int i = 0; i < nList.getLength(); i++) {
            Element e = (Element) nList.item(i);
            //If the code matches the current question code, then get all the other information (text, type)
            if (e.getAttribute("code").equals(questionID)) {
                questionText =  e.getAttribute("question");
                values = e.getAttribute("values");
                helpText = e.getAttribute("help");
                scaleInformation = e.getAttribute("scale-type");
            }
        }
        //Return a question object containing all the information
        QuestionObject question = new QuestionObject(questionText, values, questionID, false, helpText, scaleInformation);
        return question;
    }

    //If the question is of type nominal, scale or layer, then extract the other information associated with it from cat.xml
    public QuestionObject getQuestionFormat(NodeList nList, String questionID, QuestionObject question) {
            for (int i = 0; i < nList.getLength(); i++) {
                Element e = (Element) nList.item(i);
                String search_attribute = questionID;
                if (e.getAttribute("code").equals(search_attribute)) {
                    String action = e.getAttribute("action");
                    String value_mg = e.getAttribute("value-mg");
                    question.setQuestionAction(action);
                    question.setQuestionMG(value_mg);
                    break;
                }
            }

        return question;
    }
}
