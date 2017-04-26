package com.example.theom.mmha.PreviousAssessments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.theom.mmha.DbBitmapUtility;
import com.example.theom.mmha.Assessment.Dialogs.InfoDialog;
import com.example.theom.mmha.Assessment.QuestionObject;
import com.example.theom.mmha.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by theom on 19/04/2017.
 */

public class AssessmentDetailsFragment extends Fragment {

    private AnsweredQuestionsDBHelper answersDB;
    private String id;
    private TextView id_textview;
    private TextView dateOfBirthTextView;
    private TextView genderTextView;
    private TextView relationshipTextView;
    private TextView ethnicityTextView;
    private TextView locationTextView;
    private TextView answersTextView;
    private TextView dateOfAssessmentTextView;
    private TextView riskResultTextView;
    private String TAG = "AssessDetails";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_prev_assessment, container, false);

        id = getArguments().getString("id");

        //Create database to store favourite locations
        answersDB = new AnsweredQuestionsDBHelper(getActivity());

        id_textview = (TextView) v.findViewById(R.id.id_textview);
        id_textview.setText(id);
        dateOfBirthTextView = (TextView) v.findViewById(R.id.date_of_birth);

        genderTextView = (TextView) v.findViewById(R.id.gender);
        relationshipTextView = (TextView) v.findViewById(R.id.relationship);
        ethnicityTextView = (TextView) v.findViewById(R.id.ethnicity);
        locationTextView = (TextView) v.findViewById(R.id.location);
        answersTextView = (TextView) v.findViewById(R.id.answers);
        dateOfAssessmentTextView = (TextView) v.findViewById(R.id.date_of_assessment);
        riskResultTextView = (TextView) v.findViewById(R.id.risk_result);

        getDB();

        return v;
    }

    private ArrayList<PrevAssessmentListItem> getDB() {
        ArrayList<PrevAssessmentListItem> data = new ArrayList<>();
        Cursor res = answersDB.getAssessmentDetails(id);
        StringBuffer dbContents = new StringBuffer();

        setHasOptionsMenu(true);

        if (res.getCount() == 0) {
            DbBitmapUtility dbBitmapUtility = new DbBitmapUtility();
            String titleText = "There are no previous assessments to display";
            PrevAssessmentListItem current = new PrevAssessmentListItem("0", "0000-00-00 00:00:00", "");
            data.add(current);
        } else {
            while (res.moveToNext()) {
                String dateOfBirth = res.getString(1);
                String gender = res.getString(2);
                String relationship = res.getString(3);
                String ethnicity = res.getString(4);
                String location = res.getString(5).split("\\:")[0];
                String dateOfAssessment = res.getString(7);
                String answers = res.getString(8);
                String risk = res.getString(9);

                dateOfBirthTextView.setText(dateOfBirth);
                genderTextView.setText(gender);
                relationshipTextView.setText(relationship);
                ethnicityTextView.setText(ethnicity);
                locationTextView.setText(location);
                dateOfAssessmentTextView.setText(dateOfAssessment);
                answersTextView.setText(formatAnswer(answers));
                riskResultTextView.setText(risk);

                PrevAssessmentListItem current = new PrevAssessmentListItem(id, dateOfAssessment, gender);
                data.add(current);
            }
            Log.i("Assessment_list", dbContents.toString());
        }
        return data;
    }

    private String formatAnswer(String answers) {
        //To decode JSON for user answers
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        Bundle bundle = new Bundle();
        bundle.putString("key", answers);
        HashMap<String, String> newUserAnswersHashMap = gson.fromJson(bundle.getString("key"), type);
        if (newUserAnswersHashMap == null) {
            return "No user answers";
        } else {
            return printMap(newUserAnswersHashMap);
        }
    }

    public static String printMap(Map mp) {
        Iterator it = mp.entrySet().iterator();
        String answerString = "";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            it.remove(); // avoids a ConcurrentModificationException
            answerString = answerString + pair.getKey() + "\n" + pair.getValue() + "\n"+ "\n";
        }
        return answerString;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.assessement_details_menu, menu);

        MenuItem helpMenu = menu.findItem(R.id.menu_delete_assessment);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_delete_assessment:
                InfoDialog deleteAssessment = InfoDialog.newInstance("Delete Assessment", new QuestionObject("","","",false,"",""), id);
                deleteAssessment.setTargetFragment(this, 0);
                deleteAssessment.show(getActivity().getSupportFragmentManager(), "fragmentDialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
