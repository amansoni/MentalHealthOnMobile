package com.example.theom.mmha.Fragments.MapLocation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.theom.mmha.R;

/**
 * Created by theom on 28/03/2017.
 */

public class CoordinatorFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_location_item_coordinator, container, false);

        return v;
    }
}
