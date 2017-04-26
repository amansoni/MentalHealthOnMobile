package com.example.theom.mmha.LocalSerivces.Map;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;



public class MyItem implements ClusterItem {
    private final LatLng mPosition;
    private final String mTitle;
    private BitmapDescriptor icon;
    private Integer placeArrayPosition;

public MyItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
        mTitle = "Hi";
    }


    public MyItem(double lat, double lng, String title, BitmapDescriptor icon, Integer placeArrayPosition) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        this.icon=icon;
        this.placeArrayPosition = placeArrayPosition;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    public BitmapDescriptor getIcon(){
        return icon;
    }

    public Integer getPlaceArrayPosition(){
        return placeArrayPosition;
    }

    @Override
    public String getSnippet() {
        return "Click me for more info";
    }
}
