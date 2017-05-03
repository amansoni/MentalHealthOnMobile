package com.example.theom.mmha.Assessment;

import android.content.Context;
import android.util.Log;

import com.example.theom.mmha.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NodeList;

//Class to pase
public class JSON_parser {
    //JSON Array object that contains JSON Objects (the questions)
    static JSONArray rows = null;
    //Singleton XML Parser that is used to extract data for question text and supplementary information
    final static XML_parser xml_parser = new XML_parser();
    //JSON object that holds the current question text
    JSONObject currentJSONObject;
    //Node structure for the qt.xml (the question text)
    NodeList nList;
    //Flag for when leaf node is reached to stop parsing errors
    Boolean leafNodeReached = false;
    String TAG = "JSON_Parser";

    public static void main(String[] args) throws FileNotFoundException, JSONException {

    }

    //Parse JSON to setup the assessment
    public QuestionObject setupAssessment(Context ctx) {
        QuestionObject firstQuestion;
        try {
            try {
                //Read JSON data in
                InputStream JSONin = ctx.getResources().openRawResource(R.raw.question_tree);
                int count = 0;
                byte[] bytes = new byte[32768];
                StringBuilder builder = new StringBuilder();
                while ((count = JSONin.read(bytes, 0, 32768)) > 0) {
                    builder.append(new String(bytes, 0, count));
                }
                JSONin.close();
                //Assign input to String
                String jsonStr = builder.toString();
                // Parse the JSON to a JSONObject
                currentJSONObject = new JSONObject(jsonStr);
                // Get all JSONArray rows
                rows = currentJSONObject.getJSONArray("screening-tree");
                //Setup XML parser to extract question text
                nList = xml_parser.parseXML(ctx, R.raw.qt);
                //Get question code for the first question
                String firstQuestionText = getNextQuestionCode("first-question");
                //Get the question text using the code
                firstQuestion = xml_parser.getQuestionText(nList, firstQuestionText);

            } catch (IOException e) {
                firstQuestion = new QuestionObject("Couldn't setup assessment", "", "", false, "", "");
                e.printStackTrace();
            }
        } catch (JSONException e) {
            // JSON Parsing error
            firstQuestion = new QuestionObject("Couldn't setup assessment", "", "", false, "", "");
            e.printStackTrace();
        }

        return firstQuestion;
    }

    //Run the assessment, taking the user's input and getting the next question
    public QuestionObject progressAssessment(String answer, Context ctx) throws JSONException {
        QuestionObject question;
        String nextQuestion;
        //Get the question code, then the text
        nextQuestion = getNextQuestionCode(answer);
        question = xml_parser.getQuestionText(nList, nextQuestion);
        //If the question type matches any of the following, extract from cat.xml using XML_Parser
        if (question.getQuestionType().equals("layer") || question.getQuestionType().equals("nominal") || question.getQuestionType().equals("scale")) {
            NodeList nListQuestionType = xml_parser.parseXML(ctx, R.raw.cat);
            question = xml_parser.getQuestionFormat(nListQuestionType, nextQuestion, question);
        }
        //If the leaf node is reached, make sure that is indicated in the returned question to the parent QuestionFragment class
        if (leafNodeReached == true) {
            question.setLeadNodeResult(nextQuestion);
            question.setLeafNode(leafNodeReached);
        }
        return question;
    }

    //Get the next question code the user
    public String getNextQuestionCode(String userAnswer) {
        String nextQuestion = "";
        try {
            // Loop over each each row
            for (int i = 0; i < rows.length(); i++) {
                // Get row object
                JSONObject row = rows.getJSONObject(i);
                //Get next question code based on user's answer
                JSONArray result = row.getJSONArray(userAnswer);
                //Get the child that result
                JSONObject child = result.getJSONObject(0);
                Iterator<String> iterator = child.keys();
                while (iterator.hasNext()) {
                    nextQuestion = iterator.next();
                }
                //LOGIC TO DETERMINE IF IT'S A LEAF NODE - If the text doesn't contain 'prob, then it's an ordinary question
                if (!nextQuestion.contains("prob")) {
                    rows = rows.getJSONObject(0).getJSONArray(userAnswer).getJSONObject(0).getJSONArray(nextQuestion);
                } else {
                    //Else it's a leaf node and the contents of which should be extracted for a risk result
                    Log.i(TAG, "Leaf node contains " + nextQuestion);
                    nextQuestion = rows.getJSONObject(0).getJSONArray(userAnswer).getJSONObject(0).toString();
                    leafNodeReached = true;
                    break;
                }


            }
        } catch (JSONException e) {
            // JSON Parsing error
            e.printStackTrace();
        }

        return nextQuestion;
    }

}