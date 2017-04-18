package com.example.theom.mmha.MySafety_Quiz;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.theom.mmha.Fragments.SeeSightsFragment;
import com.example.theom.mmha.R;

/**
 * Created by theom on 17/04/2017.
 */

public class AssessmentFinishFragment extends Fragment {

    public static AssessmentFinishFragment newInstance(){
        AssessmentFinishFragment fragment = new AssessmentFinishFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_assessment_finish, container, false);

        String leafNodeResult = (String) getArguments().getString("resultsOfAssessment");
        TextView leafNodeResultTxtView = (TextView) v.findViewById(R.id.leaf_node_result);
        leafNodeResultTxtView.setText(leafNodeResult);

        Button localServicesButton = (Button) v.findViewById(R.id.find_local_services);
        localServicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new SeeSightsFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.relativeLayout, fragment);
                transaction.commit();
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
}
