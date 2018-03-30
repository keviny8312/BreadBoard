package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class addQuestions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_questions);
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.finishQuiz) {
            Intent i = new Intent(addQuestions.this, quizConfirmation.class);
            startActivity(i);
        }

        if(v.getId() == R.id.nextQuestion) {
            Intent i = new Intent(addQuestions.this, addQuestions.class);
            startActivity(i);
        }
    }
}
