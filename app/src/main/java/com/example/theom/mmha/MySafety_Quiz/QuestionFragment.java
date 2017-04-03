package com.example.theom.mmha.MySafety_Quiz;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.theom.mmha.R;

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
        //Parsing the JSON
        final JSON_parser senorJSON_parser = new JSON_parser();
        senorJSON_parser.setupQuiz(getActivity());

        //Create database to store assessment answers
        answersDB = new AnsweredQuestionsDBHelper(getActivity());

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_question, container, false);

        questionTextView = (TextView) v.findViewById(R.id.questionTextView);
        questionActionTextView = (TextView) v.findViewById(R.id.questionAction);
        questionTypeTextView = (TextView) v.findViewById(R.id.questionType);
        questionMGTextView = (TextView) v.findViewById(R.id.questionMG);
        questionCodeTextView = (TextView) v.findViewById(R.id.questionCode);

        Button mYesButton = (Button)v.findViewById(R.id.yesButton);
        mYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = getActivity();
                String answer = "Yes";
                QuestionObject question = senorJSON_parser.runQuiz(answer, ctx);
                questionTextView.setText("Question: "+question.getQuestionText());
                questionActionTextView.setText("Action: "+question.getQuestionAction());
                questionTypeTextView.setText("Type: "+question.getQuestionType());
                questionMGTextView.setText("MG: "+question.getQuestionMG());
                questionCodeTextView.setText("Code: "+question.getQuestionCode());
            }
        });
        Button mNoButton = (Button)v.findViewById(R.id.noButton);
        mNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = getActivity();
                String answer = "No";
                QuestionObject question = senorJSON_parser.runQuiz(answer, ctx);
                questionTextView.setText("Question: "+question.getQuestionText());
                questionActionTextView.setText("Action: "+question.getQuestionAction());
                questionTypeTextView.setText("Type: "+question.getQuestionType());
                questionMGTextView.setText("MG: "+question.getQuestionMG());
                questionCodeTextView.setText("Code: "+question.getQuestionCode());
            }
        });
        return v;
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
}
