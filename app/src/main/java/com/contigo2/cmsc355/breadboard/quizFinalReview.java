package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class quizFinalReview extends AppCompatActivity {

    private String quizCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_final_review);
        quizCode = getIntent().getStringExtra("quizCode");
        final TextView q1 = findViewById(R.id.question1);
        final TextView q2 = findViewById(R.id.question2);
        final TextView q3 = findViewById(R.id.question3);
        final TextView q4 = findViewById(R.id.question4);
        final TextView qst[] = {q1, q2, q3, q4};

        final TextView a1 = findViewById(R.id.Answer1);
        final TextView a2 = findViewById(R.id.Answer2);
        final TextView a3 = findViewById(R.id.Answer3);
        final TextView a4 = findViewById(R.id.Answer4);
        final TextView ans[] = {a1, a2, a3, a4};

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference ansRef = database.getReference("users/" + user.getUid() + "/answers/" + quizCode);
        ValueEventListener getAnswers = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for(DataSnapshot ansSnapshot : dataSnapshot.getChildren()) {
                    ans[i].setText(ansSnapshot.getValue().toString());
                    i++;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        ansRef.addListenerForSingleValueEvent(getAnswers);

        DatabaseReference qstRef = database.getReference("quiz/" + quizCode + "/questions");
        ValueEventListener getQuestions = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for(DataSnapshot qstSnapshot : dataSnapshot.getChildren()) {
                    qst[i].setText(qstSnapshot.child("question").getValue().toString());
                    i++;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        qstRef.addListenerForSingleValueEvent(getQuestions);

    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.submitQuiz) {

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            final FirebaseUser user = mAuth.getCurrentUser();
            DatabaseReference answers = database.getReference("/");
            ValueEventListener gradeRef = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int i = 0, total = 0, correct = 0;
                    ArrayList<String> studentAns = new ArrayList<>(4);
                    for(DataSnapshot ans : dataSnapshot.child("quiz/" + quizCode + "/questions").getChildren()) {
                        String studentAnsPath = "users/" + user.getUid() + "/answers/" + quizCode + "/q" + i;
                        String correctAnsPath = "quiz/" + quizCode + "/questions/q" + i + "/correct";

                        Toast.makeText(quizFinalReview.this, "ans:" + dataSnapshot.child(studentAnsPath).getValue().toString() + " correct: " + dataSnapshot.child(correctAnsPath).getValue().toString(), Toast.LENGTH_SHORT).show();

                        studentAns.add(dataSnapshot.child(studentAnsPath).getValue().toString());

                        if(studentAns.get(i).contains(dataSnapshot.child(correctAnsPath).getValue().toString())) correct++;
                        total++;
                        i++;
                    }

                    Map<String, Object> finished = new HashMap<>();

                    finished.put(quizCode, Double.toString(((double)correct/total)*100));
                    DatabaseReference finish = database.getReference("users/" + user.getUid() + "/finished/" + quizCode);
                    finish.updateChildren(finished);

                    finished.clear();

                    finish = database.getReference("quiz/" + quizCode + "/grades");
                    finished.put(user.getUid(), Double.toString(((double)correct/total)*100));
                    finish.updateChildren(finished);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            answers.addListenerForSingleValueEvent(gradeRef);

            Intent i = new Intent(quizFinalReview.this, quizComplete.class);
            i.putExtra("quizCode", quizCode);
            startActivity(i);
            finish();
        }
        if(v.getId() == R.id.returnToQuiz) {
            Intent i = new Intent(quizFinalReview.this, exampleQuiz.class);
            i.putExtra("quizCode", quizCode);
            i.putExtra("questionNum", 0);
            startActivity(i);
            finish();
        }
    }
}
