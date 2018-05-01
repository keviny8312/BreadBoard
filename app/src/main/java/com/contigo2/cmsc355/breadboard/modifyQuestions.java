package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class modifyQuestions extends AppCompatActivity {
    private String quizCode, questionNumString;
    private int questionNum, totalNumQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {        // change current question
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_questions);

        quizCode = getIntent().getStringExtra("quizCode");
        questionNum = getIntent().getIntExtra("questionNum", 0);
        questionNumString = "q" + questionNum;

        setPreviousValues();
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.modifyNextQuestion) {              // change current question then go to next
            updateCurrentQuestion();

            if(questionNum + 1 >= totalNumQuestions) {
                Intent i = new Intent(modifyQuestions.this, QuizInformation.class);
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

        if(v.getId() == R.id.finishModifications) {
            updateCurrentQuestion();
            Intent i = new Intent(modifyQuestions.this, QuizInformation.class);
            i.putExtra("quizCode", quizCode);
            startActivity(i);
            finish();
        }
    }

    public void updateCurrentQuestion() {                       // make question changes in database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference qRef = database.getReference("quiz/" + quizCode + "/questions/" + questionNumString);

        TextView curQ = findViewById(R.id.modifyCurrentQuestionNum);
        TextView totQ = findViewById(R.id.modifyTotalNumQuestions);
        EditText qtxt = findViewById(R.id.modifyQuestionField);
        EditText ans1 = findViewById(R.id.modifyAns1);
        EditText ans2 = findViewById(R.id.modifyAns2);
        EditText ans3 = findViewById(R.id.modifyAns3);
        EditText ans4 = findViewById(R.id.modifyAns4);
        EditText ans[] = {ans1, ans2, ans3, ans4};
        CheckBox cb1  = findViewById(R.id.modifyAnsCheck1);
        CheckBox cb2  = findViewById(R.id.modifyAnsCheck2);
        CheckBox cb3  = findViewById(R.id.modifyAnsCheck3);
        CheckBox cb4  = findViewById(R.id.modifyAnsCheck4);
        CheckBox cb[] = {cb1, cb2, cb3, cb4};

        Map<String, Object> values = new HashMap<>();

        String ansString = "";
        for(int i = 0; i < 4; i++) {
            if(cb[i].isChecked()) ansString += i+1;
        }
        values.put("question", qtxt.getText().toString());
        values.put("correct", ansString);
        qRef.updateChildren(values);

        values.clear();
        DatabaseReference aRef = qRef.child("answers");
        for(int i = 0; i < 4; i++) {
            values.put("ans" + i, ans[i].getText().toString());
        }
        aRef.updateChildren(values);

    }

    public void setPreviousValues() {                           // get previous question data from database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        TextView curQ = findViewById(R.id.modifyCurrentQuestionNum);
        Resources res = getResources();
        curQ.setText(res.getString(R.string.CurrentQuestionNum, questionNum+1));
        final TextView totQ = findViewById(R.id.modifyTotalNumQuestions);
        final EditText qtxt = findViewById(R.id.modifyQuestionField);
        final EditText ans1 = findViewById(R.id.modifyAns1);
        final EditText ans2 = findViewById(R.id.modifyAns2);
        final EditText ans3 = findViewById(R.id.modifyAns3);
        final EditText ans4 = findViewById(R.id.modifyAns4);
        final CheckBox cb1  = findViewById(R.id.modifyAnsCheck1);
        final CheckBox cb2  = findViewById(R.id.modifyAnsCheck2);
        final CheckBox cb3  = findViewById(R.id.modifyAnsCheck3);
        final CheckBox cb4  = findViewById(R.id.modifyAnsCheck4);

        DatabaseReference questionRef = database.getReference("quiz/" + quizCode);
        ValueEventListener getQuizzes = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String numQuestionsTotalTest = "";
                numQuestionsTotalTest = dataSnapshot.child("num questions").getValue(String.class);
                totalNumQuestions = Integer.valueOf(numQuestionsTotalTest);
                qtxt.setText(dataSnapshot.child("questions").child(questionNumString).child("question").getValue(String.class));
                totQ.setText("" + totalNumQuestions);

                int i = 1;
                for (DataSnapshot codeSnapshot: dataSnapshot.child("questions").child(questionNumString).child("answers").getChildren()) {
                    if(i == 1) ans1.setText(codeSnapshot.getValue().toString());
                    if(i == 2) ans2.setText(codeSnapshot.getValue().toString());
                    if(i == 3) ans3.setText(codeSnapshot.getValue().toString());
                    if(i == 4) ans4.setText(codeSnapshot.getValue().toString());
                    i++;
                }
                String correctAnswers = dataSnapshot.child("questions").child(questionNumString).child("correct").getValue(String.class);
                if(correctAnswers.contains("1")) cb1.setChecked(true);
                if(correctAnswers.contains("2")) cb2.setChecked(true);
                if(correctAnswers.contains("3")) cb3.setChecked(true);
                if(correctAnswers.contains("4")) cb4.setChecked(true);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        questionRef.addListenerForSingleValueEvent(getQuizzes);
    }

}
