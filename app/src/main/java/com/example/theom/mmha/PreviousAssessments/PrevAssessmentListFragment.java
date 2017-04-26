package com.example.theom.mmha.PreviousAssessments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.theom.mmha.DbBitmapUtility;
import com.example.theom.mmha.Assessment.AnsweredQuestionsDBHelper;
import com.example.theom.mmha.R;

import java.util.ArrayList;


/**
 * Created by theom on 15/03/2017.
 */

public class PrevAssessmentListFragment extends Fragment implements PrevAssessmentListAdapter.MyViewHolder.OnItemClickListener{

    private RecyclerView recyclerView;
    private PrevAssessmentListAdapter adapter;
    private AnsweredQuestionsDBHelper answersDB;
    private String TAG = "PrevAssessment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_prev_assess_list, container, false);

        //Create database to store favourite locations
        answersDB = new AnsweredQuestionsDBHelper(getActivity());

        setHasOptionsMenu(true);
        recyclerView = (RecyclerView) v.findViewById(R.id.favouritesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new PrevAssessmentListAdapter(getDB(), this, false);

        recyclerView.setAdapter(adapter);
        getDB();
        answersDB.close();
        return v;
    }

    private ArrayList<PrevAssessmentListItem> getDB(){
        ArrayList<PrevAssessmentListItem> data = new ArrayList<>();
        Cursor res = answersDB.getAllData();
        StringBuffer dbContents = new StringBuffer();

        if (res.getCount() == 0){
            DbBitmapUtility dbBitmapUtility = new DbBitmapUtility();
            String titleText="There are no previous assessments to display";
            PrevAssessmentListItem current=new PrevAssessmentListItem("0", "0000-00-00 00:00:00", "");
            data.add(current);
        }else {
            while (res.moveToNext()) {
                //titles.add(res.getString(0));
                String id = res.getString(0);
                String dateOfAssessment= res.getString(7);
                String location = res.getString(5);

                PrevAssessmentListItem current = new PrevAssessmentListItem(id, dateOfAssessment, location);

                data.add(current);
            }
            Log.i("Favouriites_list", dbContents.toString());
        }
        return data;
    }

    @Override
    public void onItemClicked(int position) {
        Log.i(TAG, "Clicked");
    }

    @Override
    public boolean onItemLongClicked(int position) {
        return false;
    }

    @Override
    public void onAttach(Activity activity) {
        OnSetToolbarTitleListener callback;
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = (OnSetToolbarTitleListener) activity;
            callback.setTitle("Previous Assessments");
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public interface OnSetToolbarTitleListener {
        public void setTitle(String title);
    }

}
