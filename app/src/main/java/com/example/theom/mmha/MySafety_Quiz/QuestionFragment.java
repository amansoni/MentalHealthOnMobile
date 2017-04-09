package com.example.theom.mmha.MySafety_Quiz;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.theom.mmha.R;

import org.json.JSONException;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QuestionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestionFragment extends Fragment {

    TextView questionTextView;
    TextView questionActionTextView;
    TextView questionTypeTextView;
    TextView questionMGTextView;
    TextView questionCodeTextView;
    private AnsweredQuestionsDBHelper answersDB;
    String TAG = "QuestionFragment";
    Boolean leafNodeReached = false;
    QuestionObject currentQuestion;
    FrameLayout frameLayout;
    View view;

    private OnFragmentInteractionListener mListener;

    public QuestionFragment() {

        // Required empty public constructor
    }

    public static QuestionFragment newInstance() {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        //View v = inflater.inflate(R.layout.fragment_question, container, false);

        frameLayout = new FrameLayout(getActivity());
        inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.fragment_question, null);
        frameLayout .addView(view);


        //Parsing the JSON
        final JSON_parser senorJSON_parser = new JSON_parser();
        QuestionObject firstQuestion = senorJSON_parser.setupQuiz(getActivity());
        currentQuestion = firstQuestion;
        //Create database to store assessment answers
        answersDB = new AnsweredQuestionsDBHelper(getActivity());

        questionTextView = (TextView) frameLayout.findViewById(R.id.questionTextView);
        questionActionTextView = (TextView) frameLayout.findViewById(R.id.questionAction);
        questionTypeTextView = (TextView) frameLayout.findViewById(R.id.questionType);
        questionMGTextView = (TextView) frameLayout.findViewById(R.id.questionMG);
        questionCodeTextView = (TextView) frameLayout.findViewById(R.id.questionCode);

        questionTextView.setText("Question: "+firstQuestion.getQuestionText());
        questionActionTextView.setText("Action: "+firstQuestion.getQuestionAction());
        questionTypeTextView.setText("Type: "+firstQuestion.getQuestionType());
        questionMGTextView.setText("MG: "+firstQuestion.getQuestionMG());
        questionCodeTextView.setText("Code: "+firstQuestion.getQuestionCode());

        Button mYesButton = (Button)frameLayout.findViewById(R.id.yesButton);
        Button mNoButton = (Button)frameLayout.findViewById(R.id.noButton);

        if(leafNodeReached == false) {
            mYesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetFragmentText("Yes", senorJSON_parser);
                }
            });
            mNoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetFragmentText("No", senorJSON_parser);
                }
            });
        }
        return frameLayout;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.action_bar_items, menu);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        /*if(id == R.id.action_favorite){
            //Do whatever you want to do
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public Boolean GetFragmentText(String answer, JSON_parser senorJSON_parser){

        Context ctx = getActivity();
        QuestionObject question = null;

        try {
            if (leafNodeReached == false) {
                question = senorJSON_parser.runQuiz(answer, ctx, currentQuestion);
                leafNodeReached = question.isLeafNode();

                changeView(question, senorJSON_parser);

                currentQuestion=question;
            }else{
                Log.i(TAG, "Houston, we reached the leaf node.");
                questionTextView.setText("Leaf node reached");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return leafNodeReached;
    }

    public void ChangeButtonStatus(){
        Button mYesButton = (Button)getActivity().findViewById(R.id.yesButton);
        Button mNoButton = (Button)getActivity().findViewById(R.id.noButton);
        mYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    public void changeView(QuestionObject question, final JSON_parser senorJSON_parser) {


        int optionId = R.layout.fragment_question;

        if (question.getQuestionType().equals("layer")){
            optionId = R.layout.fragment_question;
        }else if (question.getQuestionType().equals("nominal")){
            optionId = R.layout.fragment_question_nominal;
        }else if (question.getQuestionType().equals("scale")){
            optionId = R.layout.fragment_question_scale;
        }

        frameLayout. removeAllViews();
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(optionId, null);
        frameLayout .addView(view);

        questionTextView = (TextView) view.findViewById(R.id.questionTextView);
        questionActionTextView = (TextView) view.findViewById(R.id.questionAction);
        questionTypeTextView = (TextView) view.findViewById(R.id.questionType);
        questionMGTextView = (TextView) view.findViewById(R.id.questionMG);
        questionCodeTextView = (TextView) view.findViewById(R.id.questionCode);

        questionTextView.setText("Question: "+question.getQuestionText());
        questionActionTextView.setText("Action: "+question.getQuestionAction());
        questionTypeTextView.setText("Type: "+question.getQuestionType());
        questionMGTextView.setText("MG: "+question.getQuestionMG());
        questionCodeTextView.setText("Code: "+question.getQuestionCode());

        //Check that layout contains yes/no buttons
        if(leafNodeReached == false && optionId == R.layout.fragment_question) {

            Button mYesButton = (Button)frameLayout.findViewById(R.id.yesButton);
            Button mNoButton = (Button)frameLayout.findViewById(R.id.noButton);

            mYesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetFragmentText("Yes", senorJSON_parser);
                }
            });
            mNoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetFragmentText("No", senorJSON_parser);
                }
            });
        }else if (leafNodeReached == false && optionId == R.layout.fragment_question_nominal){
            String radioButtonValues = question.getQuestionMG();
            radioButtonValues = radioButtonValues.substring(2, radioButtonValues.length() -2 );
            String [] radioButtonValuesArray = radioButtonValues.split("\\)\\(");
            HashMap<String, Float> nominalScaleValues = new HashMap<>();
            HashMap<String, String> nominalScaleTitles = new HashMap<>();

            Integer i = 0;
            for (String value : radioButtonValuesArray){
                String text = value.split(" ")[0];

                //Capitalise text
                String nominalText = text.substring(0,1).toUpperCase() + text.substring(1);

                //Extract nominal value from mg string
                Float nominalValue = Float.parseFloat(value.split(" ")[1]);

                //Insert extracted values into respected hashmaps
                nominalScaleValues.put(i.toString(), nominalValue);
                nominalScaleTitles.put(i.toString(), nominalText);

                i++;
            }
            RadioButton r1 = (RadioButton)frameLayout.findViewById(R.id.nominal1);
            RadioButton r2 = (RadioButton)frameLayout.findViewById(R.id.nominal2);
            RadioButton r3 = (RadioButton)frameLayout.findViewById(R.id.nominal3);
            RadioButton r4 = (RadioButton)frameLayout.findViewById(R.id.nominal4);

            r1.setTag(nominalScaleValues.get("0"));
            r1.setText(nominalScaleTitles.get("0"));

            r2.setTag(nominalScaleValues.get("1"));
            r2.setText(nominalScaleTitles.get("1"));

            r3.setTag(nominalScaleValues.get("2"));
            r3.setText(nominalScaleTitles.get("2"));

            r4.setTag(nominalScaleValues.get("3"));
            r4.setText(nominalScaleTitles.get("3"));
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.nominal1:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.nominal2:
                if (checked)
                    // Ninjas rule
                    break;
            case R.id.nominal3:
                if (checked)
                    // Ninjas rule
                    break;
            case R.id.nominal4:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }
}
