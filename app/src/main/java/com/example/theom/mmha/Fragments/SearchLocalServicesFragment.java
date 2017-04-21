package com.example.theom.mmha.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.theom.mmha.Fragments.FilterBy.ChooseFilterDialog;
import com.example.theom.mmha.Fragments.Places.PlacesList;
import com.example.theom.mmha.Fragments.SearchArea.SearchAreaDialogFragment;
import com.example.theom.mmha.Fragments.SearchArea.SearchAreaItem;
import com.example.theom.mmha.Fragments.SearchType.SearchTypeDialogFragment;
import com.example.theom.mmha.Fragments.SearchType.SearchTypeItem;
import com.example.theom.mmha.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

public class SearchLocalServicesFragment extends Fragment implements View.OnClickListener,
        SearchTypeDialogFragment.OnSetSearchLocationTypeFromListener,
        SearchAreaDialogFragment.OnSetSearchLocationAreaFromListener,
        ChooseFilterDialog.OnSetFiltersListener,
        GoogleApiClient.OnConnectionFailedListener {


    private OnFragmentInteractionListener mListener;
    private String searchLocationType;
    private TextView whatTypeTextView;
    private TextView whatAreaTextView;
    private TextView filterByTextView;
    private double searchLat;
    private double searchLong;
    private String searchRadius;
    private String filterBy="";
    private static final String TAG = "SearchServices";
    private LocationManager locationManager;
    private LocationListener locationListener;


    // TODO: Rename and change types and number of parameters
    public static SearchLocalServicesFragment newInstance() {
        SearchLocalServicesFragment fragment = new SearchLocalServicesFragment();
        return fragment;
    }

    public SearchLocalServicesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_see_sights, container, false);

        //TextViews that are changed to display user's search inputs
        whatAreaTextView = (TextView) v.findViewById(R.id.whatAreaTextView);
        whatTypeTextView = (TextView) v.findViewById(R.id.what_type_search_textview);
        filterByTextView = (TextView) v.findViewById(R.id.filter_by_textview);

        //request location permissions
        setUpLocation();

        // Configure what area is search
        Button mWhatArea = (Button) v.findViewById(R.id.what_area_button);
        mWhatArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showLocationAreaDialog();
            }
        });

        // Configure what type of location is being searched for
        Button mWhatType = (Button) v.findViewById(R.id.what_type_button);
        mWhatType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showLocationTypeDialog();
            }
        });

        Button mWhatFilter = (Button) v.findViewById(R.id.filterByButton) ;
        mWhatFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showFilterByDialog();
            }
        });

        Button mSearch = (Button) v.findViewById(R.id.search_map);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                if (searchLong == 0.0 && searchLat == 0.0){
                    Toast.makeText(getActivity(), "Please enter a search location", Toast.LENGTH_SHORT).show();
                    return;
                }
                String locationType = searchLocationType;
                if (locationType != null)
                    bundle.putString("locationType", locationType);
                else {
                    Toast.makeText(getActivity(), "You've not entered a location type", Toast.LENGTH_SHORT).show();
                    searchLocationType = "hospital";
                }
                bundle.putDouble("searchAreaLong", searchLong);
                bundle.putDouble("searchAreaLat", searchLat);
                bundle.putString("searchRadius",searchRadius);
                bundle.putString("searchType", searchLocationType);
                bundle.putString("filterBy", filterBy);
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

    }

    //Display the dialog to allow user to input what area they would like to search within
    public void showLocationAreaDialog(){
        SearchAreaDialogFragment dialog = SearchAreaDialogFragment.newInstance("Search area");
        dialog.setTargetFragment(this, 0);
        dialog.show(getActivity().getSupportFragmentManager(), "fragmentDialog");
    }

    //Display dialog to allow user to input what type of location they would like to search
    public void showLocationTypeDialog(){
        SearchTypeDialogFragment dialog = SearchTypeDialogFragment.newInstance("Point of interest type");
        dialog.setTargetFragment(this, 0);
        dialog.show(getActivity().getSupportFragmentManager(), "fragmentDialog");
    }

    //Display dialog to allow user to input what type of location they would like to search
    public void showFilterByDialog(){
        ChooseFilterDialog chooseFilterDialog = ChooseFilterDialog.newInstance("Choose filter for search");
        chooseFilterDialog.setTargetFragment(this, 0);
        chooseFilterDialog.show(getActivity().getSupportFragmentManager(), "fragmentDialog");
    }


    //Set the search location area parameters
    public void setSearchLocationArea(SearchAreaItem searchAreaItem) {
        searchRadius = searchAreaItem.getRadius().toString();
        String searchAreaName = searchAreaItem.getName();

        if (searchAreaName == null || searchAreaName.equals("My Location")){
            searchLat = 0.0;
            searchLong = 0.0;
        }
        else {
            searchLat = searchAreaItem.getSearchCoordinates().latitude;
            searchLong = searchAreaItem.getSearchCoordinates().longitude;
        }

        Log.i(TAG, "searchAreaName = "+searchAreaName+", Coordinates: "+searchLat + ", "+searchLong);
        //change the text view above the dialog launch button to show the user their selection
        if (searchAreaName == null) {
            whatAreaTextView.setText("My Location");
            whatAreaTextView.setTextSize(35);
        } else {
            whatAreaTextView.setText(searchAreaName);
            shrinkTextToFit(whatAreaTextView.getMaxWidth(),whatAreaTextView,35,10);
        }

    }

    //Set the search location type
    public void setSearchLocationType(ArrayList<SearchTypeItem> searchLocationTypeArray){
        String searchLocationType="";
        String visibleSearchLocationType="";

        //Iterate over the returned array list from the dialogfragment to create a query string for places API
        for(int i = 0; i<searchLocationTypeArray.size(); i++){
            if(i == 0){
                searchLocationType = searchLocationTypeArray.get(i).getSearchValue();
                visibleSearchLocationType = searchLocationTypeArray.get(i).getName();
            }else {
                searchLocationType = searchLocationType + "|" + searchLocationTypeArray.get(i).getSearchValue();
                visibleSearchLocationType = visibleSearchLocationType + ", " + searchLocationTypeArray.get(i).getName();
            }
        }

        //change the text view above the dialog launch button to show the user their selection
        if(visibleSearchLocationType.equals(""))
        {
            whatTypeTextView.setText("What type?");
            whatTypeTextView.setTextSize(50);
        }else{
            whatTypeTextView.setText(visibleSearchLocationType);
            whatTypeTextView.setTextSize(22);
        }
        this.searchLocationType = searchLocationType;
        Log.i("Search Type", "Location search type is set to "+searchLocationType);
    }

    public void setFilters(String filterBy, String filterTitle){
        //change the text view above the dialog launch button to show the user their selection
        if(filterTitle.equals(""))
        {
            filterByTextView.setText("What type?");
            filterByTextView.setTextSize(50);
        }else{
            filterByTextView.setText(filterTitle);
            //filterByTextView.setTextSize(22);
        }
        this.filterBy = filterBy;
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onAttach(Activity activity) {
        OnSetToolbarTitleListener callback;
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = (OnSetToolbarTitleListener) activity;
            callback.setTitle("Search Local Services");
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public interface OnSetToolbarTitleListener {
        public void setTitle(String title);
    }

    public static void shrinkTextToFit(float availableWidth, TextView textView,
                                       float startingTextSize, float minimumTextSize) {

        CharSequence text = textView.getText();
        float textSize = startingTextSize;
        textView.setTextSize(startingTextSize);
        while (text != (TextUtils.ellipsize(text, textView.getPaint(),
                availableWidth, TextUtils.TruncateAt.END))) {
            textSize -= 1;
            if (textSize < minimumTextSize) {
                break;
            } else {
                textView.setTextSize(textSize);
            }
        }
    }

    private void setUpLocation(){
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //Log.i(TAG, "Your location is "+location.getLatitude()+", "+location.getLongitude());
              /*  while (toastGPSShown == false) {
                    toastGPSShown = true;
                }
                userLocationLat = location.getLatitude();
                userLocationLong = location.getLongitude();*/
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
            } else {
                locationManager.requestLocationUpdates("gps", 500, 0, locationListener);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    // getUserLocation();
                    return;
        }
    }
}
