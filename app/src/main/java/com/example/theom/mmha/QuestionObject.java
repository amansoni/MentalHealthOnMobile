package com.example.theom.mmha;

/**
 * Created by Theo on 01/04/2017.
 */

public class QuestionObject {
    String questionText;
    String questionType;
    String questionAction;
    String questionMG;

    public QuestionObject(String questionText, String questionType){
        this.questionText=questionText;
        this.questionType=questionType;
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
}
