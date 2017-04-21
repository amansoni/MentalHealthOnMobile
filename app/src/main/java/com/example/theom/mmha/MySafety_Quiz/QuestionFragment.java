package com.example.theom.mmha.MySafety_Quiz;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
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

import com.example.theom.mmha.DbBitmapUtility;
import com.example.theom.mmha.MySafety_Quiz.Dialogs.InfoDialog;
import com.example.theom.mmha.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QuestionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestionFragment extends Fragment {

    //Parsing the JSON
    final JSON_parser senorJSON_parser = new JSON_parser();
    TextView questionTextView;
    ImageView chosenImageView;
    private AnsweredQuestionsDBHelper answersDB;
    String TAG = "QuestionFragment";
    Boolean leafNodeReached = false;
    QuestionObject currentQuestion;
    FrameLayout frameLayout;
    View view;
    Menu mOptionsMenu;
    Integer likertScaleInput;
    Long id;
    HashMap<String, String> userAnswersHashMap = new HashMap<String, String>();
    Float scaleValue = Float.valueOf(0);

    private OnFragmentInteractionListener mListener;

    public QuestionFragment() {

        // Required empty public constructor
    }

    public static QuestionFragment newInstance() {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        frameLayout = new FrameLayout(getActivity());
        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.fragment_question, null);
        frameLayout.addView(view);

        QuestionObject firstQuestion = senorJSON_parser.setupQuiz(getActivity());
        currentQuestion = firstQuestion;
        //Create database to store assessment answers
        answersDB = new AnsweredQuestionsDBHelper(getActivity());
        //id of current assessment session in database
        id = getArguments().getLong("id");

        questionTextView = (TextView) frameLayout.findViewById(R.id.questionTextView);
        questionTextView.setText(firstQuestion.getQuestionText());

        //Yes and no buttons in layout
        Button mYesButton = (Button) frameLayout.findViewById(R.id.yesButton);
        Button mNoButton = (Button) frameLayout.findViewById(R.id.noButton);

        //Get question text for when yes button is clicked
        mYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetNextQuestion("Yes");
            }
        });

        //Get question text for when no button is clicked
        mNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetNextQuestion("No");
            }
        });

        //Disable back button
        frameLayout.setFocusableInTouchMode(true);
        frameLayout.requestFocus();
        frameLayout.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    Toast.makeText(getActivity(), "Can't press back during assessment", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        } );

        return frameLayout;
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void GetNextQuestion(String answer) {
        //Enter answer into hashmap for storing
        Log.i(TAG, "Putting "+currentQuestion.getQuestionText()+" into Hashmap and the answer was "+answer);
        userAnswersHashMap.put(currentQuestion.getQuestionText(), answer);
        try {
            //take users input, ask JSON_Parser for next question
            QuestionObject question = senorJSON_parser.runAssessment(answer, getActivity());
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
            submitUserAnswers();
            String leafNodeResult = question.getLeafNodeResult();
            Log.i(TAG, "Houston, we reached the leaf node. "+leafNodeResult);
            Bundle bundle = new Bundle();
            bundle.putString("resultsOfAssessment", leafNodeResult);
            bundle.putLong("id", id);
            bundle.putFloat("scaleValue",scaleValue);
            Fragment fragment = new AssessmentFinishFragment();
            fragment.setArguments(bundle);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.relativeLayout, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }
    }

    private void submitUserAnswers(){
        Gson objGson= new Gson();
        String strObject = objGson.toJson(userAnswersHashMap);
        Bundle bundle = new Bundle();
        bundle.putString("key", strObject);
        String id_string = Long.toString(id);
        answersDB.insertAssessmentAnswers(id_string, strObject);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.question_fragment_info_menu, menu);
        mOptionsMenu = menu;
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem helpMenu = menu.findItem(R.id.menu_show_help);
        if (currentQuestion.getQuestionHelp() != "") {
            helpMenu.setVisible(true);
        } else {
            helpMenu.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_show_extra_info:
                InfoDialog extraInfoDialog = InfoDialog.newInstance("Question Information", currentQuestion, "");
                extraInfoDialog.setTargetFragment(this, 0);
                extraInfoDialog.show(getActivity().getSupportFragmentManager(), "fragmentDialog");
                return true;
            case R.id.menu_show_help:
                InfoDialog helpDialog = InfoDialog.newInstance("Help", currentQuestion, "");
                helpDialog.setTargetFragment(this, 0);
                helpDialog.show(getActivity().getSupportFragmentManager(), "fragmentDialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupLayerQuestionDisplay(QuestionObject question){
        Button mYesButton = (Button) frameLayout.findViewById(R.id.yesButton);
        Button mNoButton = (Button) frameLayout.findViewById(R.id.noButton);

        mYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetNextQuestion("Yes");
            }
        });
        mNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetNextQuestion("No");
            }
        });
    }

    private void setupLikertScaleDisplay(QuestionObject question) {
        chosenImageView = (ImageView) frameLayout.findViewById(R.id.ChosenImageView);
        updateLikertScaleDisplay(frameLayout, R.drawable.ic_likert_scale);

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

        TextView scaleInformationTxtView = (TextView) frameLayout.findViewById(R.id.scale_information);
        scaleInformationTxtView.setText(scaleInformation);

        Button nextQuestion = (Button) frameLayout.findViewById(R.id.next_question);
        nextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar
                        .make(frameLayout, "Submitted the value " + likertScaleInput, Snackbar.LENGTH_SHORT);
                GetNextQuestion("Yes");
                scaleValue = Float.valueOf(likertScaleInput)/10;
                Log.i(TAG, "Scale input value is "+scaleValue);
                snackbar.show();
            }
        });
    }

    private void updateLikertScaleDisplay(View v, int displayButtonPressed) {
        DbBitmapUtility dbUtil = new DbBitmapUtility();
        Bitmap bmp = dbUtil.drawableToBitmap(getResources().getDrawable(displayButtonPressed));

        // Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.likert_scale);
        chosenImageView.setDrawingCacheEnabled(true);
        chosenImageView.setOnTouchListener(changeColorListener);
        chosenImageView.setImageBitmap(bmp);

    }

    private final View.OnTouchListener changeColorListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Bitmap bmp = Bitmap.createBitmap(v.getDrawingCache());
            int color = 0;
            if (event.getX() <= 0 || event.getY() <= 0 || event.getY() > bmp.getHeight() || event.getX() > bmp.getWidth()) {
                Log.i(TAG, "X or Y == 0");
            } else {
                color = bmp.getPixel((int) event.getX(), (int) event.getY());
            }
            if (color == Color.TRANSPARENT) {
                Log.i("TEST", "False");
                return false;
            } else {
                //code to execute

                Log.i("TEST", "True and the colour is " + color);
                if (color == -339893) {
                    updateLikertScaleDisplay(v, R.drawable.ic_likert_scale_4_pressed);
                    likertScaleInput = 4;
                } else if (color == -3679941) {
                    updateLikertScaleDisplay(v, R.drawable.ic_likert_scale_3_pressed);
                    likertScaleInput = 3;
                } else if (color == -8012472) {
                    updateLikertScaleDisplay(v, R.drawable.ic_likert_scale_2_pressed);
                    likertScaleInput = 2;
                } else if (color == -11491252) {
                    updateLikertScaleDisplay(v, R.drawable.ic_likert_scale_1_pressed);
                    likertScaleInput = 1;
                } else if (color == -12877256) {
                    updateLikertScaleDisplay(v, R.drawable.ic_likert_scale_0_pressed);
                    likertScaleInput = 0;
                } else if (color == -10460816) {
                    updateLikertScaleDisplay(v, R.drawable.ic_likert_scale_no_answer_pressed);
                    likertScaleInput = 11;
                } else if (color == -65536) {
                    updateLikertScaleDisplay(v, R.drawable.ic_likert_scale_10_pressed);
                    likertScaleInput = 10;
                } else if (color == -50928) {
                    updateLikertScaleDisplay(v, R.drawable.ic_likert_scale_9_pressed);
                    likertScaleInput = 9;
                } else if (color == -1161191) {
                    updateLikertScaleDisplay(v, R.drawable.ic_likert_scale_8_pressed);
                    likertScaleInput = 8;
                } else if (color == -1478654) {
                    updateLikertScaleDisplay(v, R.drawable.ic_likert_scale_7_pressed);
                    likertScaleInput = 7;
                } else if (color == -616953) {
                    updateLikertScaleDisplay(v, R.drawable.ic_likert_scale_6_pressed);
                    likertScaleInput = 6;
                } else if (color == -739027) {
                    updateLikertScaleDisplay(v, R.drawable.ic_likert_scale_5_pressed);
                    likertScaleInput = 5;
                }
                return true;
            }
        }
    };

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
                Integer selectedId = nominalRadioButtons.getCheckedRadioButtonId();
                RadioButton selectedRadButton = (RadioButton) frameLayout.findViewById(selectedId);

                String nominalValue = "0";
                if (selectedRadButton != null) {
                    nominalValue = selectedRadButton.getTag().toString();
                }
                Snackbar snackbar = Snackbar
                        .make(frameLayout, "Nominal value " + nominalValue, Snackbar.LENGTH_SHORT);
                GetNextQuestion("Yes");
                scaleValue = Float.valueOf(nominalValue);
                snackbar.show();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        OnSetToolbarTitleListener callback;
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = (OnSetToolbarTitleListener) activity;
            callback.setTitle("Assessment");
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public interface OnSetToolbarTitleListener {
        public void setTitle(String title);
    }

}
