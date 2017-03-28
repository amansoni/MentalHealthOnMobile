package com.example.theom.mmha.Fragments.Places;

import com.example.theom.mmha.R;

import java.util.HashMap;




public class PlacePins {


    public HashMap PlacePins(){
        return buildPinsHashmap();
    }


    private static HashMap<String, Integer> buildPinsHashmap(){
        HashMap<String, Integer> placesPins = new HashMap<String, Integer>();
        placesPins.put("accounting", R.drawable.ic_map_pin);
        placesPins.put("airport", R.drawable.ic_map_pin);

        return placesPins;
    }
}
