package com.example.theom.mmha.MySafety_Quiz.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.theom.mmha.R;

import java.util.ArrayList;

/**
 * Created by theom on 11/04/2017.
 */

public class SetupDialog extends DialogFragment {

    public SetupDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public interface OnSetRelationshipStatusListener {
        public void setSearchLocationType(String relationshipStatus);
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
