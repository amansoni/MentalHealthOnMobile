package com.example.theom.mmha.Fragments.Places;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.theom.mmha.R;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
/**
 * Created by theom on 28/03/2017.
 */

public class SearchDoctorFragment extends Fragment {


    private SupportPlaceAutocompleteFragment autocompleteFragment;
    private LatLng searchCoordinates;
    private double searchLong;
    private double searchLat;
    private Integer searchRadius;
    private String searchAreaName;

    public SearchDoctorFragment(){
        //empty constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       // View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_hospital_finder, null);
        View v = inflater.inflate(R.layout.fragment_hospital_finder, container, false);
        //Create the Google Places AutoComplete Widget
        autocompleteFragment = (SupportPlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        //Listener for Google Places AutoComplete widget
        autoCompleteProcess();

        Button searchButton = (Button) v.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putDouble("searchAreaLat", searchLat);
                bundle.putDouble("searchAreaLong", searchLong);
                Fragment fragment = new PlacesList();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.relativeLayout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });



        return v;
    }

    public void cleanUp(){
        getActivity().getSupportFragmentManager().beginTransaction().remove(autocompleteFragment).commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cleanUp();
    }

    public void autoCompleteProcess(){
        //Listener for Google Places AutoComplete widget
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                //Log.i(TAG, "Place: " + place.getName() + ", " + place.getLatLng());//get place details here
                searchCoordinates = place.getLatLng();
                searchAreaName = place.getName().toString();
                searchLong = place.getLatLng().longitude;
                searchLat = place.getLatLng().latitude;
                Log.i("Places Widget", "These are the results of auto-complete "+searchCoordinates);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("SearchDoctorFragment", "An error occurred: " + status);
            }
        });
    }
}
