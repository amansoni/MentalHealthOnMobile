package com.example.theom.mmha.MySafety_Quiz;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.theom.mmha.MySafety_Quiz.Dialogs.DatePickerFragment;
import com.example.theom.mmha.MySafety_Quiz.Dialogs.EthnicityPickerDialog;
import com.example.theom.mmha.MySafety_Quiz.Dialogs.InfoDialog;
import com.example.theom.mmha.MySafety_Quiz.Dialogs.SetupDialog;
import com.example.theom.mmha.R;

/**
 * Created by theom on 03/04/2017.
 */

public class SetupAssessmentFragment extends Fragment implements
        View.OnClickListener,
        SetupDialog.OnSetRelationshipStatusListener,
        SetupDialog.OnSetMAPPAListener,
        DatePickerFragment.OnSetDateListener,
        EthnicityPickerDialog.OnSetEthnicityListener{

    private AnsweredQuestionsDBHelper answersDB;
    String TAG = "Assessment setup";
    private EditText datePickerEditText;
    private EditText relationshipStatusEditText;
    private EditText ethnicityEditText;
    private EditText mappaStatusEditText;

    private String relationshipStatus = "Null";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_assessment_setup, container, false);

        //Create database to store assessment answers
        answersDB = new AnsweredQuestionsDBHelper(getActivity());

        relationshipStatusEditText = (EditText) v.findViewById(R.id.relationshipStatus);
        relationshipStatusEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    showRelationshipsDialog();
                }
                return true;
            }
        });


        datePickerEditText = (EditText) v.findViewById(R.id.birthDate);
        datePickerEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    showDatePickerDialog();
                }
                return true;
            }
        });

        ethnicityEditText = (EditText) v.findViewById(R.id.ethnicity);
        ethnicityEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    showEthnicityPickerDialog();
                }
                return true;
            }
        });

        mappaStatusEditText = (EditText) v.findViewById(R.id.mappa_status);
        mappaStatusEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()){
                    showMappaDialog();
                }
                return true;
            }
        });

        Button startQuiz = (Button) v.findViewById(R.id.startQuiz);
        startQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  answersDB.insertData(assessmentTitle.getText().toString(), patientName.getText().toString(), interviewerName.getText().toString(), "", preSessionNotes.getText().toString());
                Fragment fragment = new QuestionFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.relativeLayout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return v;
    }

    public void showDatePickerDialog(){
        DialogFragment picker = new DatePickerFragment();
        picker.setTargetFragment(this, 0);
        picker.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void setDate(String dateOfBirth) {
        Log.i(TAG, "Date of birth"+ dateOfBirth);
        datePickerEditText.setText(dateOfBirth);
    }


    @Override
    public void setRelationshipStatus(String relationshipStatus) {
        this.relationshipStatus=relationshipStatus;
        Log.i(TAG, "Relationships: "+relationshipStatus);
        relationshipStatusEditText.setText(relationshipStatus);
    }

    public void showRelationshipsDialog(){
        SetupDialog relationshipsDialog = SetupDialog.newInstance("Relationships");
        relationshipsDialog.setTargetFragment(this, 0);
        relationshipsDialog.show(getActivity().getSupportFragmentManager(), "fragmentDialog");
    }

    public void showEthnicityPickerDialog(){
        EthnicityPickerDialog relationshipsDialog = EthnicityPickerDialog.newInstance("Ethnicity");
        relationshipsDialog.setTargetFragment(this, 0);
        relationshipsDialog.show(getActivity().getSupportFragmentManager(), "fragmentDialog");
    }

    public void showMappaDialog(){
        SetupDialog mappaDialog = SetupDialog.newInstance("MAPPA");
        mappaDialog.setTargetFragment(this, 0);
        mappaDialog.show(getActivity().getSupportFragmentManager(), "fragmentDialog");
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void setEthnicity(String ethnicity) {
        ethnicityEditText.setText(ethnicity);
    }

    @Override
    public void setMAPPA(String mappaStatus) {
        mappaStatusEditText.setText(mappaStatus);
    }
}
