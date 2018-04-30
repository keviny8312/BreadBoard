package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class postQuizInfo extends AppCompatActivity {
    private String quizCode;
    public final String TAG = "pQuiz";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_quiz_info);

        quizCode = getIntent().getStringExtra("quizCode");

        final TextView title = findViewById(R.id.quizTitleField);
        final TextView dueDate = findViewById(R.id.quizDueDate);
        final TextView time = findViewById(R.id.quizTimeLimit);
        final TextView score = findViewById(R.id.quizScore);
        final TextView numQ = findViewById(R.id.quizNumQuestions);
        final TextView qClass = findViewById(R.id.quizClass);
        final Button answerKey = findViewById(R.id.answerKeyButton);
        Calendar currentDate = Calendar.getInstance();
        final int currentYear = currentDate.get(Calendar.YEAR);
        final int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        final int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);

        final Resources res = getResources();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        final DatabaseReference quizRef = database.getReference("quiz/" + quizCode);
        ValueEventListener getQuizInfo = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                title.setText(dataSnapshot.child("name").getValue().toString());
                dueDate.setText(res.getString(R.string.dueDate, dataSnapshot.child("due date").getValue().toString()));
                if(dataSnapshot.child("time").getValue().toString().equals("0")) time.setText(res.getString(R.string.timeLimitNone));
                else time.setText(res.getString(R.string.timeLimit, dataSnapshot.child("time").getValue().toString()));
                score.setText(res.getString(R.string.score, Float.valueOf(dataSnapshot.child("grades/" + user.getUid()).getValue().toString())));
                numQ.setText(res.getString(R.string.numQuestions, dataSnapshot.child("num questions").getValue().toString()));
                qClass.setText(res.getString(R.string.className, dataSnapshot.child("className").getValue().toString()));

                String ansDate = dataSnapshot.child("answer date").getValue(String.class);
                Log.d(TAG, "ans date " + ansDate + " index '-' " + ansDate.indexOf('-') + " lastindex '-' " + ansDate.lastIndexOf('-'));
                int answerYear = Integer.valueOf(ansDate.substring(ansDate.lastIndexOf('-') + 1));
                int answerMonth = Integer.valueOf(ansDate.substring(0, ansDate.indexOf('-')));
                int answerDay = Integer.valueOf(ansDate.substring(ansDate.indexOf('-') + 1, ansDate.lastIndexOf('-')));

                Log.d(TAG, "answer date " + answerMonth + " " + answerDay + " " + answerYear);
                Log.d(TAG, "current date " + currentMonth + " " + currentDay + " " + currentYear);

                if(answerYear > currentYear || (answerYear == currentYear && answerMonth > currentMonth) || (answerYear == currentYear && answerMonth == currentMonth && answerDay > currentDay)) {
                    answerKey.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        quizRef.addListenerForSingleValueEvent(getQuizInfo);
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.returnToDashButton) {
            Intent i = new Intent(postQuizInfo.this, StudentHome.class);
            startActivity(i);
        }
        if(v.getId() == R.id.answerKeyButton) {
            //TODO add field for teacher to set answer visibility, block as necessary
            Intent i = new Intent(postQuizInfo.this, AnswerKey.class);
            i.putExtra("quizCode", quizCode);
            startActivity(i);
            finish();
        }
    }
}
