package com.contigo2.cmsc355.breadboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import static java.lang.Thread.sleep;

public class exampleQuiz extends AppCompatActivity {
    private String quizCode, questionNumString;
    private final String TAG = "eQuiz";
    private int questionNum, totalNumQuestions, choice, maxTimeLimit = 100;
    private long timeLimitMilliseconds, timeLimitFromDatabase;
    private final int S_TO_MS = 1000, MIN_TO_S = 60, HR_TO_MIN = 60, FIVE_MIN = 300, INVALID = 6969;
    public boolean finish, initTime;
    public Map<String, Object> secondsRemaining = new HashMap<>();
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public DatabaseReference userTime = database.getReference("users/" + uid + "/timers/");
    public CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_quiz);
        quizCode = getIntent().getStringExtra("quizCode");
        questionNum = getIntent().getIntExtra("questionNum", 0);
        questionNumString = "q" + questionNum;

        //createInitTimers();

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
        maxTimeLimit = getIntent().getIntExtra("initTime", INVALID);
        int secondsCarry = Calendar.getInstance().get(Calendar.SECOND);
        //TODO seconds are kinda off when switching
        Log.d(TAG + " MTL before loop ", String.valueOf(maxTimeLimit));
        timeLimitMilliseconds = maxTimeLimit * MIN_TO_S * S_TO_MS + secondsCarry * S_TO_MS;
        timer = new CountDownTimer(timeLimitMilliseconds, S_TO_MS){
            public void onTick(long millisUntilFinished) {
                int timeLeft = (int) millisUntilFinished / S_TO_MS;
                Log.d(TAG + " timeLeft in loop ", String.valueOf(timeLeft));

                timeRemainingField.setText(getResources().getString(R.string.TimeRemaining, timeLeft/MIN_TO_S, timeLeft%MIN_TO_S));
                //TODO maybe make this show hours too

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
                    Calendar currentTime = Calendar.getInstance();
                    int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
                    int currentMinutes = currentTime.get(Calendar.MINUTE);
                    //int currentSecond = currentTime.get(Calendar.SECOND);
                    int endHour = getIntent().getIntExtra("endHour", INVALID);
                    int endMinutes = getIntent().getIntExtra("endMinutes", INVALID);
                    maxTimeLimit = (endHour - currentHour)*HR_TO_MIN + Math.abs(endMinutes - currentMinutes);
                    maxTimeLimit--;

                    Intent i = getIntent();
                    i.putExtra("questionNum", questionNum + 1);
                    //i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    i.putExtra("initTime", maxTimeLimit);
                    //i.putExtra("seconds", currentSecond);
                    i.putExtra("endHour", endHour);
                    i.putExtra("endMinutes", endMinutes);
                    startActivity(i);
                    timer.cancel();
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
                    int maxTimeHours = maxTimeLimit/HR_TO_MIN;
                    int maxTimeMinutes = maxTimeLimit%HR_TO_MIN;

                    //Toast.makeText(exampleQuiz.this, "from db: " + maxTime + " hr: " + maxTimeHours + " min: " + maxTimeMinutes, Toast.LENGTH_SHORT).show();

                    Calendar currentTime = Calendar.getInstance();
                    int startHour = currentTime.get(Calendar.HOUR_OF_DAY);
                    int endHour = startHour + maxTimeHours;
                    int startMinutes = currentTime.get(Calendar.MINUTE);
                    int endMinutes = startMinutes + maxTimeMinutes;
                    if(endMinutes > HR_TO_MIN) {
                        endMinutes %= HR_TO_MIN;
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

                    maxTimeLimit = maxTimeMinutes + HR_TO_MIN*maxTimeHours;
                    Log.d(TAG + " MTL after create ", String.valueOf(maxTimeLimit));
                    //Toast.makeText(exampleQuiz.this, "maxTimeLimit: " + maxTimeLimit, Toast.LENGTH_SHORT).show();

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        initTimers.addListenerForSingleValueEvent(setTimers);
    }
}