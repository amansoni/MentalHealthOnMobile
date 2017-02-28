package com.example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NodeList;

public class JSON_parser {

    static JSONArray rows = null;
    static XML_parser xml_parser = new XML_parser();

    public static void main(String[] args) throws FileNotFoundException, JSONException{
        String jsonStr = readFile("lib/src/main/java/files/finalresults.json");

        try {
            JSONObject rootObject = new JSONObject(jsonStr); // Parse the JSON to a JSONObject
            rows = rootObject.getJSONArray("suic-curr-int"); // Get all JSONArray rows
            System.out.println("suic-curr-int");

            String answer = "";

            while (!answer.equals("cancel")){
                Scanner reader = new Scanner(System.in);  // Reading from System.in
                System.out.println("Enter in Yes/No answer: ");
                answer = reader.next();
                String nextQuestion = getNextQuestion(answer);
                NodeList nList = xml_parser.parseXML();
                xml_parser.getQuestionText(nList, nextQuestion);
                rows = rows.getJSONObject(0).getJSONArray(answer).getJSONObject(0).getJSONArray(nextQuestion);
            }

        } catch (JSONException e) {
            // JSON Parsing error
            e.printStackTrace();
        }
    }

    public static String getNextQuestion(String userinput){
        String nextQuestion = "";
        for(int i=0; i < rows.length(); i++) { // Loop over each each row
            JSONObject row = rows.getJSONObject(i); // Get row object
            JSONArray answer =  row.getJSONArray(userinput);
            JSONObject child = answer.getJSONObject(0);
            Iterator<String> iterator = child.keys();
            while(iterator.hasNext()){
                nextQuestion=iterator.next();

                System.out.println("Next Q: "+nextQuestion);

            }
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


}