package com.example.theom.mmha.Assessment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.theom.mmha.BitmapUtility;
import com.example.theom.mmha.LocalSerivces.Places.PlacesList;
import com.example.theom.mmha.PreviousAssessments.AnsweredQuestionsDBHelper;
import com.example.theom.mmha.PreviousAssessments.PrevAssessmentListItem;
import com.example.theom.mmha.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by theom on 17/04/2017.
 */

public class AssessmentFinishFragment extends Fragment {

    private AnsweredQuestionsDBHelper answersDB;
    private LatLng location;
    private String id;
    private String TAG = "AssessmentFinish";
    private Boolean veryHighRiskSet = false;
    private Boolean highRiskSet = false;
    private Boolean mediumRiskSet = false;
    private Boolean lowRiskSet = false;

    private TextView leafNodeResultTxtView;
    private Button actionButton1;
    private TextView adviceAction1;
    private Button actionButton2;
    private TextView adviceAction2;

    public static AssessmentFinishFragment newInstance(){
        AssessmentFinishFragment fragment = new AssessmentFinishFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_assessment_finish, container, false);

        //assessment session ID
        id = Long.toString(getArguments().getLong("id"));
        Float scaleInput = getArguments().getFloat("scaleValue");

        //Create database to store assessment answers
        answersDB = new AnsweredQuestionsDBHelper(getActivity());
        getDB();

        String leafNodeResult = (String) getArguments().getString("resultsOfAssessment");
        leafNodeResultTxtView = (TextView) v.findViewById(R.id.leaf_node_result);
        /*String leafNodeResultRaw =  leafNodeResult.split("\\:")[1];
        String leafNodeResultPercent = leafNodeResultRaw.substring(1,leafNodeResultRaw.length()-2);
        Float risk = Float.valueOf(leafNodeResultPercent)*100;
        DecimalFormat df = new DecimalFormat("#");
        df.setRoundingMode(RoundingMode.CEILING);
        Double d = risk.doubleValue();
        leafNodeResultTxtView.setText(df.format(d)+"%");
*/

        actionButton1 = (Button) v.findViewById(R.id.find_local_services1);
        adviceAction1 = (TextView) v.findViewById(R.id.action_advice1);
        actionButton2 = (Button) v.findViewById(R.id.find_local_services2);
        adviceAction2 = (TextView) v.findViewById(R.id.action_advice2);
        actionButton2.setVisibility(View.INVISIBLE);
        adviceAction2.setVisibility(View.INVISIBLE);

        calculateAction(leafNodeResult, actionButton1, adviceAction1, true);
        if (scaleInput != 0) {
            calculateAction(Float.toString(scaleInput), actionButton2, adviceAction2, false);
        }

