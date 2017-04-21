package com.example.theom.mmha.PreviousAssessments;


import java.io.Serializable;


/**
 * Created by theom on 15/03/2017.
 */

//Object to store all the details for a given favourite, used in cardview
public class PrevAssessmentListItem implements Serializable {
    String id;
    String dateOfAssessment;
    String timeOfAssessment;
    String location;

    public PrevAssessmentListItem(String id, String dateOfAssessment, String location) {
        this.id = id;
        setTimeOfAssessment(dateOfAssessment);
        this.location = location;
    }

    public String getID() {
        return id;
    }

    public String getDateOfAssessment() {
        return dateOfAssessment;
    }

    public String getGender() {
        return location;
    }

    public void setTimeOfAssessment(String dateOfAssessment) {
        String[] times = dateOfAssessment.split("\\s+");
        this.dateOfAssessment = times[0];
        String[] timesOfAssessment = times[1].split("\\:");
        timeOfAssessment = timesOfAssessment[0] + ":" + timesOfAssessment[1];
    }

    public String getLocation() {
        String[] returnLocation = location.split("\\:");
        return returnLocation[0];
    }
}
