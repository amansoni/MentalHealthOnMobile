package com.example.theom.mmha.Assessment.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.theom.mmha.Assessment.AnsweredQuestionsDBHelper;
import com.example.theom.mmha.Assessment.QuestionObject;
import com.example.theom.mmha.PreviousAssessments.PrevAssessmentListFragment;
import com.example.theom.mmha.R;

/**
 * Created by theom on 21/03/2017.
 */

//Dialog to tell the user about the itinerary function
public class InfoDialog extends DialogFragment {

    private AnsweredQuestionsDBHelper answersDB;

    public InfoDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static InfoDialog newInstance(String title, QuestionObject questionObject, String id) {
        InfoDialog dialog = new InfoDialog();
        Bundle args = new Bundle();
        //Fetch all arguments needed for inserting into the dialog
        args.putString("title", title);
        args.putString("question", questionObject.getQuestionText());
        args.putString("questionCode", questionObject.getQuestionCode());
        args.putString("questionAction", questionObject.getQuestionAction());
        args.putString("questionMG", questionObject.getQuestionMG());
        args.putString("questionType", questionObject.getQuestionType());
        args.putString("questionHelp", questionObject.getQuestionHelp());
        args.putString("id", id);
        dialog.setArguments(args);
        return dialog;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Declare the view for it to be set depending on what the dialog type is
        View v;
        //Build the alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //Set the string dialog's title
        String title = getArguments().getString("title");
        //Switch between different dialog types that need to be displayed
        if(title.equals("Question Information")) {
            //Set view to info dialog fragment to display question information
            v = getActivity().getLayoutInflater().inflate(R.layout.info_dialog_fragment, null);
            //Set the dialog's title
            builder.setTitle(title).setView(v);
            //Find the textviews inside fragment
            TextView questionText = (TextView) v.findViewById(R.id.questionTextView);
            TextView questionCode = (TextView) v.findViewById(R.id.questionCode);
            TextView questionAction = (TextView) v.findViewById(R.id.questionAction);
            TextView questionMG = (TextView) v.findViewById(R.id.questionMG);
            TextView questionType = (TextView) v.findViewById(R.id.questionType);
            //Set the textviews inside the dialog to the arguments
            questionText.setText("Text: " + getArguments().getString("question"));
            questionCode.setText("Code: " + getArguments().getString("questionCode"));
            questionAction.setText("Action: " + getArguments().getString("questionAction"));
            questionMG.setText("MG: " + getArguments().getString("questionMG"));
            questionType.setText("Type: " + getArguments().getString("questionType"));
        }else if (title.equals("Delete Assessment")) {
            v = getActivity().getLayoutInflater().inflate(R.layout.delete_assessment_dialog, null);
            //Set title of dialog
            builder.setTitle(title).setView(v);
            Button deleteButton = (Button) v.findViewById(R.id.deleteButton);
            //Create database to store favourite locations
            answersDB = new AnsweredQuestionsDBHelper(getActivity());
            //ID for assessment to delete
            final String assessmentID = getArguments().getString("id");
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answersDB.deleteValue(assessmentID);
                    Snackbar snackbar = Snackbar
                            .make(v, "Undo delete ", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    Fragment fragment = new PrevAssessmentListFragment();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.relativeLayout, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    dismiss();
                }
            });
        }else{
            //Set view to the help dialog fragment
            v = getActivity().getLayoutInflater().inflate(R.layout.help_dialog_fragment, null);
            //Set title of dialog
            builder.setTitle(title).setView(v);
            //Set helptext textview to question's help text
            TextView helpText = (TextView) v.findViewById(R.id.questionHelpText);
            helpText.setText(getArguments().getString("questionHelp"));
        }
        //Find done button in fragment
        Button doneButton = (Button) v.findViewById(R.id.doneButton);
        //Attatch listener to Done button
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dismiss();
            }
        });
        //Return the created dialog
        return builder.create();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    }
}

