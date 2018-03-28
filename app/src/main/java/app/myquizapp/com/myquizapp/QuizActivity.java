package app.myquizapp.com.myquizapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import app.myquizapp.com.myquizapp.model.Question;

public class QuizActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private static final int QUIZ_LOADER_ID = 22;
    private static final String LEVEL_EASY = "easy";
    private static final String LEVEL_INTERMEDIATE = "intermediate";
    private static final String LEVEL_DIFFICULT = "difficult";
    private static final String LEVEL = "level";

    private static final String CURRENT_INDEX_KEY = "current-index",
            CORRECT_ANSWERS_COUNT = "correct-answers";
    private static final String PREF_EASY = "pref-level-easy";
    private static final String PREF_EASY_SETS_COMPLETED = "pref-easy-sets-completed";
    private static final String PREF_INTERMED_SETS_COMPLETED = "pref-intermed-sets-completed";
    private static final String PREF_DIFF_SETS_COMPLETED = "pref-diff-sets-completed";
    private static final String LEVEL_KEY = "level-key";
    private static final String PREF_SET_KEY = "set-key";

    private CardView optionOneCard, optionTwoCard, optionThreeCard, optionFourCard;
    private TextView optionOneTextView, optionTwoTextView, optionThreeTextView, optionFourTextView, questionTextView;
    private TextView countTextView;
    private Button nextButton;
    private AlertDialog.Builder builder;
    private SharedPreferences levelPrefs;

    private boolean chosenAnswer = false;
    private int questionsAttended = 0, questionsAnsweredCorrect = 0;
    private List<Question> listOfQuestions;
    private int questionsSetCompleted = 0;
    private String level;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        //initializing all the views
        initViews();

        //checking and updating nextButton status
        setNextButtonStatus(nextButton.isEnabled());

        if (savedInstanceState != null) {
            questionsAttended = savedInstanceState.getInt(CURRENT_INDEX_KEY);
            questionsAnsweredCorrect = savedInstanceState.getInt(CORRECT_ANSWERS_COUNT);
            Log.d("QuizActivity.class", "CURRENT INDEX " + questionsAttended + " is received in onCreate()");
            Log.d("QuizActivity.class", "CORRECT ANSWERS COUNT " + questionsAnsweredCorrect +
                    "is received in onCreate()");
        }

        Intent intent = getIntent();
        level = "";
        final Bundle loaderBundle = new Bundle();
        if (intent != null) {
            level = intent.getStringExtra("level");
            if (level != null)
                switch (level) {
                    case LEVEL_EASY:
                        levelPrefs = getSharedPreferences(
                                PREF_EASY, MODE_PRIVATE);
                        int easySetsCompleted = levelPrefs.getInt(PREF_EASY_SETS_COMPLETED, 0);
                        loaderBundle.putString(LEVEL_KEY, LEVEL_EASY);
                        questionsSetCompleted = easySetsCompleted;
                        Log.d("QuizActivity.class", "LoaderBundle is loaded with \"EASY\" Value");
                        break;
                    case LEVEL_INTERMEDIATE:
                         levelPrefs = getSharedPreferences(
                                PREF_EASY, MODE_PRIVATE);
                        int intermedSetsCompleted = levelPrefs.getInt(PREF_INTERMED_SETS_COMPLETED, 0);
                        loaderBundle.putString(LEVEL_KEY, LEVEL_INTERMEDIATE);
                        questionsSetCompleted = intermedSetsCompleted;
                        break;
                    case LEVEL_DIFFICULT:
                        levelPrefs = getSharedPreferences(
                                PREF_EASY, MODE_PRIVATE);
                        int diffSetsCompleted = levelPrefs.getInt(PREF_DIFF_SETS_COMPLETED, 0);
                        loaderBundle.putString(LEVEL_KEY, LEVEL_DIFFICULT);
                        questionsSetCompleted = diffSetsCompleted;
                        break;
                    default:
                        break;
                }
        }

        questionsSetCompleted = levelPrefs.getInt(PREF_SET_KEY,0);

        //below section is triggering the Loader to load data.
        final LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> quizFileLoader = loaderManager.getLoader(QUIZ_LOADER_ID);

        if (quizFileLoader == null) {
            loaderManager.initLoader(QUIZ_LOADER_ID, loaderBundle, this);
            Log.v("QuizActivity.class", "Initiated Loader");
        } else {
            loaderManager.restartLoader(QUIZ_LOADER_ID, loaderBundle, this);
            Log.v("QuizActivity.class", "Restarted Loader");
        }

        optionOneCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chosenAnswer = checkAndValidateAnswer(optionOneTextView.getText().toString(), questionsAttended);
                updateCardColors(optionOneCard);
            }
        });

        optionTwoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chosenAnswer = checkAndValidateAnswer(optionTwoTextView.getText().toString(), questionsAttended);
                updateCardColors(optionTwoCard);
            }
        });

        optionThreeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chosenAnswer = checkAndValidateAnswer(optionThreeTextView.getText().toString(), questionsAttended);
                updateCardColors(optionThreeCard);
            }
        });

        optionFourCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chosenAnswer = checkAndValidateAnswer(optionFourTextView.getText().toString(), questionsAttended);
                updateCardColors(optionFourCard);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chosenAnswer)
                    questionsAnsweredCorrect++;
                Toast.makeText(getApplicationContext(), "Answered Correct: " + questionsAnsweredCorrect, Toast.LENGTH_SHORT).show();
                questionsAttended++;
                if (questionsAttended == listOfQuestions.size()) {
                    questionsSetCompleted++;
                    builder.setTitle("Quiz completed!")
                            .setMessage("You have answered " + questionsAnsweredCorrect +
                                    " right! Congratulations!")
                            .setPositiveButton("Try new set", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startNewSet(loaderManager, loaderBundle);
                                }
                            })
                            .setNegativeButton("Thanks!", null)
                            .create().show();
                    SharedPreferences.Editor editor = levelPrefs.edit();
                    editor.putInt(PREF_SET_KEY,questionsSetCompleted);
                    editor.apply();
                    return;
                }
                updateCount();
                loadQuestionToCards(questionsAttended);
                setNextButtonStatus(false);
                resetCardColors();
            }
        });

    }

    private void startNewSet(LoaderManager loaderManager, Bundle savedInstanceState) {
        if(questionsSetCompleted==3)
        {
            Toast.makeText(getApplicationContext(),"All sets completed!",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, QuizHomeActivity.class));
            return;
        }
        resetCardColors();
        listOfQuestions = new ArrayList<>();
        questionsAttended = 0;
        questionsAnsweredCorrect = 0;
        questionsSetCompleted = levelPrefs.getInt(PREF_SET_KEY,0);
        loaderManager.restartLoader(QUIZ_LOADER_ID, savedInstanceState, QuizActivity.this);
    }

    private void updateCount() {
        String count = (questionsAttended+1) + "/" + listOfQuestions.size();
        countTextView.setText(count);
    }

    /**
     * method used to initialize all the views used in the layout
     */
    private void initViews() {
        optionOneCard = findViewById(R.id.qa_optionOneCardID);
        optionTwoCard = findViewById(R.id.qa_optionTwoCardID);
        optionThreeCard = findViewById(R.id.qa_optionThreeCardID);
        optionFourCard = findViewById(R.id.qa_optionFourCardID);
        optionOneTextView = findViewById(R.id.qa_optionOneID);
        optionTwoTextView = findViewById(R.id.qa_optionTwoID);
        optionThreeTextView = findViewById(R.id.qa_optionThreeID);
        optionFourTextView = findViewById(R.id.qa_optionFourID);
        questionTextView = findViewById(R.id.qa_questionID);
        nextButton = findViewById(R.id.qa_nextID);
        countTextView = findViewById(R.id.qa_countID);
        builder = new AlertDialog.Builder(this);
        listOfQuestions = new ArrayList<>();
    }


    /**
     * method used to enable / disable NEXT button and update it's look accordingly
     *
     * @param enabled - value passed and indicates if the nextButton is enabled or disabled
     */
    private void setNextButtonStatus(boolean enabled) {
        nextButton.setEnabled(enabled);
        if (!enabled)
            nextButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        else
            nextButton.getBackground().setColorFilter(null);
    }

    /**
     * The method is used to check the chosen answer and validate it. Resulting boolean will be returned
     *
     * @param optionSelected - selected answer
     * @return - returning boolean value
     */
    private boolean checkAndValidateAnswer(String optionSelected, int index) {

        boolean answeredCorrect = false;
        int solutionIndex = listOfQuestions.get(index).getSolution() - 1;
        String correctAnswer = listOfQuestions.get(index).getOptions()[solutionIndex];

        if (optionSelected.equals(correctAnswer))
            answeredCorrect = true;

        setNextButtonStatus(true);
        Log.v("QuizActivity.class", "Option Selected: " + optionSelected +
                "\nCorrect Answer: " + correctAnswer);
        return answeredCorrect;
    }

    /**
     * updating cards colors. Method should be called everytime user chooses an option
     *
     * @param cardView - cardView which user has chosen
     */
    private void updateCardColors(CardView cardView) {
        resetCardColors();
        cardView.setCardBackgroundColor(Color.YELLOW);
    }

    /**
     * this method resets all the optionCard's colors back to WHITE
     */
    private void resetCardColors() {
        optionOneCard.setCardBackgroundColor(Color.WHITE);
        optionTwoCard.setCardBackgroundColor(Color.WHITE);
        optionThreeCard.setCardBackgroundColor(Color.WHITE);
        optionFourCard.setCardBackgroundColor(Color.WHITE);
    }


    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<String> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<String>(this) {

            /**
             * Data will be loaded from the jsonFile in background and the extracted data will be returned as String
             * @return - string read from the file
             */
            @Override
            public String loadInBackground() {
                //String level = bundle.getString(LEVEL);
                try {
                    /*String level = bundle.getString(LEVEL_KEY);
                    String fileName = level + ".json";*/
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(getAssets().open("easy.json")));
                    String buffer;
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((buffer = bufferedReader.readLine()) != null)
                        stringBuilder.append(buffer);
                    Log.v("QuizActivity.class", "Returned Json String read from the file");
                    return stringBuilder.toString();
                } catch (IOException e) {
                    //Log.e("QuizActivity.class", "Error while reading " + bundle.getString(LEVEL_KEY) + " file from assets folder");
                    e.printStackTrace();
                    return null;
                }
            }

            /**
             * The method will be called before starting load data from the json file.
             * forceLoad() method is mandatory otherwise system may choose to ignore calling loadInBackground() method.
             */
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (bundle == null)
                    return;
                forceLoad();
            }
        };
    }

    /**
     * Once the data is loaded in the background, this method will be called.
     * Received String of data will be converted to JSON Objects and load them to the ArrayList<Question>
     * which will then be used for further questions
     *
     * @param loader     - loader received
     * @param jsonString - string loaded from jsonFile will be received as string. This has been handled
     *                   to create JSON Objects and load data to ArrayList.
     */
    @Override
    public void onLoadFinished(Loader<String> loader, String jsonString) {
        try {
            Log.v("QuizActivity.class", "Json Extracted: \n" + jsonString);
            JSONObject quizParentObject = new JSONObject(jsonString);
            //TODO(5) replace "Questions" by constant value
            JSONObject questionSets = quizParentObject.getJSONObject("Questions");
            Log.v("QuizActivity.class", "Extracted JSONObject for Questions");
            //TODO(6) need to replace 1 in below line of code by count stored in sharedPrefs
            JSONArray requiredSetOfQuestions = questionSets.getJSONArray("Questions set " + questionsSetCompleted);
            Log.v("QuizActivity.class", "Extracted question set");
            for (int i = 0; i < requiredSetOfQuestions.length(); i++) {
                String question = requiredSetOfQuestions.getJSONObject(i).getString("Question");
                Log.v("QuizActivity.class", "Question: " + question);
                JSONObject options = requiredSetOfQuestions.getJSONObject(i).getJSONObject("options");
                String option1 = options.getString("one");
                Log.v("QuizActivity.class", "Option One: " + option1);
                String option2 = options.getString("two");
                String option3 = options.getString("three");
                String option4 = options.getString("four");
                int solution = requiredSetOfQuestions.getJSONObject(i).getInt("solution");
                Question newQuestion = new Question(question, new String[]{
                        option1, option2, option3, option4}, solution);
                listOfQuestions.add(newQuestion);

                Log.v("QuizActivity.class", "Finished loading quiz data to ArrayList");
            }

            //setting first question values to cards textViews.
            loadQuestionToCards(questionsAttended);
            updateCount();

            Log.v("QuizActivity.class", "Finished setting first question on UI");

        } catch (JSONException e) {
            Log.e("QuizActivity.class", "Error parsing \n" + jsonString + "\n to JSON");
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "LoadingFinished", Toast.LENGTH_SHORT).show();
    }

    /**
     * The method will be used to load next question to cards
     *
     * @param i - index of the arrayList to load question from.
     */
    private void loadQuestionToCards(int i) {
        questionTextView.setText(listOfQuestions.get(i).getQuestion());
        optionOneTextView.setText(listOfQuestions.get(i).getOptions()[0]);
        optionTwoTextView.setText(listOfQuestions.get(i).getOptions()[1]);
        optionThreeTextView.setText(listOfQuestions.get(i).getOptions()[2]);
        optionFourTextView.setText(listOfQuestions.get(i).getOptions()[3]);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<String> loader) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt(CURRENT_INDEX_KEY, questionsAttended);
        int currentCorrectAnswersGiven = questionsAnsweredCorrect;
        outState.putInt(CORRECT_ANSWERS_COUNT, currentCorrectAnswersGiven);

        super.onSaveInstanceState(outState);
    }

}
