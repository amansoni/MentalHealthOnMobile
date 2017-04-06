package com.example.theom.mmha.MySafety_Quiz;

/**
 * Created by Theo on 01/04/2017.
 */

public class QuestionObject {
    String questionText;
    String questionType;
    String questionAction;
    String questionMG;
    String questionCode;
    Boolean isLeafNode;

    public QuestionObject(String questionText, String questionType, String questionCode, Boolean isLeafNode){
        this.questionCode=questionCode;
        this.questionText=questionText;
        this.questionType=questionType;
        this.isLeafNode=isLeafNode;
    }

    public String getQuestionText(){
        return questionText;
    }

    public String getQuestionType(){
        return questionType;
    }

    public void setQuestionAction(String questionAction){
        this.questionAction=questionAction;
    }

    public String getQuestionAction(){
        return questionAction;
    }

    public void setQuestionMG(String questionMG){
        this.questionMG=questionMG;
    }

    public String getQuestionMG(){
        return questionMG;
    }

    public String getQuestionCode(){ return questionCode;}

    public Boolean isNode(){
        return isLeafNode;
    }
}
