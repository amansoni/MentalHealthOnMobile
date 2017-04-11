package com.example.theom.mmha.MySafety_Quiz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import com.example.theom.mmha.DbBitmapUtility;
import com.example.theom.mmha.MySafety_Quiz.Dialogs.InfoDialog;
import com.example.theom.mmha.R;

import org.json.JSONException;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QuestionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestionFragment extends Fragment {

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
        //View v = inflater.inflate(R.layout.fragment_question, container, false);

        frameLayout = new FrameLayout(getActivity());
        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.fragment_question, null);
        frameLayout.addView(view);


        //Parsing the JSON
        final JSON_parser senorJSON_parser = new JSON_parser();
        QuestionObject firstQuestion = senorJSON_parser.setupQuiz(getActivity());
        currentQuestion = firstQuestion;
        //Create database to store assessment answers
        answersDB = new AnsweredQuestionsDBHelper(getActivity());

        questionTextView = (TextView) frameLayout.findViewById(R.id.questionTextView);
        questionTextView.setText(firstQuestion.getQuestionText());

        Button mYesButton = (Button) frameLayout.findViewById(R.id.yesButton);
        Button mNoButton = (Button) frameLayout.findViewById(R.id.noButton);

        if (leafNodeReached == false) {
            mYesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetFragmentText("Yes", senorJSON_parser);
                }
            });
            mNoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetFragmentText("No", senorJSON_parser);
                }
            });
        }
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

    public Boolean GetFragmentText(String answer, JSON_parser senorJSON_parser) {

        Context ctx = getActivity();
        QuestionObject question = null;

        try {
            if (leafNodeReached == false) {
                question = senorJSON_parser.runQuiz(answer, ctx, currentQuestion);
                leafNodeReached = question.isLeafNode();

                changeView(question, senorJSON_parser);

                currentQuestion = question;
            } else {
                Log.i(TAG, "Houston, we reached the leaf node.");
                questionTextView.setText("Leaf node reached");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return leafNodeReached;
    }

    public void ChangeButtonStatus() {
        Button mYesButton = (Button) getActivity().findViewById(R.id.yesButton);
        Button mNoButton = (Button) getActivity().findViewById(R.id.noButton);
        mYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    public void changeView(QuestionObject question, final JSON_parser senorJSON_parser) {

        int optionId = R.layout.fragment_question;

        if (question.getQuestionType().equals("layer")) {
            optionId = R.layout.fragment_question;
        } else if (question.getQuestionType().equals("nominal")) {
            optionId = R.layout.fragment_question_nominal;
        } else if (question.getQuestionType().equals("scale")) {
            optionId = R.layout.fragment_question_scale;
        }

        frameLayout.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(optionId, null);
        frameLayout.addView(view);

        questionTextView = (TextView) view.findViewById(R.id.questionTextView);

        questionTextView.setText(question.getQuestionText());

        MenuItem helpMenu = mOptionsMenu.findItem(R.id.menu_show_help);
        if (question.getQuestionHelp() != "") {
            helpMenu.setVisible(true);
        } else {
            helpMenu.setVisible(false);
        }

        //Check that layout contains yes/no buttons
        if (leafNodeReached == false && optionId == R.layout.fragment_question) {

            Button mYesButton = (Button) frameLayout.findViewById(R.id.yesButton);
            Button mNoButton = (Button) frameLayout.findViewById(R.id.noButton);

            mYesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetFragmentText("Yes", senorJSON_parser);
                }
            });
            mNoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetFragmentText("No", senorJSON_parser);
                }
            });
        } else if (leafNodeReached == false && optionId == R.layout.fragment_question_nominal) {
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
                    GetFragmentText("Yes", senorJSON_parser);

                    snackbar.show();
                }
            });
        } else if (leafNodeReached == false && optionId == R.layout.fragment_question_scale) {
            chosenImageView = (ImageView) frameLayout.findViewById(R.id.ChosenImageView);
            setLikertScaleDisplay(frameLayout, R.drawable.ic_likert_scale);

            TextView scaleInformation = (TextView) frameLayout.findViewById(R.id.scale_information);
            scaleInformation.setText(question.getScaleInformation());
            Log.i(TAG, "Scale Information " + question.getQuestionText());

            Button nextQuestion = (Button) frameLayout.findViewById(R.id.next_question);
            nextQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar snackbar = Snackbar
                            .make(frameLayout, "Submitted the value " + likertScaleInput, Snackbar.LENGTH_SHORT);
                    GetFragmentText("Yes", senorJSON_parser);

                    snackbar.show();
                }
            });

        }
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
                InfoDialog extraInfoDialog = InfoDialog.newInstance("Question Information", currentQuestion);
                extraInfoDialog.setTargetFragment(this, 0);
                extraInfoDialog.show(getActivity().getSupportFragmentManager(), "fragmentDialog");
                return true;
            case R.id.menu_show_help:
                InfoDialog helpDialog = InfoDialog.newInstance("Help", currentQuestion);
                helpDialog.setTargetFragment(this, 0);
                helpDialog.show(getActivity().getSupportFragmentManager(), "fragmentDialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setLikertScaleDisplay(View v, int displayButtonPressed) {
        DbBitmapUtility db = new DbBitmapUtility();
        Bitmap bmp = db.drawableToBitmap(getResources().getDrawable(displayButtonPressed));

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
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_4_pressed);
                    likertScaleInput = 4;
                } else if (color == -3679941) {
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_3_pressed);
                    likertScaleInput = 3;
                } else if (color == -8012472) {
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_2_pressed);
                    likertScaleInput = 2;
                } else if (color == -11491252) {
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_1_pressed);
                    likertScaleInput = 1;
                } else if (color == -12877256) {
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_0_pressed);
                    likertScaleInput = 0;
                } else if (color == -10460816) {
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_no_answer_pressed);
                    likertScaleInput = 11;
                } else if (color == -65536) {
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_10_pressed);
                    likertScaleInput = 10;
                } else if (color == -50928) {
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_9_pressed);
                    likertScaleInput = 9;
                } else if (color == -1161191) {
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_8_pressed);
                    likertScaleInput = 8;
                } else if (color == -1478654) {
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_7_pressed);
                    likertScaleInput = 7;
                } else if (color == -616953) {
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_6_pressed);
                    likertScaleInput = 6;
                } else if (color == -739027) {
                    setLikertScaleDisplay(v, R.drawable.ic_likert_scale_5_pressed);
                    likertScaleInput = 5;
                }
                return true;
            }
        }
    };

}
