package com.example.theom.mmha;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.example.theom.mmha.XML_parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NodeList;


public class JSON_parser{

    static JSONArray rows = null;
    static XML_parser xml_parser = new XML_parser();
    String JSON_File;
    JSONObject rootObject;

    public static void main(String[] args) throws FileNotFoundException, JSONException{

        /*Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("Enter in Yes/No answer: ");
        String answer = reader.next();
        String question=runQuiz(answer);
        System.out.println(question);*/
        //System.out.println("HI");
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

    public String runQuiz(String answer, Context ctx){
        try {

            String nextQuestion = getNextQuestion(answer);
            NodeList nList = xml_parser.parseXML(ctx);
            String fullTextQuestion = xml_parser.getQuestionText(nList, nextQuestion);
            rows = rows.getJSONObject(0).getJSONArray(answer).getJSONObject(0).getJSONArray(nextQuestion);
            return fullTextQuestion;


        } catch (JSONException e) {
            // JSON Parsing error
            e.printStackTrace();
        }
        return "Leaf Node Reached";
    }


    public static String getNextQuestion(String userinput){
        String nextQuestion = "";
        try {
            for (int i = 0; i < rows.length(); i++) { // Loop over each each row
                JSONObject row = rows.getJSONObject(i); // Get row object
                JSONArray answer = row.getJSONArray(userinput);
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