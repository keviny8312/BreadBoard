package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class exampleQuiz extends AppCompatActivity {
    private String quizCode, questionNumString;
    private int questionNum, totalNumQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_quiz);
        quizCode = getIntent().getStringExtra("quizCode");
        questionNum = getIntent().getIntExtra("questionNum", 0);
        questionNumString = "q" + questionNum;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        TextView curQ = findViewById(R.id.currentQuestion);
        Resources res = getResources();
        curQ.setText(res.getString(R.string.CurrentQuestionNum, questionNum+1));
        final TextView totQ = findViewById(R.id.totalNumQuestions);
        final TextView qtxt = findViewById(R.id.questionText);
        final TextView ans1 = findViewById(R.id.answerText1);
        final TextView ans2 = findViewById(R.id.answerText2);
        final TextView ans3 = findViewById(R.id.answerText3);
        final TextView ans4 = findViewById(R.id.answerText4);

        DatabaseReference questionRef = database.getReference("quiz/" + quizCode);
        ValueEventListener getQuizzes = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String numQuestionsTotalTest = "";
                numQuestionsTotalTest = dataSnapshot.child("num questions").getValue().toString();
                totalNumQuestions = Integer.valueOf(numQuestionsTotalTest);
                qtxt.setText(dataSnapshot.child("questions").child(questionNumString).child("question").getValue().toString());
                totQ.setText("" + totalNumQuestions);

                int i = 1;
                for (DataSnapshot codeSnapshot: dataSnapshot.child("questions").child(questionNumString).child("answers").getChildren()) {
                    if(i == 1) ans1.setText(codeSnapshot.getValue().toString());
                    if(i == 2) ans2.setText(codeSnapshot.getValue().toString());
                    if(i == 3) ans3.setText(codeSnapshot.getValue().toString());
                    if(i == 4) ans4.setText(codeSnapshot.getValue().toString());
                    i++;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        questionRef.addListenerForSingleValueEvent(getQuizzes);
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.nextQuestion) {
            answerQuestion();

            if(questionNum + 1 >= totalNumQuestions) {
                Intent i = new Intent(exampleQuiz.this, quizFinalReview.class);
                i.putExtra("quizCode", quizCode);
                startActivity(i);
                finish();
            }
            else {
                Intent i = getIntent();
                i.putExtra("questionNum", questionNum + 1);
                finish();
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }

        }
        if(v.getId() == R.id.finishQuiz) {
            answerQuestion();
            //TODO check if all questions answered
            Intent i = new Intent(exampleQuiz.this, quizFinalReview.class);
            startActivity(i);
        }
    }

    public void answerQuestion() {
        RadioButton rb1 = findViewById(R.id.ChoiceBTN1);
        RadioButton rb2 = findViewById(R.id.ChoiceBTN2);
        RadioButton rb3 = findViewById(R.id.ChoiceBTN3);
        RadioButton rb4 = findViewById(R.id.ChoiceBTN4);
        int choice = 0;
        if(rb1.isChecked()) choice = 1;
        if(rb2.isChecked()) choice = 2;
        if(rb3.isChecked()) choice = 3;
        if(rb4.isChecked()) choice = 4;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference quizRef = database.getReference("users/" + user.getUid() + "/answers/" + quizCode);

        Map<String, Object> answer = new HashMap<>();
        answer.put(questionNumString, Integer.toString(choice));
        quizRef.updateChildren(answer);
    }
}
