package app.myquizapp.com.myquizapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class QuizHomeActivity extends AppCompatActivity {

    private Button easy, intermediate, difficult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_home);

        easy = findViewById(R.id.easyButtonID);
        intermediate = findViewById(R.id.interMedID);
        difficult = findViewById(R.id.diffButtonID);

        easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizHomeActivity.this, QuizActivity.class);
                intent.putExtra("level","easy");
                startActivity(intent);
            }
        });

        intermediate.setOnClickListener(new View.OnClickListener() {
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
        });
    }
}