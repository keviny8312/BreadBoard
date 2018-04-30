package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class createNewQuiz extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_quiz);
    }

    public void onButtonClick(View v) {
        if (v.getId() == R.id.addQuestions) {                       // create quiz with current data, then go to add questions

            EditText name = findViewById(R.id.quizTitleFieldCNQ);
            EditText dueDate = findViewById(R.id.quizDueDateCNQ);
            EditText qClass = findViewById(R.id.quizClassCNQ);
            EditText time = findViewById(R.id.quizTimeLimitCNQ);
            RadioButton immediate = findViewById(R.id.answersImmediately);
            EditText delayedAnswerDate = findViewById(R.id.delayedAnswersDate);
            ToggleButton activeState = findViewById(R.id.activeSwitch);

            String quizName = name.getText().toString();
            String quizDueDate = dueDate.getText().toString();
            String quizClass = qClass.getText().toString();
            String quizTime = time.getText().toString();
            String quizAnsDate = delayedAnswerDate.getText().toString();
            String active = String.valueOf(activeState.isChecked());

            if(checkValidInput()) {
                if(immediate.isChecked()) {
                    SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
                    Date currentDay = new Date();
                    quizAnsDate = formatter.format(currentDay);
                }
                if(quizTime.isEmpty()) quizTime = "0";

                Intent i = new Intent(createNewQuiz.this, addQuestions.class);
                i.putExtra("passQuiz", new Quiz(quizName, quizDueDate, quizClass, quizTime, quizAnsDate, active));

                startActivity(i);
            }
        }
    }

    public boolean checkValidInput() {                          // check for valid input fields
        EditText name = findViewById(R.id.quizTitleFieldCNQ);
        EditText dueDate = findViewById(R.id.quizDueDateCNQ);
        EditText qClass = findViewById(R.id.quizClassCNQ);
        EditText time = findViewById(R.id.quizTimeLimitCNQ);
        EditText delayDate = findViewById(R.id.delayedAnswersDate);
        RadioButton immediate = findViewById(R.id.answersImmediately);
        RadioButton delayed = findViewById(R.id.delayedAnswers);

        if(name.getText().toString().isEmpty()) {
            Toast.makeText(createNewQuiz.this, "Please name the quiz.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(dueDate.getText().toString().isEmpty()) {
            Toast.makeText(createNewQuiz.this, "Please enter a due date.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!dueDate.getText().toString().matches("([0-9]{2})-([0-9]{2})-([0-9]{4})")) {
            Toast.makeText(createNewQuiz.this, "Please enter a valid due date.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(qClass.getText().toString().isEmpty()) {
            Toast.makeText(createNewQuiz.this, "Please enter the class for this quiz.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!time.getText().toString().isEmpty() && !isNumeric(time.getText().toString())) {
            Toast.makeText(createNewQuiz.this, "Please enter a valid time limit.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!immediate.isChecked() && !delayed.isChecked()) {
            Toast.makeText(createNewQuiz.this, "Please choose when to show the answers.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(delayed.isChecked() && delayDate.getText().toString().isEmpty()) {
            Toast.makeText(createNewQuiz.this, "Please choose a date to show the answers.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(delayed.isChecked() && !delayDate.getText().toString().matches("([0-9]{2})-([0-9]{2})-([0-9]{4})")) {
            Toast.makeText(createNewQuiz.this, "Please enter a valid date to show the answers.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public boolean isNumeric(String s) {    // check if string is a number
        try {
            Integer.parseInt(s);
            return true;
        }
        catch(NumberFormatException nfe) {
            return false;
        }
    }

}
