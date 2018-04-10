package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QuizInformation extends AppCompatActivity {
    private String quizCode = "", name = "", time = "", numQ = "", qClass = "", dueDate = "",
                   ansDate = "", active = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_information);
        quizCode = getIntent().getStringExtra("quizCode");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference getQuizInfo = database.getReference("quiz/" + quizCode);

        final TextView TVname  = findViewById(R.id.QuizTitleField);
        final TextView TVdueD  = findViewById(R.id.quizDueDateField);
        final TextView TVnumQ  = findViewById(R.id.quizNumQuestionsField);
        final TextView TVclass = findViewById(R.id.quizClassField);
        final TextView TVtime  = findViewById(R.id.quizTimeLimitField);

        final Resources res = getResources();

        ValueEventListener listenQuizInfo = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot codeSnapshot: dataSnapshot.getChildren()) {
                    if(codeSnapshot.getKey().equals("code"));
                    if(codeSnapshot.getKey().equals("due date")) {
                        dueDate = codeSnapshot.getValue(String.class);
                        TVdueD.setText(res.getString(R.string.dueDate, dueDate));
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
                        qClass = codeSnapshot.getValue(String.class);
                        TVclass.setText(res.getString(R.string.className, qClass));
                    }
                    if(codeSnapshot.getKey().equals("time")) {
                        time = codeSnapshot.getValue(String.class);
                        if(time.equals("0")) TVtime.setText(res.getString(R.string.timeLimitNone));
                        else TVtime.setText(res.getString(R.string.timeLimit, time));
                    }
                    if(codeSnapshot.getKey().equals("answer date")) {
                        ansDate = codeSnapshot.getValue(String.class);
                    }
                    if(codeSnapshot.getKey().equals("active")) {
                        active = codeSnapshot.getValue(String.class);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        getQuizInfo.addListenerForSingleValueEvent(listenQuizInfo);

    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.statsBTN) {
            Intent i = new Intent(QuizInformation.this, quizStatistics.class);
            i.putExtra("quizCode", quizCode);
            startActivity(i);
        }
        if(v.getId() == R.id.gradesBTN) {
            Intent i = new Intent(QuizInformation.this, studentGrades.class);
            i.putExtra("quizCode", quizCode);
            startActivity(i);
        }
        if(v.getId() == R.id.modifyQuizBTN) {
            Intent i = new Intent(QuizInformation.this, modifyQuiz.class);
            i.putExtra("quizCode", quizCode);
            i.putExtra("name", name);
            i.putExtra("dueDate", dueDate);
            i.putExtra("class", qClass);
            i.putExtra("time", time);
            i.putExtra("ansDate", ansDate);
            i.putExtra("active", active);
            startActivity(i);
        }
        if(v.getId() == R.id.deleteQuizBTN) {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.getReference("quiz/" + quizCode).removeValue();

            DatabaseReference getQuizzes = database.getReference();
            ValueEventListener deleteQuizzes = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot codeSnapshot: dataSnapshot.child("users/").getChildren()) {
                        database.getReference("users/" + codeSnapshot.getKey() + "/finished/" + quizCode).removeValue();
                        database.getReference("users/" + codeSnapshot.getKey() + "/quizzes/" + quizCode).removeValue();
                        database.getReference("users/" + codeSnapshot.getKey() + "/answers/" + quizCode).removeValue();
                        database.getReference("users/" + codeSnapshot.getKey() + "/confCodes/" + quizCode).removeValue();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            getQuizzes.addListenerForSingleValueEvent(deleteQuizzes);

            Intent i = new Intent(QuizInformation.this, TeacherHome.class);
            startActivity(i);
        }
    }
}
