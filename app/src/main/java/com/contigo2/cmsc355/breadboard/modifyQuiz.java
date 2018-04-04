package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class modifyQuiz extends AppCompatActivity {
    private String quizCode, name, dueDate, qClass, time;
    private EditText ETtitle, ETdate, ETclass, ETtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_quiz);

        quizCode = getIntent().getStringExtra("quizCode");

        name = getIntent().getStringExtra("name");
        dueDate = getIntent().getStringExtra("dueDate");
        qClass = getIntent().getStringExtra("class");
        time = getIntent().getStringExtra("time");

        ETtitle = findViewById(R.id.quizTitleField);
        ETdate = findViewById(R.id.quizDueDateField);
        ETclass = findViewById(R.id.quizClassName);
        ETtime = findViewById(R.id.quizTimeLimit);

        ETtitle.setText(name);
        ETdate.setText(dueDate);
        ETclass.setText(qClass);
        ETtime.setText(time);
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.submitChangesBTNModifyQuiz) {
            name = ETtitle.getText().toString();
            dueDate = ETdate.getText().toString();
            qClass = ETclass.getText().toString();
            time = ETtime.getText().toString();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            DatabaseReference getQuizInfo = database.getReference("quiz/" + quizCode);

            Map<String, Object> modifications = new HashMap<>();
            modifications.put("name", name);
            modifications.put("due date", dueDate);
            modifications.put("className", qClass);
            modifications.put("time", time);

            getQuizInfo.updateChildren(modifications);

            getQuizInfo = database.getReference("users/" + user.getUid() + "/quizzes/");
            modifications.clear();
            modifications.put(quizCode, name);
            getQuizInfo.updateChildren(modifications);

            Intent i = new Intent(modifyQuiz.this, QuizInformation.class);
            i.putExtra("quizCode", quizCode);
            startActivity(i);
        }
    }
}
