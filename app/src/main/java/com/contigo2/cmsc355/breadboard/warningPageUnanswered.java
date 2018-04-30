package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class warningPageUnanswered extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {        // deprecated
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning_unanswered);
    }
    public void onButtonClick(View view){
        if(view.getId() == R.id.warningContinue){
            Intent i = new Intent(warningPageUnanswered.this, exampleQuiz.class);
            i.putExtra("quizCode", getIntent().getStringExtra("quizCode"));
            i.putExtra("questionNum", getIntent().getStringExtra("questionNum"));
            startActivity(i);
            finish();
        }
    }

}
