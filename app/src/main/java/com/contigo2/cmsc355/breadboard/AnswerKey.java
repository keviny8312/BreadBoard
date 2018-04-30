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

public class AnswerKey extends AppCompatActivity {
    private String quizCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_key);
        quizCode = getIntent().getStringExtra("quizCode");

        populateTextFields();
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.returnToQuizDetails) {
            Intent i = new Intent(AnswerKey.this, postQuizInfo.class);
            i.putExtra("quizCode", quizCode);
            startActivity(i);
            finish();
        }
    }

    public void populateTextFields() {                      // get answers from database and put into textviews
        TextView TVq1  = findViewById(R.id.QuestionText1);
        TextView TVq2  = findViewById(R.id.QuestionText2);
        TextView TVq3  = findViewById(R.id.QuestionText3);
        TextView TVq4  = findViewById(R.id.QuestionText4);
        final TextView TVq[] = {TVq1, TVq2, TVq3, TVq4};
        TextView TVua1 = findViewById(R.id.userAnswer1);
        TextView TVua2 = findViewById(R.id.userAnswer2);
        TextView TVua3 = findViewById(R.id.userAnswer3);
        TextView TVua4 = findViewById(R.id.userAnswer4);
        final TextView TVua[] = {TVua1, TVua2, TVua3, TVua4};
        TextView TVca1 = findViewById(R.id.correctAnswer1);
        TextView TVca2 = findViewById(R.id.correctAnswer2);
        TextView TVca3 = findViewById(R.id.correctAnswer3);
        TextView TVca4 = findViewById(R.id.correctAnswer4);
        final TextView TVca[] = {TVca1, TVca2, TVca3, TVca4};

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference qRef = database.getReference();

        ValueEventListener getKey = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {             // TODO careful!!!
                int numQuestions = Integer.valueOf(dataSnapshot.child("quiz/" + quizCode + "/num questions").getValue(String.class));
                for(int i = 0; i < numQuestions; i++) {                                  // only up to 4 q's!!!
                    String question = dataSnapshot.child("quiz/" + quizCode + "/questions/q" + i + "/question").getValue(String.class);
                    String userAns = dataSnapshot.child("users/" + user.getUid() + "/answers/" + quizCode + "/q" + i).getValue(String.class);
                    String corrAns = dataSnapshot.child("quiz/" + quizCode + "/questions/q" + i + "/correct").getValue(String.class);

                    TVq[i].setText(question);
                    TVua[i].setText(userAns);
                    if(corrAns.length() <= 1) TVca[i].setText(userAns);
                    else {
                        String corrAnsFormatted = "";
                        corrAnsFormatted += corrAns.charAt(0);
                        for(int k = 1; k < corrAns.length(); k++) corrAnsFormatted += " or " + corrAns.charAt(k);
                        TVca[i].setText(corrAnsFormatted);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        qRef.addListenerForSingleValueEvent(getKey);
    }
}
