package app.myquizapp.com.myquizapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
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

    private static final int WRITE_STORAGE_PERMISSION_GRANTED = 100, READ_STORAGE_PERMISSION_GRANTED = 101;
    protected static List<Question> listOfQuestions;
    private static ArrayList<String> listOfAnswers;
    final Bundle loaderBundle = new Bundle();
    final LoaderManager loaderManager = getSupportLoaderManager();
    private CardView optionOneCard, optionTwoCard, optionThreeCard, optionFourCard;
    private TextView optionOneTextView, optionTwoTextView, optionThreeTextView, optionFourTextView, questionTextView;
    private TextView countTextView;
    private Button nextButton;
    private AlertDialog.Builder builder;
    private SharedPreferences levelPrefs;
    private boolean chosenAnswer = false;
    private int questionsAttended = 0, questionsAnsweredCorrect = 0;
    private int questionsSetCompleted = 0;
    private String level;

    public static Bitmap loadLargeBitmapFromView(View v) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(spec,spec);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);

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

        questionsSetCompleted = levelPrefs.getInt(PREF_SET_KEY, 0);

        //below section is triggering the Loader to load data.

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
                listOfAnswers.add(questionsAttended, optionOneTextView.getText().toString());
                updateCardColors(optionOneCard);
            }
        });

        optionTwoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chosenAnswer = checkAndValidateAnswer(optionTwoTextView.getText().toString(), questionsAttended);
                updateCardColors(optionTwoCard);
                listOfAnswers.add(questionsAttended, optionTwoTextView.getText().toString());
            }
        });

        optionThreeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chosenAnswer = checkAndValidateAnswer(optionThreeTextView.getText().toString(), questionsAttended);
                listOfAnswers.add(questionsAttended, optionThreeTextView.getText().toString());
                updateCardColors(optionThreeCard);
            }
        });

        optionFourCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chosenAnswer = checkAndValidateAnswer(optionFourTextView.getText().toString(), questionsAttended);
                listOfAnswers.add(questionsAttended, optionFourTextView.getText().toString());
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
                    showResultDialog((questionsAnsweredCorrect * 100) / listOfQuestions.size());
                    return;
                }
                updateCount();
                loadQuestionToCards(questionsAttended);
                setNextButtonStatus(false);
                resetCardColors();
            }
        });

    }

    private void showResultDialog(final int score) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_quiz_result, null);
        TextView resultHeader = view.findViewById(R.id.resultHeaderID);
        TextView result = view.findViewById(R.id.resultTextID);
        ImageView tryAgain = view.findViewById(R.id.quizAgainID);
        ImageView tryNext = view.findViewById(R.id.quizNextID);
        Button exitQuiz = view.findViewById(R.id.quizExitID);
        ImageView shareQuiz = view.findViewById(R.id.quizShareID);
        ImageView reviewQuiz = view.findViewById(R.id.reviewQuizID);
        ContentLoadingProgressBar resultProgressBar = view.findViewById(R.id.resultprogressBarID);

        result.setText(score + "%");
        resultProgressBar.setIndeterminate(false);
        resultProgressBar.setMax(100);
        resultProgressBar.setProgress(score);

        if (score <= 50) {
            resultHeader.setText("Too bad..Please try again");
            tryNext.setVisibility(View.GONE);
            tryAgain.setVisibility(View.VISIBLE);
        } else if (score > 50 && score < 80) {
            resultHeader.setText("Not bad! You can do better....");
            tryNext.setVisibility(View.VISIBLE);
            tryAgain.setVisibility(View.VISIBLE);
        } else if (score >= 80 && score != 100) {
            resultHeader.setText("Well Done! Almost there...");
            tryNext.setVisibility(View.VISIBLE);
            tryAgain.setVisibility(View.VISIBLE);
        } else {
            resultHeader.setText("You aced it!!..");
            tryNext.setVisibility(View.VISIBLE);
            tryAgain.setVisibility(View.GONE);
            reviewQuiz.setVisibility(View.GONE);
        }
        Toast.makeText(view.getContext(), questionsSetCompleted + "", Toast.LENGTH_SHORT).show();
        if (questionsSetCompleted == 2) {
            if (score == 100) {
                tryAgain.setVisibility(View.GONE);
                tryNext.setVisibility(View.GONE);
            } else
                tryNext.setVisibility(View.GONE);
        }
        final AlertDialog alertDialog;
        builder.setView(view).setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();

        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = levelPrefs.edit();
                editor.putInt(PREF_SET_KEY, questionsSetCompleted);
                editor.apply();
                setNextButtonStatus(false);
                alertDialog.dismiss();
                startNewSet(loaderManager, loaderBundle);


            }
        });
        tryNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionsSetCompleted++;
                SharedPreferences.Editor editor = levelPrefs.edit();
                editor.putInt(PREF_SET_KEY, questionsSetCompleted);
                editor.apply();
                setNextButtonStatus(false);
                Toast.makeText(view.getContext(), questionsSetCompleted + "", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
                startNewSet(loaderManager, loaderBundle);
            }
        });
        exitQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questionsSetCompleted++;
                alertDialog.dismiss();
                SharedPreferences.Editor editor = levelPrefs.edit();
                editor.putInt(PREF_SET_KEY, questionsSetCompleted);
                editor.apply();
                setNextButtonStatus(false);
                Intent i = new Intent(view.getContext(), QuizHomeActivity.class);
                startActivity(i);
                finish();
            }
        });
        reviewQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                int marks = (questionsAnsweredCorrect * 100) / listOfQuestions.size();
                setNextButtonStatus(false);
                Intent i = new Intent(QuizActivity.this, ReviewActivity.class);
                i.putStringArrayListExtra("chosenAnswerslist", listOfAnswers);
                i.putExtra("score", marks);
                startActivityForResult(i, 2);
            }
        });

        shareQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareResult((questionsAnsweredCorrect * 100) / listOfQuestions.size());
            }
        });

    }

    private void shareResult(int result) {
        View certificateView = getLayoutInflater().inflate(R.layout.layout_certificate_linearlayout, null);
        TextView userNameTextView = certificateView.findViewById(R.id.userNameTextView);
        String userName = getString(R.string.user_name, "Mr");
        userNameTextView.setText(userName);
        String certBody = getString(R.string.certificateText, "EASY", result + "%");
        TextView certBodyTextView = certificateView.findViewById(R.id.certificateBodyTextView);
        certBodyTextView.setText(certBody);
        Bitmap screenShotBitMap = getScreenShot(certificateView);
        String currentTimeStamp = (String) DateFormat.format("MMddyyhhmmss", new Date().getTime());
        storeAndShare(screenShotBitMap, "LearnKannadaSmartapp_" + currentTimeStamp + ".jpg", getString(R.string.resultShareBody));
    }

    private void startNewSet(LoaderManager loaderManager, Bundle savedInstanceState) {
        resetCardColors();
        listOfQuestions = new ArrayList<>();
        questionsAttended = 0;
        questionsAnsweredCorrect = 0;
        questionsSetCompleted = levelPrefs.getInt(PREF_SET_KEY, 0);
        loaderManager.restartLoader(QUIZ_LOADER_ID, savedInstanceState, QuizActivity.this);
    }

    private void updateCount() {
        String count = (questionsAttended + 1) + "/" + listOfQuestions.size();
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
            listOfAnswers = new ArrayList<>(listOfQuestions.size());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int score = data.getIntExtra("score", 0);

        if (requestCode == 2 && resultCode == 2) {

            String action = data.getStringExtra("action");

            if (action.equalsIgnoreCase("exit")) {
                if (score > 50)
                    questionsSetCompleted++;
                SharedPreferences.Editor editor = levelPrefs.edit();
                editor.putInt(PREF_SET_KEY, questionsSetCompleted);
                editor.apply();
                setNextButtonStatus(false);
                Intent i = new Intent(QuizActivity.this, QuizHomeActivity.class);
                startActivity(i);
                finish();
            } else if (action.equalsIgnoreCase("tryAgain")) {
                SharedPreferences.Editor editor = levelPrefs.edit();
                editor.putInt(PREF_SET_KEY, questionsSetCompleted);
                editor.apply();
                setNextButtonStatus(false);
                startNewSet(loaderManager, loaderBundle);
            } else if (action.equalsIgnoreCase("tryNext")) {
                if (questionsSetCompleted == 2) {
                    questionsSetCompleted++;
                    Toast.makeText(this, "You have completed all the sets.", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = levelPrefs.edit();
                    editor.putInt(PREF_SET_KEY, questionsSetCompleted);
                    editor.apply();
                    setNextButtonStatus(false);
                    Intent i = new Intent(QuizActivity.this, QuizHomeActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    questionsSetCompleted++;
                    SharedPreferences.Editor editor = levelPrefs.edit();
                    editor.putInt(PREF_SET_KEY, questionsSetCompleted);
                    editor.apply();
                    setNextButtonStatus(false);
                    startNewSet(loaderManager, loaderBundle);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_quiz_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.helpID) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                } else
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_PERMISSION_GRANTED);

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                } else
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_GRANTED);
            } else
                shareScreenShot();
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareScreenShot() {
        Toast.makeText(this, "shareScreenShot()", Toast.LENGTH_SHORT).show();
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        Bitmap screenShotBitMap = getScreenShot(rootView);
        String currentTimeStamp = (String) DateFormat.format("MMddyyhhmmss", new Date().getTime());
        storeAndShare(screenShotBitMap, "LearnKannadaSmartapp_" + currentTimeStamp + ".jpg", getString(R.string.helpShareBody));
    }

    private Bitmap getScreenShot(View view) {
        Log.d("QuizActivity.class", "getScreenShot()");
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        screenView.buildDrawingCache();
        Bitmap bitmap;
        if (screenView.getDrawingCache() != null)
            bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        else
            bitmap = loadLargeBitmapFromView(screenView);
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public void storeAndShare(Bitmap bm, String fileName, String message) {
        Log.d("QuizActivity.class", "storeAndShare()");
        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/QuizAppScreenshots";
        File dir = new File(dirPath);
        if (!dir.exists())
            dir.mkdirs();
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
            shareImage(file, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shareImage(File file, String message) {
        Log.d("QuizActivity.class", "shareImage()");
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share Screenshot Title");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, message);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            startActivity(Intent.createChooser(intent, "Share Screenshot"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(QuizActivity.this, "No App Available", Toast.LENGTH_SHORT).show();
        }
    }
}
