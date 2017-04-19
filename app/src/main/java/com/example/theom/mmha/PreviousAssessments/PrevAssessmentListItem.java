package com.example.theom.mmha.PreviousAssessments;


import java.io.Serializable;


/**
 * Created by theom on 15/03/2017.
 */

//Object to store all the details for a given favourite, used in cardview
public class PrevAssessmentListItem implements Serializable
{
    String id;
    String dateOfAssessment;
    String timeOfAssessment;
    String gender;

    public PrevAssessmentListItem(String id, String dateOfAssessment, String gender){
        this.id=id;
        setTimeOfAssessment(dateOfAssessment);
        this.gender=gender;
    }

    public String getID(){
        return id;
    }

    public String getDateOfAssessment(){
        return dateOfAssessment;
    }

    public String getGender(){return gender;}

    public void setTimeOfAssessment(String dateOfAssessment){
        String[] times = dateOfAssessment.split("\\s+");
        this.dateOfAssessment=times[0];
        timeOfAssessment=times[1];
    }
}
