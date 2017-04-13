package com.example.theom.mmha.MySafety_Quiz.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.theom.mmha.R;

import java.util.ArrayList;

/**
 * Created by theom on 11/04/2017.
 */

public class SetupDialog extends DialogFragment {

    String TAG = "Setup Dialog";
    RadioGroup relationshipStatusButtons;
    RadioGroup livingStatusButtons;
    String romRelationshipStatus = "Null";
    String livingStatus = "Null";

    public SetupDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public interface OnSetRelationshipStatusListener {
        public void setRelationshipStatus(String relationshipStatus);
    }

    public static SetupDialog newInstance(String title) {
        SetupDialog dialog = new SetupDialog();
        Bundle args = new Bundle();
        //Fetch all arguments needed for inserting into the dialog
        args.putString("title", title);
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
        if(title.equals("Relationships")) {

            //Set view to the help dialog fragment
            v = getActivity().getLayoutInflater().inflate(R.layout.relationships_dialog, null);

            RadioButton r1 = (RadioButton) v.findViewById(R.id.radio_yes_partner);
            RadioButton r2 = (RadioButton) v.findViewById(R.id.radio_no_partner);
            RadioButton r3 = (RadioButton) v.findViewById(R.id.radio_dontknow_partner);

            RadioButton r4 = (RadioButton) v.findViewById(R.id.radio_yes_living);
            RadioButton r5 = (RadioButton) v.findViewById(R.id.radio_no_living);
            RadioButton r6 = (RadioButton) v.findViewById(R.id.radio_dontknow_living);

            r1.setTag("They have a partner");
            r2.setTag("No partner");
            r3.setTag("Don't know relationship status");

            r4.setTag("They share accommodation");
            r5.setTag("They don't share accommodation");
            r6.setTag("Don't know living arrangements");

            setupRadioButtons(v);

            //Set title of dialog
            builder.setTitle(title).setView(v);

        }else{
            //Set view to the help dialog fragment
            v = getActivity().getLayoutInflater().inflate(R.layout.relationships_dialog, null);
            //Set title of dialog
            builder.setTitle(title).setView(v);
        }
        //Find done button in fragment
        Button doneButton = (Button) v.findViewById(R.id.doneButton);
        //Attatch listener to Done button
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String relationshipsStatus = romRelationshipStatus + ", "+livingStatus;
                OnSetRelationshipStatusListener callback = (OnSetRelationshipStatusListener) getTargetFragment();
                callback.setRelationshipStatus(relationshipsStatus);
                dismiss();
            }
        });
        //Return the created dialog
        return builder.create();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    }

    public void setupRadioButtons(View v){
        relationshipStatusButtons = (RadioGroup) v.findViewById(R.id.partner_radio_buttons);
        livingStatusButtons = (RadioGroup) v.findViewById(R.id.living_radio_buttons);

        relationshipStatusButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRelationshipButton = (RadioButton) group.findViewById(checkedId);
                if (selectedRelationshipButton != null){
                    romRelationshipStatus = selectedRelationshipButton.getTag().toString();
                }
            }
        });

        livingStatusButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedLivingButton = (RadioButton) group.findViewById(checkedId);
                if (selectedLivingButton != null){
                    livingStatus = selectedLivingButton.getTag().toString();
                }

            }
        });
    }

}
