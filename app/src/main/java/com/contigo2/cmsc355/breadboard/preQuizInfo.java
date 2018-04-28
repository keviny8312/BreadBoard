package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

public class preQuizInfo extends AppCompatActivity {
    String quizCode, name, dueDate, time, numQ;
    final int MS_TO_S = 1000, S_TO_MIN = 60, MIN_TO_HR = 60, FIVE_MIN = 300, INVALID = 6969;
    int maxTimeLimit = INVALID;
    boolean initTime;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference userTime = database.getReference("users/" + uid + "/timers");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_quiz_info);

        quizCode = getIntent().getStringExtra("quizCode");

        final TextView TVname = findViewById(R.id.quizTitleField);
        final TextView TVdueDate = findViewById(R.id.quizDueDate);
        final TextView TVtime = findViewById(R.id.quizTimeLimit);
        final TextView TVnumQ = findViewById(R.id.quizNumQuestions);
        final Resources res = getResources();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference getQuizInfo = database.getReference("quiz/" + quizCode);

        ValueEventListener listenQuizInfo = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot codeSnapshot: dataSnapshot.getChildren()) {
                    if(codeSnapshot.getKey().equals("code"));
                    if(codeSnapshot.getKey().equals("due date")) {
                        dueDate = codeSnapshot.getValue(String.class);
                        TVdueDate.setText(res.getString(R.string.dueDate, dueDate));
                    }
                    if(codeSnapshot.getKey().equals("name")) {
                        name = codeSnapshot.getValue(String.class);
                        TVname.setText(res.getString(R.string.quizName, name));
                    }
                    if(codeSnapshot.getKey().equals("num questions")) {
                        numQ = codeSnapshot.getValue(String.class);
                        TVnumQ.setText(res.getString(R.string.numQuestions, numQ));
                    }
                    if(codeSnapshot.getKey().equals("className")) {
                        //qClass = codeSnapshot.getValue(String.class);
                        //TVclass.setText(res.getString(R.string.className, qClass));
                    }
                    if(codeSnapshot.getKey().equals("time")) {
                        time = codeSnapshot.getValue(String.class);
                        TVtime.setText(res.getString(R.string.timeLimit, time));
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        getQuizInfo.addListenerForSingleValueEvent(listenQuizInfo);
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.takeQuizButton) {
            if(!time.equals("0")) {
                initTime = false;
                DatabaseReference initTimers = database.getReference();
                ValueEventListener setTimers = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DataSnapshot userTimes = dataSnapshot.child("users/" + uid + "/timers/" + quizCode);
                        if (!userTimes.hasChild("start time") || !userTimes.hasChild("end time")) {
                            //Toast.makeText(exampleQuiz.this, "no init time fields found", Toast.LENGTH_SHORT).show();
                            initTime = true;
                        }

                        if (initTime) {

                            DataSnapshot maxTime = dataSnapshot.child("quiz/" + quizCode + "/time");
                            maxTimeLimit = Integer.valueOf(maxTime.getValue(String.class));
                            int maxTimeHours = maxTimeLimit / MIN_TO_HR;
                            int maxTimeMinutes = maxTimeLimit % MIN_TO_HR;

                            //Toast.makeText(exampleQuiz.this, "from db: " + maxTime + " hr: " + maxTimeHours + " min: " + maxTimeMinutes, Toast.LENGTH_SHORT).show();

                            Calendar currentTime = Calendar.getInstance();
                            int startHour = currentTime.get(Calendar.HOUR_OF_DAY);
                            int endHour = startHour + maxTimeHours;
                            int startMinutes = currentTime.get(Calendar.MINUTE);
                            int endMinutes = startMinutes + maxTimeMinutes;
                            if (endMinutes > MIN_TO_HR) {
                                endMinutes %= MIN_TO_HR;
                                endHour++;
                            }
                            int endSeconds = currentTime.get(Calendar.SECOND);
                            String startTime = startHour + ":" + startMinutes + ":" + endSeconds;
                            String endTime = endHour + ":" + endMinutes + ":" + endSeconds;

                            Map<String, Object> initTimes = new HashMap<>();
                            initTimes.put("start time", startTime);
                            //Toast.makeText(exampleQuiz.this, String.valueOf(currentTime.HOUR_OF_DAY), Toast.LENGTH_SHORT).show();
                            initTimes.put("end time", endTime);

                            DatabaseReference newTimes = database.getReference("users/" + uid + "/timers/" + quizCode);
                            newTimes.updateChildren(initTimes);

                            maxTimeLimit = maxTimeMinutes + MIN_TO_HR * maxTimeHours;
                            maxTimeLimit--;

                            //Log.d(TAG + " MTL after create ", String.valueOf(maxTimeLimit));
                            //Toast.makeText(exampleQuiz.this, "maxTimeLimit: " + maxTimeLimit, Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(preQuizInfo.this, exampleQuiz.class);
                            i.putExtra("endHour", endHour);
                            i.putExtra("endMinutes", endMinutes);
                            i.putExtra("endSeconds", endSeconds);
                            i.putExtra("quizCode", quizCode);
                            i.putExtra("questionNum", 0);
                            i.putExtra("initTime", maxTimeLimit);
                            startActivity(i);
                            finish();
                        }
                        else {
                            Calendar currentTime = Calendar.getInstance();
                            int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
                            int currentMinutes = currentTime.get(Calendar.MINUTE);
                            //int currentSecond = currentTime.get(Calendar.SECOND);
                            String endTime = userTimes.child("end time").getValue(String.class);
                            int endHour = Integer.valueOf(endTime.substring(0, endTime.indexOf(':')));
                            //int endMinutes = Integer.valueOf(endTime.substring(endTime.indexOf(':') + 1));
                            int endMinutes = Integer.valueOf(endTime.substring(endTime.indexOf(':') + 1, endTime.lastIndexOf(':')));
                            int endSeconds = Integer.valueOf(endTime.substring(endTime.lastIndexOf(':') + 1));
                            maxTimeLimit = (endHour - currentHour) * MIN_TO_HR - Math.abs(endMinutes - currentMinutes);
                            maxTimeLimit--;

                            Intent i = new Intent(preQuizInfo.this, exampleQuiz.class);
                            i.putExtra("quizCode", quizCode);
                            i.putExtra("questionNum", 0);
                            i.putExtra("initTime", maxTimeLimit);
                            //i.putExtra("seconds", currentSecond);
                            i.putExtra("endHour", endHour);
                            i.putExtra("endMinutes", endMinutes);
                            i.putExtra("endSeconds", endSeconds);
                            startActivity(i);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };
                initTimers.addListenerForSingleValueEvent(setTimers);
            }
        }
    }
}