        //Disable back button
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Toast.makeText(getActivity(), "Can't press back from here", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        OnSetToolbarTitleListener callback;
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = (OnSetToolbarTitleListener) activity;
            callback.setTitle("Assessment Complete");
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public interface OnSetToolbarTitleListener {
        public void setTitle(String title);
    }

    private void calculateAction(String result, Button actionButton, TextView adviceActionTV, Boolean action_origin){
       Float riskValue;
        String riskString = "";
        if (action_origin) {
            String riskValueRaw = result.split("\\:")[1];
            String riskValueString = riskValueRaw.substring(1, riskValueRaw.length() - 2);
            riskValue = Float.valueOf(riskValueString);
        }else{
            riskValue = Float.valueOf(result);
        }
        if (riskValue >= 0.75 && !veryHighRiskSet){
            if (action_origin) {
                leafNodeResultTxtView.setText("Your answers suggest that you have a very high risk of hurting yourself.");
                adviceActionTV.setText("Based on your results, you should go visit your nearest A&E:");
                riskString = "Very high risk";
            }else{
                adviceActionTV.setText("We also suggested during the assessment that you should go visit your nearest A&E");
                showSecondaryActions();
            }
            actionButton.setText("Find A&E");
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new PlacesList();
                    Bundle bundle = new Bundle();
                    bundle.putDouble("searchAreaLong", location.longitude);
                    bundle.putDouble("searchAreaLat", location.latitude);
                    bundle.putString("searchRadius","10000");
                    bundle.putString("searchType", "hospital");
                    bundle.putString("filterBy", "");
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.relativeLayout, fragment);
                    transaction.commit();
                }
            });
            veryHighRiskSet = true;
        }else if (riskValue >= 0.5 && riskValue < 0.75 && !highRiskSet ){
            if (action_origin) {
                leafNodeResultTxtView.setText("Your answers suggest that you have a high risk of hurting yourself.");
                adviceActionTV.setText("Based on your results, you should call 111 for further advice:");
                riskString = "High risk";
            }else{
                adviceActionTV.setText("We also suggested during the assessment that you call 111 for further advice:");
                showSecondaryActions();
            }
            Log.i(TAG, "Call 111");
            actionButton.setText("Call 111");
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+111));
                    startActivity(intent);
                }
            });
            highRiskSet = true;
        }else if (riskValue >= 0.3 && riskValue < 0.5 && !mediumRiskSet){
            Log.i(TAG, "See you GP");
            if (action_origin) {
                leafNodeResultTxtView.setText("Your answers suggest that you have a moderate risk of hurting yourself.");
                adviceActionTV.setText("Based on your results, you should go visit your GP:");
                riskString = "Moderate risk";
            }else{
                adviceActionTV.setText("We also suggested during the assessment that you should go visit your GP:");
                showSecondaryActions();
            }
            actionButton.setText("Find GP");
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new PlacesList();
                    Bundle bundle = new Bundle();
                    bundle.putDouble("searchAreaLong", location.longitude);
                    bundle.putDouble("searchAreaLat", location.latitude);
                    bundle.putString("searchRadius","2000");
                    bundle.putString("searchType", "gp");
                    bundle.putString("filterBy", "");
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.relativeLayout, fragment);
                    transaction.commit();
                }
            });
            mediumRiskSet = true;
        }else if (riskValue >= 0.1 && riskValue < 0.3 && !lowRiskSet){
            if(action_origin) {
                leafNodeResultTxtView.setText("Your answers suggest that you have a low risk of hurting yourself.");
                adviceActionTV.setText("We advise that you text a friend to ask for some support:");
                riskString = "Low risk";
            }else{
                adviceActionTV.setText("We also suggested during the assessment that you should text a friend to ask for some support:");
                showSecondaryActions();
            }
            Log.i(TAG, "Text friend");
            actionButton.setText("Text Friend");
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setData(Uri.parse("sms:"));
                    sendIntent.putExtra("sms_body", "Hello, I need to talk to someone. Are you free?");
                    startActivity(sendIntent);
                }
            });
            lowRiskSet = true;
        }else if (riskValue < 0.1){
            leafNodeResultTxtView.setText("Your answers suggest that you have no risk of hurting yourself.");
            adviceActionTV.setText("You're all good. If you want to find some local services, click the button:");
            riskString = "No risk";
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new PlacesList();
                    Bundle bundle = new Bundle();
                    bundle.putDouble("searchAreaLong", location.longitude);
                    bundle.putDouble("searchAreaLat", location.latitude);
                    bundle.putString("searchRadius","1500");
                    bundle.putString("searchType", "doctor|hospital|pharmacy");
                    bundle.putString("filterBy", "");
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.relativeLayout, fragment);
                    transaction.commit();
                }
            });
            Log.i(TAG, "No action");
        }
        if (action_origin){
            answersDB.insertAssessmentRisk(id, riskString);
        }
        Log.i(TAG, "Risk value is "+riskString);
    }

    private ArrayList<PrevAssessmentListItem> getDB() {
        ArrayList<PrevAssessmentListItem> data = new ArrayList<>();
        Cursor res = answersDB.getAssessmentDetails(id);
        StringBuffer dbContents = new StringBuffer();

        if (res.getCount() == 0) {
            BitmapUtility bitmapUtility = new BitmapUtility();
            String titleText = "There are no previous assessments to display";
            PrevAssessmentListItem current = new PrevAssessmentListItem("0", "No Assessments Found", "");
            data.add(current);
        } else {
            while (res.moveToNext()) {
                String[] locationStrings = res.getString(5).split("\\:")[2].split(",");
                Double lat = Double.parseDouble(locationStrings[0].substring(2));
                Double longitude = Double.parseDouble(locationStrings[1].substring(0,locationStrings[1].length()-1));
                location = new LatLng(lat, longitude);
                Log.i(TAG, "Location string is "+location);

                String gender = res.getString(2);
                String dateOfAssessment = res.getString(7);

                PrevAssessmentListItem current = new PrevAssessmentListItem(id, dateOfAssessment, gender);
                data.add(current);
            }
            Log.i("Assessment_list", dbContents.toString());
        }
        return data;
    }

    private void showSecondaryActions(){
        actionButton2.setVisibility(View.VISIBLE);
        adviceAction2.setVisibility(View.VISIBLE);
    }

}
