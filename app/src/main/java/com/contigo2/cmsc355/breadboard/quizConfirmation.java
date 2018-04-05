package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class quizConfirmation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_confirmation);
        Quiz quiz = (Quiz)getIntent().getSerializableExtra("passQuiz");

        TextView code = findViewById(R.id.quizConfirmationField);
        code.setText(quiz.getCode());
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.returnToDashboard) {
            Intent i = new Intent(quizConfirmation.this, TeacherHome.class);
            startActivity(i);
        }
    }
}
