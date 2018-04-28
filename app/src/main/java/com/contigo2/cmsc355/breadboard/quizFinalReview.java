package com.contigo2.cmsc355.breadboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class quizFinalReview extends AppCompatActivity {

    private String quizCode;
    public boolean cont = false;
    public TextView q1, q2, q3, q4, q5, q6, q7, q8, q9, q10, qst[];
    public TextView a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, ans[];
    public int maxTimeLimit, startQuestionTime, endQuestionTime;
    public final int INVALID = 6969, MIN_TO_S = 60, S_TO_MS = 1000, HR_TO_MIN = 60, FIVE_MIN = 300;
    public long timeLimitMilliseconds;
    public CountDownTimer timer;
    public TextView timeRemainingField;
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

        timeRemainingField = findViewById(R.id.timeRemaining);

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

        maxTimeLimit = getIntent().getIntExtra("initTime", INVALID);
        int secondsCarry = Calendar.getInstance().get(Calendar.SECOND);
        //TODO seconds are kinda off when switching
        //Log.d(TAG + " MTL before loop ", String.valueOf(maxTimeLimit));
        startQuestionTime = maxTimeLimit * MIN_TO_S + secondsCarry;
        timeLimitMilliseconds = maxTimeLimit * MIN_TO_S * S_TO_MS + secondsCarry * S_TO_MS;
        timer = new CountDownTimer(timeLimitMilliseconds, S_TO_MS){
            public void onTick(long millisUntilFinished) {
                int timeLeft = (int) millisUntilFinished / S_TO_MS;
                //Log.d(TAG + " timeLeft in loop ", String.valueOf(timeLeft));

                endQuestionTime = timeLeft;
                timeRemainingField.setText(getResources().getString(R.string.TimeRemaining, timeLeft/MIN_TO_S, timeLeft%MIN_TO_S));
                //TODO maybe make this show hours too

                //If 5 minutes left, go to the warning page
                if(timeLeft == FIVE_MIN) {
                    AlertDialog.Builder timeWarning = new AlertDialog.Builder(quizFinalReview.this);
                    timeWarning.setMessage(R.string.timeWarning);
                    timeWarning.setTitle(R.string.alertTitle);
                    timeWarning.setPositiveButton(R.string.alertOK, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    timeWarning.create().show();
                }


            }

            public void onFinish() {
                //Forces the user to go to the quiz confirmation page, and submits quiz contents regardless of whether or not questions have been answered
                //Intent i = new Intent(exampleQuiz.this, quizConfirmation.class);
                //startActivity(i);
                //finish();
            }
        }.start();


    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.submitQuiz) {
            if(hasAnsweredAllQuestions()) {
                submitQuiz();
                timer.cancel();
            }
        }
        if(v.getId() == R.id.returnToQuiz) {
            Calendar currentTime = Calendar.getInstance();
            int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
            int currentMinutes = currentTime.get(Calendar.MINUTE);
            int endHour = getIntent().getIntExtra("endHour", INVALID);
            int endMinutes = getIntent().getIntExtra("endMinutes", INVALID);
            maxTimeLimit = (endHour - currentHour)*HR_TO_MIN + Math.abs(endMinutes - currentMinutes);
            maxTimeLimit--;

            Intent i = new Intent(quizFinalReview.this, exampleQuiz.class);
            i.putExtra("quizCode", quizCode);
            i.putExtra("questionNum", 0);
            i.putExtra("initTime", maxTimeLimit);
            i.putExtra("endHour", endHour);
            i.putExtra("endMinutes", endMinutes);
            startActivity(i);
            finish();
            timer.cancel();
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
