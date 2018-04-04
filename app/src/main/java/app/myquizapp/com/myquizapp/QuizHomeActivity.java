package app.myquizapp.com.myquizapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class QuizHomeActivity extends AppCompatActivity {

    private CardView easyCard, intermedCard, diffCard;
    private ProgressBar easyProgressBar, intermedProgressBar, diffProgressBar;
    private TextView easyProgressTextView, intermedProgressTextView, diffProgressTextView;
    private SharedPreferences levelPrefs;

    private int easySetsCompleted = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_home);

        easyCard = findViewById(R.id.easyCardID);
        intermedCard = findViewById(R.id.intermedCardID);
        diffCard = findViewById(R.id.diffCardID);
        easyProgressBar = findViewById(R.id.easyProgressBarID);
        easyProgressTextView = findViewById(R.id.easyProgressTextID);
        intermedProgressBar = findViewById(R.id.intermedProgressBarID);
        intermedProgressTextView = findViewById(R.id.intermedProgressTextID);
        diffProgressBar = findViewById(R.id.diffProgressBarID);
        diffProgressTextView = findViewById(R.id.diffProgressTextID);

        loadProgress();

        easyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (easySetsCompleted == 3) {
                    Toast.makeText(getApplicationContext(), "All sets completed! Clearing it and taking you inside", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = levelPrefs.edit();
                    editor.putInt("set-key", 0);
                    editor.apply();
                }
                Intent intent = new Intent(QuizHomeActivity.this, QuizActivity.class);
                intent.putExtra("level", "easy");
                startActivity(intent);
            }
        });

        /*intermediate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizHomeActivity.this, QuizActivity.class);
                intent.putExtra("level","intermediate");
                startActivity(intent);
            }
        });

        difficult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizHomeActivity.this, QuizActivity.class);
                intent.putExtra("level","difficult");
                startActivity(intent);
            }
        });*/
    }

    private void loadProgress() {
        levelPrefs = getSharedPreferences("pref-level-easy", MODE_PRIVATE);
        final int setsCompleted = levelPrefs.getInt("set-key", 0);
        easySetsCompleted = setsCompleted;
        int numberOfSets = 3;
        int percentagePerQuiz = 100 / numberOfSets;
        int quizProgress = percentagePerQuiz * setsCompleted;
        if (setsCompleted == numberOfSets) {
            easyProgressBar.setProgress(100);
            easyProgressTextView.setText("100% completed");
        } else {
            easyProgressBar.setProgress(quizProgress);
            easyProgressTextView.setText(quizProgress + "% completed");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadProgress();
    }
}
