package com.example.theom.mmha.MySafety_Quiz;

import android.content.Context;
import android.util.Log;

import com.example.theom.mmha.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NodeList;


public class JSON_parser {

    static JSONArray rows = null;
    static XML_parser xml_parser = new XML_parser();
    String JSON_File;
    JSONObject rootObject;
    String TAG = "JSON_Parser";
    NodeList nList;
    QuestionObject firstQuestion;
    Boolean leafNodeReached = false;

    public static void main(String[] args) throws FileNotFoundException, JSONException {

    }

    public QuestionObject setupQuiz(Context ctx) {
        QuestionObject question;
        try {
            try {

                String jsonStr;
                InputStream JSONin = ctx.getResources().openRawResource(R.raw.question_tree_wscale_nd_leafs);
                int count = 0;
                byte[] bytes = new byte[32768];
                StringBuilder builder = new StringBuilder();
                while ((count = JSONin.read(bytes, 0, 32768)) > 0) {
                    builder.append(new String(bytes, 0, count));
                }

                JSONin.close();
                jsonStr = builder.toString();
                rootObject = new JSONObject(jsonStr); // Parse the JSON to a JSONObject

                rows = rootObject.getJSONArray("screening-tree"); // Get all JSONArray rows

                nList = xml_parser.parseXML(ctx, R.raw.qt);

                String firstQuestionText = getNextQuestionCode("first-question");
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

    public QuestionObject runAssessment(String answer, Context ctx) throws JSONException {
        QuestionObject question;
        String nextQuestion;
        String nodeInformation = "No node information";

        nextQuestion = getNextQuestionCode(answer);
        question = xml_parser.getQuestionText(nList, nextQuestion);

        if (question.getQuestionType().equals("layer") || question.getQuestionType().equals("nominal") || question.getQuestionType().equals("scale")) {
            NodeList nListQuestionType = xml_parser.parseXML(ctx, R.raw.cat);
            question = xml_parser.getQuestionFormat(nListQuestionType, nextQuestion, question);
        }

        if (leafNodeReached == true) {
            question.setLeadNodeResult(nextQuestion);
            question.setLeafNode(leafNodeReached);
        }
        return question;
    }


    public String getNextQuestionCode(String userAnswer) {
        String nextQuestion = "";
        try {
            for (int i = 0; i < rows.length(); i++) { // Loop over each each row
                JSONObject row = rows.getJSONObject(i); // Get row object
                JSONArray result = row.getJSONArray(userAnswer);
                JSONObject child = result.getJSONObject(0);
                Iterator<String> iterator = child.keys();
                while (iterator.hasNext()) {
                    nextQuestion = iterator.next();
                }

                if (!nextQuestion.contains("prob")) {
                    rows = rows.getJSONObject(0).getJSONArray(userAnswer).getJSONObject(0).getJSONArray(nextQuestion);
                } else {
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

    //method to call xml parser
    public String getQuestionText(String quetionCode) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}