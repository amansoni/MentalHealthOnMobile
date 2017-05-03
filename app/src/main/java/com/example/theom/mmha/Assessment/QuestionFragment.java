package com.example.theom.mmha.Assessment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.theom.mmha.BitmapUtility;
import com.example.theom.mmha.Assessment.Dialogs.ActionDialog;
import com.example.theom.mmha.Assessment.Dialogs.InfoDialog;
import com.example.theom.mmha.PreviousAssessments.AnsweredQuestionsDBHelper;
import com.example.theom.mmha.PreviousAssessments.PrevAssessmentListItem;
import com.example.theom.mmha.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestionFragment extends Fragment {
    //Singleton instance of the JSON parsing class that is used throughout the assessment
    final JSON_parser senorJSON_parser = new JSON_parser();
    //Database used to store the user's answers and access their setup information
    private AnsweredQuestionsDBHelper answersDB;
    //ID used to access the setup information from the database
    Long id;
    //Store question and user answer in HashMap to then add to DB
    HashMap<String, String> userAnswersHashMap = new HashMap<String, String>();
    //Class LOG ID
    String TAG = "QuestionFragment";
    //Check if leaf node is reached
    Boolean leafNodeReached = false;
    //Current question being viewed by the user
    QuestionObject currentQuestion;
    //Layout used for swapping in different question types
    FrameLayout frameLayout;
    View view;
    //TextView to display question text
    TextView questionTextView;
    //ImageView used to display the Likert scale
    ImageView chosenImageView;
    //Submitted Liker scale input
    Integer likertScaleInput;
    //Menu options (dev and extra question information)
    Menu mOptionsMenu;
    //To keep track of
    Float maxScaleValue = Float.valueOf(0);

    public QuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and allow for it to be swapped out dynamically
        frameLayout = new FrameLayout(getActivity());
        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.fragment_question, null);
        frameLayout.addView(view);
        //First question of the assessment
        QuestionObject firstQuestion = senorJSON_parser.setupAssessment(getActivity());
        currentQuestion = firstQuestion;
        //Setup the menu icons
        setHasOptionsMenu(true);
        //Create database to store assessment answers
        answersDB = new AnsweredQuestionsDBHelper(getActivity());
        //id of current assessment session in database
        id = getArguments().getLong("id");
        //Setup TextView and set text
        questionTextView = (TextView) frameLayout.findViewById(R.id.questionTextView);
        questionTextView.setText(firstQuestion.getQuestionText());

        //Yes and no buttons in layout
        Button mYesButton = (Button) frameLayout.findViewById(R.id.yesButton);
        Button mNoButton = (Button) frameLayout.findViewById(R.id.noButton);

        //Get question text for when yes button is clicked
        mYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNextQuestion("Yes");
            }
        });

        //Get question text for when no button is clicked
        mNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNextQuestion("No");
            }
        });

        //Disable back button
        frameLayout.setFocusableInTouchMode(true);
        frameLayout.requestFocus();
        frameLayout.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Toast.makeText(getActivity(), "Can't press back during assessment", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        return frameLayout;
    }

    //IMPORTANT METHOD ---- Takes user's input, and will retrieve next question if !leafnode, else displays assessment finish
    public void getNextQuestion(String answer) {
        //Enter answer into hashmap for storing
        Log.i(TAG, "Putting " + currentQuestion.getQuestionText() + " into Hashmap and the answer was " + answer);
        userAnswersHashMap.put(currentQuestion.getQuestionText(), answer);
        try {
            //take users input, ask JSON_Parser for next question
            QuestionObject question = senorJSON_parser.progressAssessment(answer, getActivity());
            //Check if this question is a leaf node
            leafNodeReached = question.isLeafNode();
            //update the question fragment to new question's layout
            changeView(question);
            //update current question global var
            currentQuestion = question;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void changeView(QuestionObject question) {
        //default layout for question fragment
        int optionId = R.layout.fragment_question;

        //depending on question type, choose appropriate question layout
        if (question.getQuestionType().equals("layer")) {
            optionId = R.layout.fragment_question;
        } else if (question.getQuestionType().equals("nominal")) {
            optionId = R.layout.fragment_question_nominal;
        } else if (question.getQuestionType().equals("scale")) {
            optionId = R.layout.fragment_question_scale;
        }
        //swap in new layouts from old one (even if there is an already inflated layout)
        frameLayout.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(optionId, null);
        frameLayout.addView(view);

        //set question text in the layout
        questionTextView = (TextView) view.findViewById(R.id.questionTextView);
        questionTextView.setText(question.getQuestionText());

        //if extra help information is available for a question, display 'i' menu icon, else display nothing
        MenuItem helpMenu = mOptionsMenu.findItem(R.id.menu_show_help);
        if (question.getQuestionHelp() != "") {
            helpMenu.setVisible(true);
        } else {
            helpMenu.setVisible(false);
        }

        //Check that layout contains yes/no buttons
        if (leafNodeReached == false && optionId == R.layout.fragment_question) {
            setupLayerQuestionDisplay(question);
        } else if (leafNodeReached == false && optionId == R.layout.fragment_question_nominal) {
            setupNominalScaleDisplay(question);
        } else if (leafNodeReached == false && optionId == R.layout.fragment_question_scale) {
            setupLikertScaleDisplay(question);
        } else if (leafNodeReached == true) {
            //Submit answers to database
            submitUserAnswers();
            String leafNodeResult = question.getLeafNodeResult();
            Log.i(TAG, "Houston, we reached the leaf node. " + leafNodeResult);
            //Launch finish screen fragment to complete quiz
            Bundle bundle = new Bundle();
            bundle.putString("resultsOfAssessment", leafNodeResult);
            bundle.putLong("id", id);
            bundle.putFloat("scaleValue", maxScaleValue);
            Log.i(TAG, "scaleValue value is "+maxScaleValue);
            Fragment fragment = new AssessmentFinishFragment();
            fragment.setArguments(bundle);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.relativeLayout, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }
    }

    //Add user's answers to the database when assessment is complete
    private void submitUserAnswers() {
        //Convert the hashmap into a JSON string to be stored
        Gson objGson = new Gson();
        String strObject = objGson.toJson(userAnswersHashMap);
        Bundle bundle = new Bundle();
        bundle.putString("key", strObject);
        String id_string = Long.toString(id);
        //Add to database
        answersDB.insertAssessmentAnswers(id_string, strObject);
    }

    //Setup menu options for fragment
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.question_fragment_info_menu, menu);
        mOptionsMenu = menu;
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem helpMenu = menu.findItem(R.id.menu_show_help);
        //If there is no additional help information, don't display the 'i' icon
        if (currentQuestion.getQuestionHelp() != "") {
            helpMenu.setVisible(true);
        } else {
            helpMenu.setVisible(false);
        }
    }

    //Handlers for when menu item is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            //Extra dev information for a given question (like question code etc...)
            case R.id.menu_show_extra_info:
                InfoDialog extraInfoDialog = InfoDialog.newInstance("Question Information", currentQuestion, "");
                extraInfoDialog.setTargetFragment(this, 0);
                extraInfoDialog.show(getActivity().getSupportFragmentManager(), "fragmentDialog");
                return true;
            //Text to help the user understand the question
            case R.id.menu_show_help:
                InfoDialog helpDialog = InfoDialog.newInstance("Help", currentQuestion, "");
                helpDialog.setTargetFragment(this, 0);
                helpDialog.show(getActivity().getSupportFragmentManager(), "fragmentDialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Setup display for a 'Yes' 'No' question
    private void setupLayerQuestionDisplay(QuestionObject question) {
        Button mYesButton = (Button) frameLayout.findViewById(R.id.yesButton);
        Button mNoButton = (Button) frameLayout.findViewById(R.id.noButton);

        mYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNextQuestion("Yes");
            }
        });
        mNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNextQuestion("No");
            }
        });
    }

    //Setup display for Likert display
    private void setupLikertScaleDisplay(QuestionObject question) {
        //Variable to store current input on the Likert scale
        likertScaleInput = 0;
        //Current image view based on what the Likert scale value i.e. if 5 is selected, have the correct SVG loaded
        chosenImageView = (ImageView) frameLayout.findViewById(R.id.ChosenImageView);
        //Display the Likert scale with the 'unselected' Likert SVG being first displayed
        updateLikertScaleDisplay(R.drawable.ic_likert_scale);

        //Extract the information for the scale, indicating what the scale value represents
        String scaleInformationRaw = question.getScaleInformation();
        String scaleInformation = "";
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(scaleInformationRaw);
        int i = 0;
        while (m.find()) {
            String text = m.group(1);
            String textCap = text.substring(0, 1).toUpperCase() + text.substring(1);
            if (i == 0) {
                textCap = "0 = " + textCap + "\n";
            } else {
                textCap = "10 = " + textCap;
            }
            //Capitalise text
            scaleInformation = scaleInformation + textCap;
            Log.i(TAG, scaleInformation);
            i++;
        }

        //Take information extracted from previously and display
        TextView scaleInformationTxtView = (TextView) frameLayout.findViewById(R.id.scale_information);
        scaleInformationTxtView.setText(scaleInformation);

        //Handler for user input when they click next question
        Button nextQuestion = (Button) frameLayout.findViewById(R.id.next_question);
        nextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Submitted the value " + likertScaleInput, Toast.LENGTH_SHORT).show();
                //to retain highest risk mid assessment action
                Float tempScaleValue = maxScaleValue;
                maxScaleValue = 0f;
                if (likertScaleInput != null) {
                    maxScaleValue = Float.valueOf(likertScaleInput) / 10;
                    calculateAction(maxScaleValue.toString());
                    //highest risk scale action is shown at the end
                    if (tempScaleValue > maxScaleValue){
                        maxScaleValue=tempScaleValue;
                    }
                }else{
                    likertScaleInput = 0;
                }
                //Next question button brings up options for 'Yes'
                getNextQuestion("Yes");
            }
        });
    }

    //Update display based on the selection the user has made on the Likert scale
    private void updateLikertScaleDisplay(int displayButtonPressed) {
        //Convert SVG into image drawable/bitmap
        BitmapUtility dbUtil = new BitmapUtility();
        Bitmap bmp = dbUtil.drawableToBitmap(getResources().getDrawable(displayButtonPressed));
        //Setup the Likert scale image for interaction
        chosenImageView.setDrawingCacheEnabled(true);
        chosenImageView.setOnTouchListener(changeColorListener);
        chosenImageView.setImageBitmap(bmp);

    }

    //Handle input of when the likert scale is touched
    private final View.OnTouchListener changeColorListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
            int color = 0;
            //Handle touches in area that cause system crashes
            if (event.getX() <= 0 || event.getY() <= 0 || event.getY() > bmp.getHeight() || event.getX() > bmp.getWidth()) {
                Log.i(TAG, "X or Y == 0");
            } else {
                color = bmp.getPixel((int) event.getX(), (int) event.getY());
            }
            if (color == Color.TRANSPARENT) {
                Log.i("TEST", "False");
                return false;
            } else {
                //if statements to select appropriate likert scale to display
                if (color == -339893) {
                    updateLikertScaleDisplay(R.drawable.ic_likert_scale_4_pressed);
                    likertScaleInput = 4;
                } else if (color == -3679941) {
                    updateLikertScaleDisplay(R.drawable.ic_likert_scale_3_pressed);
                    likertScaleInput = 3;
                } else if (color == -8012472) {
                    updateLikertScaleDisplay(R.drawable.ic_likert_scale_2_pressed);
                    likertScaleInput = 2;
                } else if (color == -11491252) {
                    updateLikertScaleDisplay(R.drawable.ic_likert_scale_1_pressed);
                    likertScaleInput = 1;
                } else if (color == -12877256) {
                    updateLikertScaleDisplay(R.drawable.ic_likert_scale_0_pressed);
                    likertScaleInput = 0;
                } else if (color == -10460816) {
                    updateLikertScaleDisplay(R.drawable.ic_likert_scale_no_answer_pressed);
                    likertScaleInput = 0;
                } else if (color == -65536) {
                    updateLikertScaleDisplay(R.drawable.ic_likert_scale_10_pressed);
                    likertScaleInput = 10;
                } else if (color == -50928) {
                    updateLikertScaleDisplay(R.drawable.ic_likert_scale_9_pressed);
                    likertScaleInput = 9;
                } else if (color == -1161191) {
                    updateLikertScaleDisplay(R.drawable.ic_likert_scale_8_pressed);
                    likertScaleInput = 8;
                } else if (color == -1478654) {
                    updateLikertScaleDisplay(R.drawable.ic_likert_scale_7_pressed);
                    likertScaleInput = 7;
                } else if (color == -616953) {
                    updateLikertScaleDisplay(R.drawable.ic_likert_scale_6_pressed);
                    likertScaleInput = 6;
                } else if (color == -739027) {
                    updateLikertScaleDisplay(R.drawable.ic_likert_scale_5_pressed);
                    likertScaleInput = 5;
                }
                return true;
            }
        }
    };

    //Nominal scale setup
    private void setupNominalScaleDisplay(QuestionObject question) {
        String radioButtonValues = question.getQuestionMG();
        radioButtonValues = radioButtonValues.substring(2, radioButtonValues.length() - 2);
        String[] radioButtonValuesArray = radioButtonValues.split("\\)\\(");
        HashMap<String, Float> nominalScaleValues = new HashMap<>();
        HashMap<String, String> nominalScaleTitles = new HashMap<>();

        Integer i = 0;
        for (String value : radioButtonValuesArray) {
            String text = value.split(" ")[0];

            //Capitalise text
            String nominalText = text.substring(0, 1).toUpperCase() + text.substring(1);

            //Extract nominal value from mg string
            Float nominalValue = Float.parseFloat(value.split(" ")[1]);

            //Insert extracted values into respected hashmaps
            nominalScaleValues.put(i.toString(), nominalValue);
            nominalScaleTitles.put(i.toString(), nominalText);

            i++;
        }
        final RadioGroup nominalRadioButtons = (RadioGroup) frameLayout.findViewById(R.id.nominal_radio_buttons);

        RadioButton r1 = (RadioButton) frameLayout.findViewById(R.id.nominal1);
        RadioButton r2 = (RadioButton) frameLayout.findViewById(R.id.nominal2);
        RadioButton r3 = (RadioButton) frameLayout.findViewById(R.id.nominal3);
        RadioButton r4 = (RadioButton) frameLayout.findViewById(R.id.nominal4);

        r1.setTag(nominalScaleValues.get("0"));
        r1.setText(nominalScaleTitles.get("0"));

        r2.setTag(nominalScaleValues.get("1"));
        r2.setText(nominalScaleTitles.get("1"));

        r3.setTag(nominalScaleValues.get("2"));
        r3.setText(nominalScaleTitles.get("2"));

        r4.setTag(nominalScaleValues.get("3"));
        r4.setText(nominalScaleTitles.get("3"));

        Button nextQuestion = (Button) frameLayout.findViewById(R.id.next_question);
        nextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the selected radio button and assign it to nominalValue
                Integer selectedId = nominalRadioButtons.getCheckedRadioButtonId();
                RadioButton selectedRadButton = (RadioButton) frameLayout.findViewById(selectedId);
                String nominalValue = "0";
                if (selectedRadButton != null) {
                    nominalValue = selectedRadButton.getTag().toString();
                }
                //Logic to keep the max scale input for the assessment stored to display at the end
                Float tempScaleValue = maxScaleValue;
                maxScaleValue = 0f;
                maxScaleValue = Float.valueOf(nominalValue);
                calculateAction(nominalValue);
                //highest risk scale action is shown at the end
                if (tempScaleValue > maxScaleValue){
                    maxScaleValue=tempScaleValue;
                }
                Log.i(TAG, "Float value is "+maxScaleValue);
                getNextQuestion("Yes");
            }
        });
    }

    //Set fragment title
    public interface OnSetToolbarTitleListener {
        public void setTitle(String title);
    }

    //Calculate how to handle a risk input from nominal or likert scale
    private void calculateAction(String result) {
        Float riskValue = Float.valueOf(result);
        final LatLng location = getLocation();
        String adviceActionText;
        String actionButtonText;

        if (riskValue >= 0.75) {

            adviceActionText = "Based on your answer, you should go visit your nearest A&E";
            actionButtonText = "Find A&E";
            //Launch dialog to then show map with A&Es posted on map
            ActionDialog extraInfoDialog = ActionDialog.newInstance("Action", location.latitude, location.longitude, actionButtonText, adviceActionText);
            extraInfoDialog.setTargetFragment(this, 0);
            extraInfoDialog.show(getActivity().getSupportFragmentManager(), "fragmentDialog");

        } else if (riskValue >= 0.5) {
            adviceActionText = "Based on your answer, you should call 111 for further advice";
            actionButtonText = "Call 111";
            //Launch dialog to then go to phone dialler with 111 inputted
            ActionDialog extraInfoDialog = ActionDialog.newInstance("Action", location.longitude, location.latitude, actionButtonText, adviceActionText);
            extraInfoDialog.setTargetFragment(this, 0);
            extraInfoDialog.show(getActivity().getSupportFragmentManager(), "fragmentDialog");
        } else if (riskValue >= 0.3) {
            adviceActionText = "Based on your results, you should go visit your GP";
            actionButtonText = "Find GP";
            //Launch dialog to then show map with nearest GPs listed
            ActionDialog extraInfoDialog = ActionDialog.newInstance("Action", location.longitude, location.latitude, actionButtonText, adviceActionText);
            extraInfoDialog.setTargetFragment(this, 0);
            extraInfoDialog.show(getActivity().getSupportFragmentManager(), "fragmentDialog");
        } else if (riskValue >= 0.1) {
            adviceActionText = "Would you like to text a friend for some support?";
            actionButtonText = "Text friend";
            //Launch dialog to send text with precompossed message
            ActionDialog extraInfoDialog = ActionDialog.newInstance("Action", location.longitude, location.latitude, actionButtonText, adviceActionText);
            extraInfoDialog.setTargetFragment(this, 0);
            extraInfoDialog.show(getActivity().getSupportFragmentManager(), "fragmentDialog");
        } else if (riskValue < 0.1) {
        }
    }

    //Get the assessment location from the DB to execute action
    private LatLng getLocation() {
        ArrayList<PrevAssessmentListItem> data = new ArrayList<>();
        String id = Float.toString(this.id);
        Cursor res = answersDB.getAssessmentDetails(id);
        StringBuffer dbContents = new StringBuffer();
        LatLng location = new LatLng(0.0,0.0);

        if (res.getCount() == 0) {
            PrevAssessmentListItem current = new PrevAssessmentListItem("0", "No Assessments Found", "");
            data.add(current);
        } else {
            while (res.moveToNext()) {
                String[] locationStrings = res.getString(5).split("\\:")[2].split(",");
                Double lat = Double.parseDouble(locationStrings[0].substring(2));
                Double longitude = Double.parseDouble(locationStrings[1].substring(0,locationStrings[1].length()-1));
                location = new LatLng(lat, longitude);
                Log.i(TAG, "Location string is "+location);

                String gender = res.getString(2);
                String dateOfAssessment = res.getString(7);

                PrevAssessmentListItem current = new PrevAssessmentListItem(id, dateOfAssessment, gender);
                data.add(current);
            }
            Log.i("Assessment_list", dbContents.toString());
        }
        return location;
    }
}
