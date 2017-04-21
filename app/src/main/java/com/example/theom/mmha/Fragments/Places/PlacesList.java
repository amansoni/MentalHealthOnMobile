package com.example.theom.mmha.Fragments.Places;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.theom.mmha.Fragments.Map.GmapFragment;
import com.example.theom.mmha.R;
import com.google.gson.Gson;

import java.net.URLEncoder;

/**
 * Created by theom on 03/03/2017.
 */

public class PlacesList extends Fragment {

    private Double searchAreaLong;
    private Double searchAreaLat;
    private String placesKey;
    private GooglePlaceList placeResults;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //request location permissions
        setUpLocation();

        placesKey = getResources().getString(R.string.google_maps_key);
        if (placesKey.equals("PUT YOUR KEY HERE")) {
            Toast.makeText(getActivity(), "You haven't entered your Google Places Key into the strings file.  Dont forget to set a referer too.", Toast.LENGTH_LONG).show();
        } else {
            //Get users inputted data from SearchLocalServicesFragment to create Google Places API Request
            searchAreaLong = getArguments().getDouble("searchAreaLong");
            searchAreaLat = getArguments().getDouble("searchAreaLat");
            String searchRadius = getArguments().getString("searchRadius");
            String type = getArguments().getString("searchType");
            String filterBy = getArguments().getString("filterBy");

            if(searchAreaLat == 0.0 && searchAreaLong == 0.0){
                searchAreaLong = -1.89028791;
                searchAreaLat = 52.48549062;
                Toast.makeText(getActivity(), "Couldn't establish user location so searching Birmingham", Toast.LENGTH_LONG).show();
            }



            //Insert retrieved data from SearchLocalServicesFragment into places API request
            String placesRequest = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                    searchAreaLat + "," + searchAreaLong +"&"+filterBy+"&type="+type+"&radius="+searchRadius+"&key=" + placesKey+"&keyword=" + type;
            PlacesReadFeed process = new PlacesReadFeed();

            //Execute API request
            process.execute(new String[] {placesRequest});

        }

        return inflater.inflate(R.layout.fragment_place_list, container, false);
    }



    private class PlacesReadFeed extends AsyncTask<String, Void, GooglePlaceList> {
        @Override
        protected GooglePlaceList doInBackground(String... urls) {
            try {
                String referer = null;
                //dialog.setMessage("Fetching Places Data");
                if (urls.length == 1) {
                    referer = null;
                } else {
                    referer = urls[1];
                }
                String input = GooglePlacesUtility.readGooglePlaces(urls[0], referer);
                Gson gson = new Gson();
                GooglePlaceList places = gson.fromJson(input, GooglePlaceList.class);
                Log.i("Places", "Number of places found is " + places.getResults().size());

                return places;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Places", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(GooglePlaceList places) {
            reportBack(places);

            //return places.getPlaceNames().get(1);
        }
    }

    protected void reportBack(GooglePlaceList placeResults) {
        if (this.placeResults == null) {
            this.placeResults = placeResults;

        } else {
            this.placeResults.getResults().addAll(placeResults.getResults());
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("googlePlaceList", placeResults.getResults());
        bundle.putInt("numberOfPlaces", placeResults.getResults().size());
        //User's search location
        bundle.putDouble("searchAreaLong", searchAreaLong);
        bundle.putDouble("searchAreaLat", searchAreaLat);

        Fragment fragment = new GmapFragment();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.relativeLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
