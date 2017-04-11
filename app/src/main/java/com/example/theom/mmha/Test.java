package com.example.theom.mmha;

/**
 * Created by theom on 09/04/2017.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import static android.app.Activity.RESULT_OK;

public class Test extends Fragment {
    ImageView chosenImageView;
    String TAG = "Test";
    Integer likertScaleInput;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_question_scale, container, false);

        chosenImageView = (ImageView) v.findViewById(R.id.ChosenImageView);

        setLikertScaleDisplay(v, R.drawable.ic_likert_scale);

        return v;
    }


    private final View.OnTouchListener changeColorListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
            int color = 0;
            if(event.getX() <= 0 || event.getY() <= 0 || event.getY() > bmp.getHeight() || event.getX() > bmp.getWidth()){
                Log.i(TAG, "X or Y == 0");
            }else {
                color = bmp.getPixel((int) event.getX(), (int) event.getY());
            }
            if (color == Color.TRANSPARENT) {
                Log.i("TEST", "False");
                return false;
            }else {
                //code to execute

                Log.i("TEST", "True and the colour is "+color);
                if(color==-339893){
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_4_pressed);
                    likertScaleInput=4;
                }else if(color==-3679941){
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_3_pressed);
                }else if(color==-8012472){
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_2_pressed);
                }else if(color==-11491252){
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_1_pressed);
                }else if(color==-12877256){
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_0_pressed);
                }else if(color==-10460816){
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_no_answer_pressed);
                }else if(color==-65536){
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_10_pressed);
                }else if(color==-50928){
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_9_pressed);
                }else if(color==-1161191){
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_8_pressed);
                }else if(color==-1478654){
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_7_pressed);
                }else if(color==-616953){
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_6_pressed);
                }else if(color==-739027){
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_5_pressed);
                }
                return true;
            }
        }
    };

    private void setLikertScaleDisplay(View v, int displayButtonPressed){

        DbBitmapUtility db = new DbBitmapUtility();
        Bitmap bmp = db.drawableToBitmap(getResources().getDrawable(displayButtonPressed));

        // Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.likert_scale);
        chosenImageView.setDrawingCacheEnabled(true);
        chosenImageView.setOnTouchListener(changeColorListener);
        chosenImageView.setImageBitmap(bmp);

    }
}