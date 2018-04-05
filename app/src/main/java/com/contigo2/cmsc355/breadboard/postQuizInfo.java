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

public class postQuizInfo extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_quiz_info);

        final String quizCode = getIntent().getStringExtra("quizCode");

        final TextView title = findViewById(R.id.quizTitleField);
        final TextView dueDate = findViewById(R.id.quizDueDate);
        final TextView time = findViewById(R.id.quizTimeLimit);
        final TextView score = findViewById(R.id.quizScore);
        final TextView numQ = findViewById(R.id.quizNumQuestions);
        final TextView qClass = findViewById(R.id.quizClass);

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
                time.setText(res.getString(R.string.timeLimit, dataSnapshot.child("time").getValue().toString()));
                score.setText(res.getString(R.string.score, Float.valueOf(dataSnapshot.child("grades/" + user.getUid()).getValue().toString())));
                numQ.setText(res.getString(R.string.numQuestions, dataSnapshot.child("num questions").getValue().toString()));
                qClass.setText(res.getString(R.string.className, dataSnapshot.child("className").getValue().toString()));
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
        }
    }
}
