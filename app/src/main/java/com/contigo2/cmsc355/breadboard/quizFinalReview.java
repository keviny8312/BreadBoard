package com.contigo2.cmsc355.breadboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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
    public boolean cont = false;
    public TextView q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, qst[];
    public TextView a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, ans[];
    // TODO this needs to be replaced with array adapter (or something else dynamic !!)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_final_review);
        quizCode = getIntent().getStringExtra("quizCode");
        q1 = findViewById(R.id.question1);
        q2 = findViewById(R.id.question2);
        q3 = findViewById(R.id.question3);
        q4 = findViewById(R.id.question4);
        // TODO more than 4 questions lol!!!!
        //q5 = findViewById(R.id.question5);
        //q6 = findViewById(R.id.question6);
        //q7 = findViewById(R.id.question7);
        //q8 = findViewById(R.id.question8);
        //q9 = findViewById(R.id.question9);
        //q10 = findViewById(R.id.question10);
        qst = new TextView[]{q1, q2, q3, q4};//, q5, q6, q7, q8, q9, q10};

        a1 = findViewById(R.id.Answer1);
        a2 = findViewById(R.id.Answer2);
        a3 = findViewById(R.id.Answer3);
        a4 = findViewById(R.id.Answer4);
        //a5 = findViewById(R.id.Answer5);
        //a6 = findViewById(R.id.Answer6);
        //a7 = findViewById(R.id.Answer7);
        //a8 = findViewById(R.id.Answer8);
        //a9 = findViewById(R.id.Answer9);
        //a10 = findViewById(R.id.Answer10);
        ans = new TextView[]{a1, a2, a3, a4};//, a5, a6, a7, a8, a9, a10};

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
            if(hasAnsweredAllQuestions()) submitQuiz();
        }
        if(v.getId() == R.id.returnToQuiz) {
            Intent i = new Intent(quizFinalReview.this, exampleQuiz.class);
            i.putExtra("quizCode", quizCode);
            i.putExtra("questionNum", 0);
            startActivity(i);
            finish();
        }
    }

    public boolean hasAnsweredAllQuestions() {

        boolean allAnswered = true;

        for(int i = 0; i < ans.length; i++) {
            if(ans[i].getText().toString().equals("0")) allAnswered = false;
        }

        if(!allAnswered) {
            AlertDialog.Builder unanswered = new AlertDialog.Builder(quizFinalReview.this);
            unanswered.setMessage(R.string.incompleteSubmission);
            unanswered.setTitle(R.string.alertTitle);
            unanswered.setPositiveButton(R.string.alertSubmit, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    submitQuiz();
                    dialog.dismiss();
                }
            });
            unanswered.setNegativeButton(R.string.alertBack, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            unanswered.create().show();
            return false;
        }

        return true;
    }

    public void submitQuiz() {
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

                    // debugging answers, leave here for now
                    //Toast.makeText(quizFinalReview.this, "ans:" + dataSnapshot.child(studentAnsPath).getValue().toString() + " correct: " + dataSnapshot.child(correctAnsPath).getValue().toString(), Toast.LENGTH_SHORT).show();

                    studentAns.add(dataSnapshot.child(studentAnsPath).getValue().toString());

                    if(dataSnapshot.child(correctAnsPath).getValue().toString().contains(studentAns.get(i))) correct++;
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
}
