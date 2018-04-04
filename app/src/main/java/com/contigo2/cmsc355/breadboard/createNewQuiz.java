package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class createNewQuiz extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_quiz);
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.addQuestions) {

            //TODO check for repeat quiz names

            //TODO back button (everywhere)

            EditText qn = findViewById(R.id.quizTitleFieldCNQ);
            EditText qdd = findViewById(R.id.quizDueDateCNQ);
            String quizName = qn.getText().toString();
            String quizDueDate = qdd.getText().toString();

            Intent i = new Intent(createNewQuiz.this, addQuestions.class);
            i.putExtra("passQuiz", new Quiz(quizName, quizDueDate));

            startActivity(i);
        }

    }
}
