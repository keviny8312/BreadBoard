package com.contigo2.cmsc355.breadboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class exampleQuiz extends AppCompatActivity {
    private String quizCode, questionNumString;
    private int questionNum, totalNumQuestions, choice, maxTimeLimit = 100;
    private long timeLimitMilliseconds, timeLimitFromDatabase;
    private final int MS_TO_S = 1000, S_TO_MIN = 60, MIN_TO_HR = 60, FIVE_MIN = 300;
    public boolean finish, initTime;
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public DatabaseReference userTime = database.getReference("users/" + uid + "/timers");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_quiz);
        quizCode = getIntent().getStringExtra("quizCode");
        questionNum = getIntent().getIntExtra("questionNum", 0);
        questionNumString = "q" + questionNum;

        createInitTimers();

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

        //Here is the timer implementation

        //Modify the number 30000, 30000 milliseconds = 30 seconds, so the time timit should be converted to miliseconds
        //Change the 45 to the time it actually is limited to: pull from firebase.

        // get time from database (initialize on start)

        timeLimitMilliseconds = maxTimeLimit * S_TO_MIN * MS_TO_S;
        new CountDownTimer(timeLimitMilliseconds, MS_TO_S){
            public void onTick(long millisUntilFinished) {
                int timeLeft = (int) millisUntilFinished / MS_TO_S;

                timeRemainingField.setText(getResources().getString(R.string.TimeRemaining, timeLeft/S_TO_MIN, timeLeft%S_TO_MIN));



                //If 5 minutes left, go to the warning page
                if(timeLeft == FIVE_MIN) {
                    Intent i = new Intent(exampleQuiz.this, warningPage.class);
                    startActivity(i);

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
        if(v.getId() == R.id.nextQuestion) {
            finish = false;
            if(answerQuestion()) {
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
        if(v.getId() == R.id.finishQuiz) {
            finish = true;
            if(answerQuestion()) {
                Intent i = new Intent(exampleQuiz.this, quizFinalReview.class);
                startActivity(i);
            }
        }
    }

    public boolean answerQuestion() {
        RadioButton rb1 = findViewById(R.id.ChoiceBTN1);
        RadioButton rb2 = findViewById(R.id.ChoiceBTN2);
        RadioButton rb3 = findViewById(R.id.ChoiceBTN3);
        RadioButton rb4 = findViewById(R.id.ChoiceBTN4);
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
    }

    public void createInitTimers() {
        initTime = false;
        DatabaseReference initTimers = database.getReference();
        ValueEventListener setTimers = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot userTimes = dataSnapshot.child("users/" + uid + "/timers/" + quizCode);
                if(!userTimes.hasChild("start time") || !userTimes.hasChild("end time")) {
                    //Toast.makeText(exampleQuiz.this, "no init time fields found", Toast.LENGTH_SHORT).show();
                    initTime = true;
                }

                if(initTime) {

                    DataSnapshot maxTime = dataSnapshot.child("quiz/" + quizCode + "/time");
                    maxTimeLimit = Integer.valueOf(maxTime.getValue(String.class));
                    int maxTimeHours = maxTimeLimit/MIN_TO_HR;
                    int maxTimeMinutes = maxTimeLimit%MIN_TO_HR;

                    //Toast.makeText(exampleQuiz.this, "from db: " + maxTime + " hr: " + maxTimeHours + " min: " + maxTimeMinutes, Toast.LENGTH_SHORT).show();

                    Calendar currentTime = Calendar.getInstance();
                    int startHour = currentTime.get(Calendar.HOUR_OF_DAY);
                    int endHour = startHour + maxTimeHours;
                    int startMinutes = currentTime.get(Calendar.MINUTE);
                    int endMinutes = startMinutes + maxTimeMinutes;
                    if(endMinutes > MIN_TO_HR) {
                        endMinutes %= MIN_TO_HR;
                        endHour++;
                    }
                    String startTime = startHour + ":" + startMinutes;
                    String endTime = endHour + ":" + endMinutes;

                    Map<String, Object> initTimes = new HashMap<>();
                    initTimes.put("start time", startTime);
                    //Toast.makeText(exampleQuiz.this, String.valueOf(currentTime.HOUR_OF_DAY), Toast.LENGTH_SHORT).show();
                    initTimes.put("end time", endTime);

                    DatabaseReference newTimes = database.getReference("users/" + uid + "/timers/" + quizCode);
                    newTimes.updateChildren(initTimes);

                    //Toast.makeText(exampleQuiz.this, "start: " + startTime + " end: " + endTime, Toast.LENGTH_SHORT).show();

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        initTimers.addListenerForSingleValueEvent(setTimers);
    }
}
