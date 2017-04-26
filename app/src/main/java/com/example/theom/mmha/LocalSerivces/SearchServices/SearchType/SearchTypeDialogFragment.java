package com.example.theom.mmha.LocalSerivces.SearchServices.SearchType;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.theom.mmha.R;

import java.util.ArrayList;

/**
 * Created by theom on 01/03/2017.
 */

public class SearchTypeDialogFragment extends DialogFragment {
    private ListView listView;
    private SearchTypeListAdapter listAdapter;
    private ArrayList<SearchTypeItem> searchTypeItems = new ArrayList<SearchTypeItem>();

    public interface OnSetSearchLocationTypeFromListener {
        public void setSearchLocationType(ArrayList<SearchTypeItem> searchLocationTypeArray);
    }

    public SearchTypeDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static SearchTypeDialogFragment newInstance(String title) {
        SearchTypeDialogFragment dialog = new SearchTypeDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        dialog.setArguments(args);
        return dialog;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Declare the view
        View v = getActivity().getLayoutInflater().inflate(R.layout.search_type_dialog, null);

        //Create the ListView with the Location Type items
        ListView listView = createLocationTypeListView(v);
        //Build the alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getArguments().getString("title")).setView(v);

        Button doneButton = (Button) v.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                OnSetSearchLocationTypeFromListener callback = (OnSetSearchLocationTypeFromListener) getTargetFragment();
                callback.setSearchLocationType(listAdapter.getCheckedItems());
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    }

    public ListView createLocationTypeListView(View v){
        ListView lv;
        lv = (ListView) v.findViewById(R.id.listView1);
        searchTypeItems.add(new SearchTypeItem("Hospital", "hospital", 0));
        searchTypeItems.add(new SearchTypeItem("Doctor", "doctor", 1));
        searchTypeItems.add(new SearchTypeItem("Pharmacy", "pharmacy", 1));
        searchTypeItems.add(new SearchTypeItem("Physiotherapist", "physiotherapist", 0));
        searchTypeItems.add(new SearchTypeItem("Police", "police", 0));

        listAdapter = new SearchTypeListAdapter(getActivity(), searchTypeItems);
        lv.setAdapter(listAdapter);
        return lv;
    }
}
