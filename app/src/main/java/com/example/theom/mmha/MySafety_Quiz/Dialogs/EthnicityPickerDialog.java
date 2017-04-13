package com.example.theom.mmha.MySafety_Quiz.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.theom.mmha.R;

import java.util.ArrayList;

/**
 * Created by theom on 14/03/2017.
 */

public class EthnicityPickerDialog extends DialogFragment {
    private EditText mEditText;
    ArrayList<String> racesArray = new ArrayList<String>();
    ListHashmapAdapter listAdapter;
    ListView listView;

    //Interfaces to send data back to SeeSightsFragment
    public interface OnSetEthnicityListener {
        public void setEthnicity(String ethnicity);
    }

    public EthnicityPickerDialog(){
        racesArray.add("White British");
        racesArray.add("White Irish");
        racesArray.add("Other white");
        racesArray.add("White black Caribbean");
        racesArray.add("White black African");
        racesArray.add("White Asian");
        racesArray.add("Other mixed");
        racesArray.add("Indian");
        racesArray.add("Pakistani");
        racesArray.add("Bangladeshi");
        racesArray.add("Other Asian");
        racesArray.add("Black Caribbean");
        racesArray.add("Black African");
        racesArray.add("Other black");
        racesArray.add("Chinese");
        racesArray.add("Other ethnic");
        racesArray.add("Other black");
    }

    public static EthnicityPickerDialog newInstance(String title){
        EthnicityPickerDialog dialog = new EthnicityPickerDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_dialog, null);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_list_dialog, null);
        listView = (ListView) v.findViewById(R.id.dialogListView);
        listAdapter = new ListHashmapAdapter(racesArray);
        listView.setAdapter(listAdapter);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle(getArguments().getString("title")).setView(v);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                switch (position){
                    default:
                        OnSetEthnicityListener callback = (OnSetEthnicityListener) getTargetFragment();
                        callback.setEthnicity(listAdapter.getItem(position).toString());
                        break;
                }
                //Code to close the dialog after selection
                dismiss();
            }

        });
        return builder.create();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch arguments from bundle and set title
        String title = "Choose a filter";
        //getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        //mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

}
