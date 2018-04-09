package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class modifyQuiz extends AppCompatActivity {
    private String quizCode, name, dueDate, qClass, time, ansDate, active;
    private EditText ETtitle, ETdate, ETclass, ETtime, ETans;
    private ToggleButton TBactive;
    private RadioButton RBimmediateAns, RBnewDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_quiz);

        quizCode = getIntent().getStringExtra("quizCode");

        name = getIntent().getStringExtra("name");
        dueDate = getIntent().getStringExtra("dueDate");
        qClass = getIntent().getStringExtra("class");
        time = getIntent().getStringExtra("time");
        ansDate = getIntent().getStringExtra("ansDate");
        active = getIntent().getStringExtra("active");

        ETtitle = findViewById(R.id.quizTitleField);
        ETdate = findViewById(R.id.quizDueDateField);
        ETclass = findViewById(R.id.quizClassName);
        ETtime = findViewById(R.id.quizTimeLimit);
        ETans = findViewById(R.id.modifyAnsDateField);
        TBactive = findViewById(R.id.activeButton);
        RBimmediateAns = findViewById(R.id.immediateAns);
        RBnewDate = findViewById(R.id.newAnsDate);

        ETtitle.setText(name);
        ETdate.setText(dueDate);
        ETclass.setText(qClass);
        if(!time.equals("0")) ETtime.setText(time);

        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        Date currentDay = new Date();
        if(ansDate.equals(formatter.format(currentDay))) RBimmediateAns.setChecked(true);
        else {
            RBnewDate.setChecked(true);
            ETans.setText(ansDate);
        }
        if(active.equals("true")) TBactive.setChecked(true);
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.submitChangesBTNModifyQuiz) {
            if(!checkValidInput()) return;

            name = ETtitle.getText().toString();
            dueDate = ETdate.getText().toString();
            qClass = ETclass.getText().toString();
            if(ETtime.getText().toString().isEmpty()) time = "0";
            else time = ETtime.getText().toString();

            if(RBnewDate.isChecked()) ansDate = ETans.getText().toString();
            else {
                SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
                Date currentDay = new Date();
                ansDate = formatter.format(currentDay);
            }
            active = String.valueOf(TBactive.isChecked());

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            DatabaseReference getQuizInfo = database.getReference("quiz/" + quizCode);

            Map<String, Object> modifications = new HashMap<>();
            modifications.put("name", name);
            modifications.put("due date", dueDate);
            modifications.put("className", qClass);
            modifications.put("time", time);
            modifications.put("active", active);
            modifications.put("answer date", ansDate);

            getQuizInfo.updateChildren(modifications);

            getQuizInfo = database.getReference("users/" + user.getUid() + "/quizzes/");
            modifications.clear();
            modifications.put(quizCode, name);
            getQuizInfo.updateChildren(modifications);

            Intent i = new Intent(modifyQuiz.this, QuizInformation.class);
            i.putExtra("quizCode", quizCode);
            startActivity(i);
        }

        if(v.getId() == R.id.modifyQuestions) {
            Intent i = new Intent(modifyQuiz.this, modifyQuestions.class);
            i.putExtra("quizCode", quizCode);
            i.putExtra("questionNum", 0);
            startActivity(i);
            finish();
        }
    }

    public boolean checkValidInput() {
        if(ETtitle.getText().toString().isEmpty()) {
            Toast.makeText(modifyQuiz.this, "Please name the quiz.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(ETdate.getText().toString().isEmpty()) {
            Toast.makeText(modifyQuiz.this, "Please enter a due date.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!ETdate.getText().toString().matches("([0-9]{2})-([0-9]{2})-([0-9]{4})")) {
            Toast.makeText(modifyQuiz.this, "Please enter a valid due date.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(ETclass.getText().toString().isEmpty()) {
            Toast.makeText(modifyQuiz.this, "Please enter the class for this quiz.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!ETtime.getText().toString().isEmpty() && !isNumeric(ETtime.getText().toString())) {
            Toast.makeText(modifyQuiz.this, "Please enter a valid time limit.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!RBimmediateAns.isChecked() && !RBnewDate.isChecked()) {
            Toast.makeText(modifyQuiz.this, "Please choose when to show the answers.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(RBnewDate.isChecked() && ETans.getText().toString().isEmpty()) {
            Toast.makeText(modifyQuiz.this, "Please choose a date to show the answers.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(RBnewDate.isChecked() && !ETans.getText().toString().matches("([0-9]{2})-([0-9]{2})-([0-9]{4})")) {
            Toast.makeText(modifyQuiz.this, "Please enter a valid date to show the answers.", Toast.LENGTH_SHORT).show();
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
