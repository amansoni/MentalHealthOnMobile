package com.example.theom.mmha.LocalSerivces.Places;

/**
 * Created by theom on 03/03/2017.
 */

public class PlaceDetailFragment {
    //@Key
    private GooglePlace result;

    public GooglePlace getResult() {
        return result;
    }

    public void setResult(GooglePlace result) {
        this.result = result;
    }

    @Override
    public String toString() {
        if (result!=null) {
            return result.toString();
        }
        return super.toString();
    }
}
