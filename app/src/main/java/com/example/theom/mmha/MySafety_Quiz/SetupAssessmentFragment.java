package com.example.theom.mmha.MySafety_Quiz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.theom.mmha.R;

/**
 * Created by theom on 03/04/2017.
 */

public class SetupAssessmentFragment extends Fragment{

    private AnsweredQuestionsDBHelper answersDB;
    private EditText assessmentTitle;
    private EditText patientName;
    private EditText interviewerName;
    private EditText preSessionNotes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_assessment_setup, container, false);

        //Create database to store assessment answers
        answersDB = new AnsweredQuestionsDBHelper(getActivity());


        final EditText assessmentTitle = (EditText) v.findViewById(R.id.assessmentTitle);
        final EditText patientName = (EditText) v.findViewById(R.id.patientName);
        final EditText interviewerName = (EditText) v.findViewById(R.id.interviewerName);
        final EditText preSessionNotes = (EditText) v.findViewById(R.id.preSessionNotes);

        Button startQuiz = (Button) v.findViewById(R.id.startQuiz);
        startQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answersDB.insertData(assessmentTitle.getText().toString(), patientName.getText().toString(), interviewerName.getText().toString(), "", preSessionNotes.getText().toString());
                Fragment fragment = new QuestionFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.relativeLayout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return v;
    }
}
