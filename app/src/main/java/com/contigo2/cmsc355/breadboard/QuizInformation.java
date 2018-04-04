package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QuizInformation extends AppCompatActivity {
    private String quizCode, name = "", time = "", numQ = "", qClass = "", dueDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_information);
        quizCode = getIntent().getStringExtra("quizCode");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference getQuizInfo = database.getReference("quiz/" + quizCode);

        ValueEventListener listenQuizInfo = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot codeSnapshot: dataSnapshot.getChildren()) {
                    if(codeSnapshot.getKey().equals("code")) continue;
                    if(codeSnapshot.getKey().equals("due date")) dueDate = R.string.dueDate + codeSnapshot.getValue(String.class);
                    if(codeSnapshot.getKey().equals("name")) name = R.string.quizName + codeSnapshot.getValue(String.class);
                    if(codeSnapshot.getKey().equals("num questions")) numQ = R.string.numQuestions + codeSnapshot.getValue(String.class);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // fndbsgyivfeksh
            }
        };
        getQuizInfo.addListenerForSingleValueEvent(listenQuizInfo);

        TextView TVname  = findViewById(R.id.QuizTitleField);
        TextView TVdueD  = findViewById(R.id.quizDueDateField);
        TextView TVnumQ  = findViewById(R.id.quizNumQuestionsField);
        TextView TVclass = findViewById(R.id.quizClassField);

        TVname.setText(name);
        TVdueD.setText(dueDate);
        TVnumQ.setText(numQ);
        TVclass.setText(qClass);

    }
}
