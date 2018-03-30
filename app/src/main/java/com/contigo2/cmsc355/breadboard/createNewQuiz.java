package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class createNewQuiz extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_quiz);
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.addQuestions) {
            Intent i = new Intent(createNewQuiz.this, addQuestions.class);

            // might not need any of this, if the data is pushed into database immediately
            // need to make sure to remove unfinished quizzes if pushed here, if they quit
            EditText qn = findViewById(R.id.quizTitleFieldCNQ);
            EditText qdd = findViewById(R.id.quizDueDateCNQ);
            String quizName = qn.getText().toString();
            String quizDueDate = qdd.getText().toString();
            i.putExtra("quizName", quizName);
            i.putExtra("quizDueDate", quizDueDate);

            startActivity(i);
        }

    }
}
