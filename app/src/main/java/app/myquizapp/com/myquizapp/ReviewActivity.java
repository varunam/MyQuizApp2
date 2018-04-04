package app.myquizapp.com.myquizapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Random;

public class ReviewActivity extends AppCompatActivity {

    private static int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Intent intent = getIntent();
        ArrayList<String> listOfAnswers = intent.getStringArrayListExtra("chosenAnswerslist");
        score = intent.getIntExtra("score",0);

        RecyclerView recyclerView = findViewById(R.id.recyclerID);
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new RecyclerViewAdapter(listOfAnswers));

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder builder= new AlertDialog.Builder(this);

        builder.setTitle("Would you like to try it again?");
        if(score>50)
            builder.setMessage("Alternatively, you can also choose to start a new set.");

        final AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);


        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("action","exit");
                intent.putExtra("score",score);
                setResult(2,intent);
                finish();
            }
        });


        if(score>50)
            builder.setNeutralButton("Try new set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("action","tryNext");
                setResult(2,intent);
                finish();
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("action","tryAgain");
                setResult(2,intent);
                finish();
            }
        }).show();
    }
}
