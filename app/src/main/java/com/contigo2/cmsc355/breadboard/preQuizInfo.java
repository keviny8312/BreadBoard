package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class preQuizInfo extends AppCompatActivity {
    String quizCode, name, dueDate, time, numQ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_quiz_info);

        quizCode = getIntent().getStringExtra("quizCode");

        final TextView TVname = findViewById(R.id.quizTitleField);
        final TextView TVdueDate = findViewById(R.id.quizDueDate);
        final TextView TVtime = findViewById(R.id.quizTimeLimit);
        final TextView TVnumQ = findViewById(R.id.quizNumQuestions);
        final Resources res = getResources();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference getQuizInfo = database.getReference("quiz/" + quizCode);

        ValueEventListener listenQuizInfo = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot codeSnapshot: dataSnapshot.getChildren()) {
                    if(codeSnapshot.getKey().equals("code"));
                    if(codeSnapshot.getKey().equals("due date")) {
                        dueDate = codeSnapshot.getValue(String.class);
                        TVdueDate.setText(res.getString(R.string.dueDate, dueDate));
                    }
                    if(codeSnapshot.getKey().equals("name")) {
                        name = codeSnapshot.getValue(String.class);
                        TVname.setText(res.getString(R.string.quizName, name));
                    }
                    if(codeSnapshot.getKey().equals("num questions")) {
                        numQ = codeSnapshot.getValue(String.class);
                        TVnumQ.setText(res.getString(R.string.numQuestions, numQ));
                    }
                    if(codeSnapshot.getKey().equals("className")) {
                        //qClass = codeSnapshot.getValue(String.class);
                        //TVclass.setText(res.getString(R.string.className, qClass));
                    }
                    if(codeSnapshot.getKey().equals("time")) {
                        time = codeSnapshot.getValue(String.class);
                        TVtime.setText(res.getString(R.string.timeLimit, time));
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        getQuizInfo.addListenerForSingleValueEvent(listenQuizInfo);
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.takeQuizButton) {
            Intent i = new Intent(preQuizInfo.this, exampleQuiz.class);
            i.putExtra("quizCode", quizCode);
            i.putExtra("questionNum", 0);
            startActivity(i);
        }
    }
}
