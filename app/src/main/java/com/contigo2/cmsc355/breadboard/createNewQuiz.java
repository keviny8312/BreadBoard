package com.contigo2.cmsc355.breadboard;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class createNewQuiz extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_quiz);
    }

    public void onButtonClick(View v) {
        if (v.getId() == R.id.addQuestions) {

            //TODO back button (everywhere)
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

            //TODO now actually put the answer key time into database (and make them viewable)

        }
    }

    public boolean checkValidInput() {
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

    public boolean isNumeric(String s) {
        try {
            Integer.parseInt(s);
            return true;
        }
        catch(NumberFormatException nfe) {
            return false;
        }
    }

}
