package com.example.theom.mmha.Fragments.MapLocation;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.example.theom.mmha.Fragments.Map.GmapFragment;
import com.example.theom.mmha.Fragments.Places.GooglePlace;
import com.example.theom.mmha.Fragments.Places.GooglePlacesUtility;
import com.example.theom.mmha.Fragments.Places.PlaceDetail;
import com.example.theom.mmha.R;
import com.google.gson.Gson;


/**
 * Created by theom on 13/03/2017.
 */

public class MapLocationFragment extends Fragment {

    private GooglePlace place;
    private String placeTitle = "";
    private String placeReference;
    private Double locationLat;
    private Double locationLong;
    private String locationTitle;
    byte [] placeImage;
    private Drawable locationPhoto;
    private ArrayList<GooglePlace> places;
    String photoReference;
    private String TAG = "MapLocationFragment";

    // TODO: Rename and change types and number of parameters
    public static MapLocationFragment newInstance() {
        MapLocationFragment fragment = new MapLocationFragment();
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
        photoReference = getArguments().getString("photoReference");
        Log.i(TAG, "phootoreference is "+photoReference);
        locationPhoto = getLocationPhoto(photoReference);
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

        String launchedFrom = getArguments().getString("launchedFrom");

        if (launchedFrom.equals("Map_fragment")){
            photoReference = getArguments().getString("photoReference");
            Log.i(TAG, "photoreference is "+photoReference);
            //locationPhoto = getLocationPhoto(photoReference);
        } else if (launchedFrom.equals("Favourite_list")){
            //the bytearray image sent from the favouritelist
            placeImage = getArguments().getByteArray("placeImage");
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

        return v;
    }


    //Create menu buttons at the top of display
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.favourite_item_menu, menu);
        MenuItem favouriteMenu = menu.findItem(R.id.favourite_menu_button);
        MenuItem visitedMenu = menu.findItem(R.id.visited);

        favouriteMenu.expandActionView();
        visitedMenu.expandActionView();

        super.onCreateOptionsMenu(menu, inflater);
    }

    //Actions to perform when menu buttons are clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.favourite_menu_button:
               /* isFavourited=!isFavourited;
                changeIcon(item);*/
                return true;
            case R.id.create_postcard:

                return true;
            case R.id.visited:
               /* isVisited=!isVisited;
                changeIcon(item);*/
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    //Query google places API for location details
    private class PlacesDetailReadFeed extends AsyncTask<String, Void, PlaceDetail> {

        @Override
        protected PlaceDetail doInBackground(String... urls) {
            try {
                String referer = null;
                if (urls.length == 1) {
                    referer = null;
                } else {
                    referer = urls[1];
                }
                String input = GooglePlacesUtility.readGooglePlaces(urls[0], referer);
                Gson gson = new Gson();
                PlaceDetail place = gson.fromJson(input, PlaceDetail.class);
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
        protected void onPostExecute(PlaceDetail placeDetail) {
            place = placeDetail.getResult();

            fillInLayout(place);
            ImageView iv = (ImageView) getView().findViewById(R.id.location_image);
            iv.setBackground(locationPhoto);
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
        address.setText(place.getFormatted_address() + " " + place.getFormatted_phone_number());
        Log.i("PLACES EXAMPLE", "Setting address to: " + address.getText());
        TextView reviews = (TextView) getView().findViewById(R.id.reviews);
        Log.i("PLACES", "INfo"+place.getIcon());
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




