package com.example.theom.mmha.MySafety_Quiz;

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


public class JSON_parser{

    static JSONArray rows = null;
    static XML_parser xml_parser = new XML_parser();
    String JSON_File;
    JSONObject rootObject;
    String TAG = "JSON_Parser";
    Boolean leafNodeReached = false;

    public static void main(String[] args) throws FileNotFoundException, JSONException{

    }

    public void setupQuiz(Context ctx) {
        try {
            String jsonStr;
            InputStream JSONin = ctx.getResources().openRawResource(R.raw.finalresults);
            try {
                int count = 0;
                byte[] bytes = new byte[32768];
                StringBuilder builder = new StringBuilder();
                while ((count = JSONin.read(bytes, 0, 32768)) > 0) {
                    builder.append(new String(bytes, 0, count));
                }

                JSONin.close();
                jsonStr = builder.toString();
                rootObject = new JSONObject(jsonStr); // Parse the JSON to a JSONObject
                rows = rootObject.getJSONArray("suic-curr-int"); // Get all JSONArray rows
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch (JSONException e) {
            // JSON Parsing error
            e.printStackTrace();
        }
    }

    public QuestionObject runQuiz(String answer, Context ctx) throws JSONException {

        String nextQuestion = getNextQuestion(answer);
        NodeList nList = xml_parser.parseXML(ctx, R.raw.qt);
        QuestionObject question = xml_parser.getQuestionText(nList, nextQuestion);
        String fullTextQuestion = question.getQuestionText();
        String nodeInformation = "No node information";

        if (leafNodeReached == false) {
            try {
                rows = rows.getJSONObject(0).getJSONArray(answer).getJSONObject(0).getJSONArray(nextQuestion);
                if (question.getQuestionType().equals("layer") || question.getQuestionType().equals("nominal") || question.getQuestionType().equals("scale")) {
                    NodeList nListQuestionType = xml_parser.parseXML(ctx, R.raw.cat);
                    question = xml_parser.getQuestionFormat(nListQuestionType, nextQuestion, question);
                    System.out.println("Action: " + question.getQuestionAction() + "\nValue-mg = " + question.getQuestionMG());
                }

                return question;


            } catch (JSONException e) {

                nodeInformation = rows.getJSONObject(0).getJSONArray(answer).toString();
                Log.i(TAG, "Running tesat NODE information " + nodeInformation);
                leafNodeReached = true;
                // JSON Parsing error
                e.printStackTrace();
            }
        }

        QuestionObject leafQuestion = new QuestionObject("Leaf Node Reached", nodeInformation, "No Code", true);
        return leafQuestion;
    }


    public static String getNextQuestion(String userInput){
        String nextQuestion = "";
        try {
            for (int i = 0; i < rows.length(); i++) { // Loop over each each row
                JSONObject row = rows.getJSONObject(i); // Get row object
                JSONArray answer = row.getJSONArray(userInput);
                JSONObject child = answer.getJSONObject(0);
                Iterator<String> iterator = child.keys();
                while (iterator.hasNext()) {
                    nextQuestion = iterator.next();

                    System.out.println("Next Q: " + nextQuestion);

                }
            }
        } catch (JSONException e) {
            // JSON Parsing error
            e.printStackTrace();
        }
        return nextQuestion;
    }

    //method to call xml parser
    public String getQuestionText(String quetionCode){
        String questionText = new String();

        return questionText;
    }

    public static String readFile(String filename) {
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            result = sb.toString();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /*public void sendJsonFile(String jsonStr){
        try {
            rootObject = new JSONObject(jsonStr); // Parse the JSON to a JSONObject
        }catch (JSONException e) {
            // JSON Parsing error
            e.printStackTrace();
        }
    }*/
}