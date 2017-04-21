package com.example.theom.mmha.PreviousAssessments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.theom.mmha.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by theom on 15/03/2017.
 */

public class PrevAssessmentListAdapter extends RecyclerView.Adapter<PrevAssessmentListAdapter.MyViewHolder> {

    private List<PrevAssessmentListItem> data = Collections.emptyList();
    private MyViewHolder.OnItemClickListener clickListener;
    private boolean isItinerary;
    private static final String TAG = "ListAdapter";

    public PrevAssessmentListAdapter(List<PrevAssessmentListItem> data, MyViewHolder.OnItemClickListener clickListener, boolean isItinerary){
        this.data=data;
        this.clickListener=clickListener;
        this.isItinerary=isItinerary;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;

        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_prev_assessment_item, parent, false);

        MyViewHolder holder = new MyViewHolder(v,clickListener);
        return holder;

    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PrevAssessmentListItem current = data.get(position);
        holder.id.setText(current.id);
        holder.dateOfAssessment.setText(current.dateOfAssessment);
        holder.timeOfAssessment.setText(current.timeOfAssessment);
        holder.location.setText(current.getLocation());
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    //Retrieve details for a specific favourite item
    public ArrayList<PrevAssessmentListItem> getItemsDetails(List<Integer> positions){
        ArrayList<PrevAssessmentListItem> itineraryList = new ArrayList<>();
        for (Integer position : positions){
            itineraryList.add(data.get(position));
        }
        return itineraryList;
    }

    //Used by recycler, each cards values are set from here
    public static class MyViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{
        CardView cv;
        TextView id;
        TextView dateOfAssessment;
        TextView timeOfAssessment;
        TextView location;

        public OnItemClickListener listener;

        public MyViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            id = (TextView) itemView.findViewById(R.id.assessment_id);
            dateOfAssessment = (TextView) itemView.findViewById(R.id.date_of_assessment);
            timeOfAssessment = (TextView) itemView.findViewById(R.id.time_of_assessment);
            location = (TextView) itemView.findViewById(R.id.location);

            this.listener = listener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        //Launch favourite detailed info fragment when card is clicked
        @Override
        public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Bundle bundle = new Bundle();
                bundle.putString("id", id.getText().toString());
                Fragment fragment = new AssessmentDetailsFragment();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.relativeLayout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
        }

        //Start itinerary selection mode
        @Override
        public boolean onLongClick(View v){
            if (listener != null) {
                return listener.onItemLongClicked(getPosition());
            }
            return false;
        }


        //Callback method to send data to favourite list fragment
        public interface OnItemClickListener{
            public void onItemClicked(int position);
            public boolean onItemLongClicked(int position);
        }

    }



}