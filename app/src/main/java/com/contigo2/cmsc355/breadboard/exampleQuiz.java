package com.contigo2.cmsc355.breadboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class exampleQuiz extends AppCompatActivity {
    private String quizCode, questionNumString;
    private final String TAG = "eQuiz";
    private int questionNum, totalNumQuestions, choice, maxTimeLimit = 100;
    private long timeLimitMilliseconds, timeLimitFromDatabase;
    private int startQuestionTime, endQuestionTime, questionTime = 0;
    private final int S_TO_MS = 1000, MIN_TO_S = 60, HR_TO_MIN = 60, FIVE_MIN = 300, INVALID = 6969;
    public boolean finish, initTime;
    public Map<String, Object> secondsRemaining = new HashMap<>();
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public DatabaseReference userTime = database.getReference("users/" + uid + "/timers/");
    public CountDownTimer timer;
    public RadioButton rb1, rb2, rb3, rb4, rb[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {        // load current question from database and display
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_quiz);
        quizCode = getIntent().getStringExtra("quizCode");
        questionNum = getIntent().getIntExtra("questionNum", 0);
        questionNumString = "q" + questionNum;

        rb1 = findViewById(R.id.ChoiceBTN1);
        rb2 = findViewById(R.id.ChoiceBTN2);
        rb3 = findViewById(R.id.ChoiceBTN3);
        rb4 = findViewById(R.id.ChoiceBTN4);
        rb = new RadioButton[]{rb1, rb2, rb3, rb4};
        getPreviousAnswer();

        database = FirebaseDatabase.getInstance();
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

        final TextView timeRemainingField = findViewById(R.id.TimeRemaining);

        DatabaseReference questionRef = database.getReference("quiz/" + quizCode);
        ValueEventListener getQuizzes = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String numQuestionsTotalTest = "";
                numQuestionsTotalTest = dataSnapshot.child("num questions").getValue().toString();
                totalNumQuestions = Integer.valueOf(numQuestionsTotalTest);
                qtxt.setText(dataSnapshot.child("questions").child(questionNumString).child("question").getValue().toString());
                totQ.setText(getResources().getString(R.string.totalQuestions, totalNumQuestions));

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

        maxTimeLimit = getIntent().getIntExtra("initTime", INVALID);
        if(maxTimeLimit == getIntent().getIntExtra("totalTime", INVALID)) maxTimeLimit--;
        int endSeconds = getIntent().getIntExtra("endSeconds", 59);
        int secondsCarry = MIN_TO_S - (Math.abs(Calendar.getInstance().get(Calendar.SECOND) - endSeconds) % MIN_TO_S);

        startQuestionTime = maxTimeLimit * MIN_TO_S + secondsCarry;
        timeLimitMilliseconds = maxTimeLimit * MIN_TO_S * S_TO_MS + secondsCarry * S_TO_MS;
        timer = new CountDownTimer(timeLimitMilliseconds, S_TO_MS){
            public void onTick(long millisUntilFinished) {
                int timeLeft = (int) millisUntilFinished / S_TO_MS;

                endQuestionTime = timeLeft;
                timeRemainingField.setText(getResources().getString(R.string.TimeRemaining, timeLeft/MIN_TO_S, timeLeft%MIN_TO_S));

                //If 5 minutes left, go to the warning page
                if(timeLeft == FIVE_MIN) {
                    AlertDialog.Builder timeWarning = new AlertDialog.Builder(exampleQuiz.this);
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
                AlertDialog.Builder timeUp = new AlertDialog.Builder(exampleQuiz.this);
                timeUp.setMessage(R.string.incompleteSubmit);
                timeUp.setTitle(R.string.alertFinished);
                timeUp.setPositiveButton(R.string.alertSubmit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                timeUp.create().show();

                Map<String, Object> answers = new HashMap<>();
                for(questionNum = questionNum; questionNum < totalNumQuestions; questionNum++) {
                    answers.put("q" + questionNum, "0");
                }
                DatabaseReference ans = database.getReference("users/" + uid + "/answers/" + quizCode);
                ans.updateChildren(answers);

                //Forces the user to go to the quiz confirmation page, and submits quiz contents regardless of whether or not questions have been answered
                Intent i = new Intent(exampleQuiz.this, quizComplete.class);
                i.putExtra("quizCode", quizCode);
                startActivity(i);
                finish();
            }
        }.start();
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.nextQuestion) {        // go to next question if not on last, else go to quiz review
            finish = false;
            if(answerQuestion()) {
                if(questionNum + 1 >= totalNumQuestions) {
                    Log.d(TAG, "last question, updating question time");
                    updateQuestionTime();
                    Log.d(TAG, "finished updating question time");

                    Calendar currentTime = Calendar.getInstance();
                    int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
                    int currentMinutes = currentTime.get(Calendar.MINUTE);
                    int endHour = getIntent().getIntExtra("endHour", INVALID);
                    int endMinutes = getIntent().getIntExtra("endMinutes", INVALID);
                    int endSeconds = getIntent().getIntExtra("endSeconds", INVALID);
                    int hour = (endHour - currentHour);
                    if(hour > 0) hour--;
                    int min;
                    if(hour == 0) min = Math.abs(endMinutes - currentMinutes);
                    else min = HR_TO_MIN - Math.abs(endMinutes - currentMinutes);
                    Log.d(TAG, "hour: " + hour + " min: " + min);
                    maxTimeLimit = hour*HR_TO_MIN + min;

                    Intent i = new Intent(exampleQuiz.this, quizFinalReview.class);
                    i.putExtra("totalTime", getIntent().getIntExtra("totalTime", INVALID));
                    i.putExtra("quizCode", quizCode);
                    i.putExtra("questionNum", questionNum + 1);
                    i.putExtra("initTime", maxTimeLimit);
                    i.putExtra("endHour", endHour);
                    i.putExtra("endMinutes", endMinutes);
                    i.putExtra("endSeconds", endSeconds);
                    startActivity(i);
                    finish();
                    timer.cancel();
                }
                else {
                    updateQuestionTime();

                    Calendar currentTime = Calendar.getInstance();
                    int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
                    int currentMinutes = currentTime.get(Calendar.MINUTE);
                    int endHour = getIntent().getIntExtra("endHour", INVALID);
                    int endMinutes = getIntent().getIntExtra("endMinutes", INVALID);
                    int endSeconds = getIntent().getIntExtra("endSeconds", INVALID);
                    int hour = (endHour - currentHour);
                    if(hour > 0) hour--;
                    int min;
                    if(hour == 0) min = Math.abs(endMinutes - currentMinutes);
                    else min = HR_TO_MIN - Math.abs(endMinutes - currentMinutes);
                    int totalTimeLimit = getIntent().getIntExtra("totalTime", INVALID);
                    maxTimeLimit = hour*HR_TO_MIN + min;
                    if(maxTimeLimit == totalTimeLimit) maxTimeLimit--;
                    Log.d(TAG, "endHour " + endHour + " currentHour " + currentHour);
                    Log.d(TAG, "(endHour - currentHour)*HR_TO_MIN " + (endHour - currentHour)*HR_TO_MIN);
                    Log.d(TAG, "Math.abs(endMinutes - currentMinutes) " + Math.abs(endMinutes - currentMinutes));
                    Log.d(TAG + " MTL before next", String.valueOf(maxTimeLimit));

                    Intent i = getIntent();
                    i.putExtra("totalTime", getIntent().getIntExtra("totalTime", INVALID));
                    i.putExtra("questionNum", questionNum + 1);
                    i.putExtra("initTime", maxTimeLimit);
                    i.putExtra("endSeconds", endSeconds);
                    i.putExtra("endHour", endHour);
                    i.putExtra("endMinutes", endMinutes);
                    startActivity(i);
                    finish();
                    timer.cancel();
                }
            }



        }
        if(v.getId() == R.id.finishQuiz) {
            finish = true;
            if(answerQuestion()) {
                updateQuestionTime();
                Intent i = new Intent(exampleQuiz.this, quizFinalReview.class);
                startActivity(i);
            }
        }
    }

    public void getPreviousAnswer() {                           // get previous answers from database
        DatabaseReference ansDatabase = database.getReference("users/" + uid + "/answers/" + quizCode);
        ValueEventListener getAnswer = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(questionNumString)) {
                    String answer = dataSnapshot.child(questionNumString).getValue(String.class);
                    rb[Integer.valueOf(answer) - 1].setChecked(true);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        ansDatabase.addListenerForSingleValueEvent(getAnswer);
    }

    public boolean answerQuestion() {
        choice = 0;
        if(rb1.isChecked()) choice = 1;
        if(rb2.isChecked()) choice = 2;
        if(rb3.isChecked()) choice = 3;
        if(rb4.isChecked()) choice = 4;

        if(choice == 0) {
            //Intent i = new Intent(exampleQuiz.this, warningPageUnanswered.class);
            //i.putExtra("quizCode", quizCode);
            //i.putExtra("questionNum", questionNum);
            //startActivity(i);
            //return false;


            AlertDialog.Builder unanswered = new AlertDialog.Builder(exampleQuiz.this);
            unanswered.setMessage(R.string.incompleteWarning);
            unanswered.setTitle(R.string.alertTitle);
            unanswered.setPositiveButton(R.string.alertContinue, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();
                    DatabaseReference quizRef = database.getReference("users/" + user.getUid() + "/answers/" + quizCode);

                    Map<String, Object> answer = new HashMap<>();
                    answer.put(questionNumString, Integer.toString(choice));
                    quizRef.updateChildren(answer);

                    dialog.dismiss();

                    if(finish) {
                        Intent i = new Intent(exampleQuiz.this, quizFinalReview.class);
                        startActivity(i);
                    }
                    else {
                        if(questionNum + 1 >= totalNumQuestions) {
                            Intent i = new Intent(exampleQuiz.this, quizFinalReview.class);
                            i.putExtra("quizCode", quizCode);
                            startActivity(i);
                        }
                        else {
                            Intent i = getIntent();
                            i.putExtra("questionNum", questionNum + 1);
                            //i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(i);
                        }
                    }
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

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference quizRef = database.getReference("users/" + user.getUid() + "/answers/" + quizCode);

        Map<String, Object> answer = new HashMap<>();
        answer.put(questionNumString, Integer.toString(choice));
        quizRef.updateChildren(answer);
        return true;
    }                       // push answer to database

    public void updateQuestionTime() {                          // push time taken for question to database
        final DatabaseReference questionTimeRef = FirebaseDatabase.getInstance().getReference("quiz/" + quizCode + "/times/q" + questionNum);
        ValueEventListener getPreviousQuestionTime = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(uid)) {
                    questionTime += dataSnapshot.child(uid).getValue(Integer.class);
                    Log.d(TAG, "found previous question time, questionTime = " + questionTime);
                }
                questionTime += startQuestionTime - endQuestionTime;

                Map<String, Object> questionTimeMap = new HashMap<>();
                questionTimeMap.put(uid, questionTime);
                questionTimeRef.updateChildren(questionTimeMap);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        questionTimeRef.addListenerForSingleValueEvent(getPreviousQuestionTime);
    }
}