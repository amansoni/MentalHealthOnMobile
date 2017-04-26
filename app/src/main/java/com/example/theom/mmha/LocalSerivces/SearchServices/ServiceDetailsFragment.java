package com.example.theom.mmha.LocalSerivces.SearchServices;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.example.theom.mmha.LocalSerivces.Map.GmapFragment;
import com.example.theom.mmha.LocalSerivces.Places.GooglePlace;
import com.example.theom.mmha.LocalSerivces.Places.GooglePlacesUtility;
import com.example.theom.mmha.LocalSerivces.Places.PlaceDetailFragment;
import com.example.theom.mmha.R;
import com.google.gson.Gson;


/**
 * Created by theom on 13/03/2017.
 */

public class ServiceDetailsFragment extends Fragment {

    private GooglePlace place;
    private String placeTitle = "";
    private String placeReference;
    private Double locationLat;
    private Double locationLong;
    private String locationTitle;
    private ArrayList<GooglePlace> places;
    private String TAG = "ServiceDetailsFragment";
    private String phone_number = "No phone";
    private Menu mOptionsMenu;

    // TODO: Rename and change types and number of parameters
    public static ServiceDetailsFragment newInstance() {
        ServiceDetailsFragment fragment = new ServiceDetailsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_scrolling, container, false);

        //Display menu items
        setHasOptionsMenu(true);

        //Get longitude and latitude of location
        locationLat = getArguments().getDouble("lat");
        locationLong = getArguments().getDouble("long");
        locationTitle = getArguments().getString("title");
        places = (ArrayList<GooglePlace>) getArguments().getSerializable("resultsFromMap");


        //Reference used to query google places for more location information
        placeReference = getArguments().getString("placeReference");

        Log.i(TAG, "Reference here is "+placeReference);

        String placesKey = getResources().getString(R.string.google_maps_key);

        //Query Google Places to extract extra information about a location
        if (placesKey.equals("PUT YOUR KEY HERE")) {
            Log.i("Favourite info", "Problems with your API key");
        } else {
            PlacesDetailReadFeed process = new PlacesDetailReadFeed();
            String placeDetailRequest = "https://maps.googleapis.com/maps/api/place/details/json?" +
                    "key=" + placesKey + "&reference=" + placeReference;
            process.execute(new String[] {placeDetailRequest});
        }

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putDouble("searchAreaLat", place.getGeometry().getLocation().getLat());
                bundle.putDouble("searchAreaLong", place.getGeometry().getLocation().getLng());
                bundle.putSerializable("googlePlaceList", places);

                Fragment fragment = new GmapFragment();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.relativeLayout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });


        //Disable back button
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    Toast.makeText(getActivity(), "Can't press back from service information", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        } );

        return v;
    }


    //Create menu buttons at the top of display
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.services_item_menu, menu);
        MenuItem phoneMenu = menu.findItem(R.id.phone_service);
        mOptionsMenu = menu;
        phoneMenu.expandActionView();

        super.onCreateOptionsMenu(menu, inflater);
    }

    //Actions to perform when menu buttons are clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.phone_service:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+phone_number));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    //Query google places API for location details
    private class PlacesDetailReadFeed extends AsyncTask<String, Void, PlaceDetailFragment> {

        @Override
        protected PlaceDetailFragment doInBackground(String... urls) {
            try {
                String referer = null;
                if (urls.length == 1) {
                    referer = null;
                } else {
                    referer = urls[1];
                }
                String input = GooglePlacesUtility.readGooglePlaces(urls[0], referer);
                Gson gson = new Gson();
                PlaceDetailFragment place = gson.fromJson(input, PlaceDetailFragment.class);
                Log.i("PLACES EXAMPLE", "Place found is " + place.toString());

                return place;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("PLACES EXAMPLE", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(PlaceDetailFragment placeDetailFragment) {
            place = placeDetailFragment.getResult();

            fillInLayout(place);
        }
    }

    //Fill in the scrollview displayed to the user for a given location
    private void fillInLayout(GooglePlace place) {
        // title element has name and types
        TextView title = (TextView)getView().findViewById(R.id.name);
        placeTitle = place.getName();
        title.setText(placeTitle);
        Log.i("PLACES EXAMPLE", "Setting title to: " + title.getText());
        TextView address = (TextView) getView().findViewById(R.id.address_text_view);
        address.setText(place.getFormatted_address());
        Log.i("PLACES EXAMPLE", "Setting address to: " + address.getText());
        TextView reviews = (TextView) getView().findViewById(R.id.reviews);

        String photoReference = "No_photo";
        if (place.getPhotos() != null) {
            Log.i("PLACES", "Photos ref " + place.getPhotos().get(0).getPhoto_reference());
            photoReference = place.getPhotos().get(0).getPhoto_reference();
        }

        String opening_hours = "";
        if (place.getOpeningHours() != null) {
            List<String> openingHoursList = place.getOpeningHours().getWeekday_text();
            for (String day : openingHoursList){
                opening_hours = opening_hours + day + "\n";
            }
            Log.i(TAG, "Hours are " + opening_hours);
        }else{
            opening_hours = "No opening hours found";
        }
        TextView openingHours = (TextView) getView().findViewById(R.id.opening_hours);
        openingHours.setText(opening_hours);

        if (place.getFormatted_phone_number() != null) {
            phone_number = place.getInternational_phone_number();
            Log.i(TAG, "Phone number " + phone_number);
        }else{
            MenuItem phoneService = mOptionsMenu.findItem(R.id.phone_service);
            phoneService.setVisible(false);
        }

        List<GooglePlace.Review> reviewsData = place.getReviews();
        if (reviewsData != null) {
            StringBuffer sb = new StringBuffer();
            for (GooglePlace.Review r : reviewsData) {
                sb.append(r.getAuthor_name());
                sb.append(" says \"");
                sb.append(r.getText());
                sb.append("\" and rated it ");
                sb.append(r.getRating());
                sb.append("\n\n");
            }
            reviews.setText(sb.toString());
        } else {
            reviews.setText("There have not been any reviews!");
        }

        Drawable locationPhoto = getResources().getDrawable(R.drawable.local_services);
        if (!photoReference.equals("No_photo")){
            locationPhoto = getLocationPhoto(photoReference);
        }
        ImageView iv = (ImageView) getView().findViewById(R.id.location_image);
        iv.setBackground(locationPhoto);
    }

    //Query Google places for the locations photo using the photo_reference
    private Drawable getLocationPhoto(String photoReference){
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        URL url = null;
        String placesKey = getResources().getString(R.string.google_maps_key);

        try {
            url = new URL("https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&maxheight=400&photoreference="+photoReference+"&key="+placesKey);
            Log.i(TAG, "URL is "+url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        InputStream content = null;
        try {
            content = (InputStream)url.getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable locationPhoto = Drawable.createFromStream(content , "src");
        return locationPhoto;
    }
}




