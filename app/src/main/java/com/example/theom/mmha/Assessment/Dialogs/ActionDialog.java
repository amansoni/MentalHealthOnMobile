package com.example.theom.mmha.Assessment.Dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.theom.mmha.LocalSerivces.Places.PlacesList;
import com.example.theom.mmha.R;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by theom on 11/04/2017.
 */

public class ActionDialog extends DialogFragment {

    String TAG = "Setup Dialog";
    String romRelationshipStatus = "Don't know";
    String livingStatus = "Don't know";
    LatLng location;
    Long id;

    public ActionDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ActionDialog newInstance(String title, Double lat, Double longitude, String actionButtonText, String adviceActionText) {
        ActionDialog dialog = new ActionDialog();
        Bundle args = new Bundle();
        //Fetch all arguments needed for inserting into the dialog
        args.putString("title", title);
        args.putDouble("lat", lat);
        args.putDouble("longitude", longitude);
        args.putString("actionButtonText", actionButtonText);
        args.putString("adviceActionText",adviceActionText);
        dialog.setArguments(args);
        return dialog;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Declare the view for it to be set depending on what the dialog type is
        View v;
        //Build the alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //Switch between different dialog types that need to be displayed

        //Set view to the help dialog fragment
        v = getActivity().getLayoutInflater().inflate(R.layout.action_dialog, null);

        //Set the string dialog's title
        String title = getArguments().getString("title");
        id = getArguments().getLong("id");
        location = new LatLng(getArguments().getDouble("lat"), getArguments().getDouble("longitude"));
        String actionAdviceString = getArguments().getString("adviceActionText");
        String actionButtonText = getArguments().getString("actionButtonText");

        //Set title of dialog
        builder.setTitle(title).setView(v);

        TextView actionAdviceTextView = (TextView) v.findViewById(R.id.action_advice_text_view);
        actionAdviceTextView.setText(actionAdviceString);

        Button actionButton = (Button) v.findViewById(R.id.action_button);
        setButtonAction(actionButtonText, actionButton);

        //Find done button in fragment
        Button doneButton = (Button) v.findViewById(R.id.doneButton);
        //Attatch listener to Done button
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String relationshipsStatus = romRelationshipStatus + ", " + livingStatus;
                dismiss();
            }
        });
        //Return the created dialog
        return builder.create();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    }


    private void setButtonAction(String actionButtonText, Button actionButton) {
        if (actionButtonText.equals("Find A&E")) {
            actionButton.setText("Find A&E");
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new PlacesList();
                    Bundle bundle = new Bundle();
                    bundle.putDouble("searchAreaLong", location.longitude);
                    bundle.putDouble("searchAreaLat", location.latitude);
                    bundle.putString("searchRadius", "10000");
                    bundle.putString("searchType", "hospital");
                    bundle.putString("filterBy", "");
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.relativeLayout, fragment);
                    transaction.commit();
                    dismiss();
                }
            });
        } else if (actionButtonText.equals("Call 111")) {
            actionButton.setText("Call 111");
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + 111));
                    startActivity(intent);
                }
            });
        } else if (actionButtonText.equals("Find GP")) {
            actionButton.setText("Find GP");
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new PlacesList();
                    Bundle bundle = new Bundle();
                    bundle.putDouble("searchAreaLong", location.longitude);
                    bundle.putDouble("searchAreaLat", location.latitude);
                    bundle.putString("searchRadius", "2000");
                    bundle.putString("searchType", "gp");
                    bundle.putString("filterBy", "");
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.relativeLayout, fragment);
                    transaction.commit();
                    dismiss();
                }
            });
        } else if (actionButtonText.equals("Text friend")) {
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
        }
            Log.i(TAG, "No action");
        }
}
