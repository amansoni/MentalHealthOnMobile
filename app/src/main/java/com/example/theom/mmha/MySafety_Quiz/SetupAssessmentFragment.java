package com.example.theom.mmha.MySafety_Quiz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.theom.mmha.MySafety_Quiz.Dialogs.InfoDialog;
import com.example.theom.mmha.MySafety_Quiz.Dialogs.SetupDialog;
import com.example.theom.mmha.R;

/**
 * Created by theom on 03/04/2017.
 */

public class SetupAssessmentFragment extends Fragment implements View.OnClickListener, SetupDialog.OnSetRelationshipStatusListener{

    private AnsweredQuestionsDBHelper answersDB;
    private EditText assessmentTitle;
    private EditText patientName;
    private EditText interviewerName;
    private EditText preSessionNotes;
    String TAG = "Assessment setup";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_assessment_setup, container, false);

        //Create database to store assessment answers
        answersDB = new AnsweredQuestionsDBHelper(getActivity());

        EditText assessmentTitle = (EditText) v.findViewById(R.id.relationshipStatus);
        assessmentTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    SetupDialog relationshipsDialog = SetupDialog.newInstance("Relationships");
                    relationshipsDialog.setTargetFragment(getParentFragment(), 0);
                    relationshipsDialog.show(getActivity().getSupportFragmentManager(), "fragmentDialog");
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

    @Override
    public void setSearchLocationType(String relationshipStatus) {

    }

    @Override
    public void onClick(View v) {

    }
}
