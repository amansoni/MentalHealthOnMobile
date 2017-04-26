package com.example.theom.mmha.Assessment;

/**
 * Created by Theo on 01/04/2017.
 */

public class QuestionObject {
    String questionText;
    String questionType;
    String questionAction;
    String questionMG;
    String helpText;
    String questionCode;
    Boolean isLeafNode;
    String scaleInformation;
    String leadNodeResult;

    public QuestionObject(String questionText, String questionType, String questionCode, Boolean isLeafNode, String helpText, String scaleInformation){
        this.questionCode=questionCode;
        this.questionText=questionText;
        this.questionType=questionType;
        this.isLeafNode=isLeafNode;
        this.helpText=helpText;
        this.scaleInformation=scaleInformation;
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

    public Boolean isLeafNode(){
        return isLeafNode;
    }

    public void setLeafNode(Boolean isLeafNode){this.isLeafNode = isLeafNode;}

    public void setLeadNodeResult(String leadNodeResult){this.leadNodeResult = leadNodeResult;}

    public String getLeafNodeResult(){return leadNodeResult;}

    public String getQuestionHelp(){return helpText;}

    public String getScaleInformation(){return scaleInformation;}
}
