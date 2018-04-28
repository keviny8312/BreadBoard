package com.contigo2.cmsc355.breadboard;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
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

public class quizStatistics extends ListActivity {
    private String quizCode;
    public final String TAG = "quizStatistics";
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    ArrayList<String> listQuizzes = new ArrayList<>();
    ArrayAdapter<String> adapter;
    public Spinner sortByMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_statistics);
        quizCode = getIntent().getStringExtra("quizCode");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listQuizzes);
        setListAdapter(adapter);

        sortByMenu = findViewById(R.id.sortByMenu);
        sortByMenu.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) sortListTimeHighLow();
                if(position == 1) sortListTimeLowHigh();
                if(position == 2) sortListPercentHighLow();
                if(position == 3) sortListPercentLowHigh();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.getConfirmationNumbers) {
            Intent i = new Intent(quizStatistics.this, quizConfirmationNumbers.class);
            i.putExtra("quizCode", quizCode);
            startActivity(i);
        }
        if(v.getId() == R.id.sortByMenu){
            sortByMenu = findViewById(R.id.sortByMenu);
            ArrayAdapter<CharSequence> arrAdapter = ArrayAdapter.createFromResource(this, R.array.sort_by_choices, android.R.layout.simple_spinner_item);
            arrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sortByMenu.setAdapter(arrAdapter);
        }
    }

    public void updateList() {
        final TextView TVaverage = findViewById(R.id.averageQuizGrade);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        final Resources res = getResources();

        DatabaseReference quizRef = database.getReference();
        ValueEventListener getGrades = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                int total = 0;
                double runningTotal = 0, average;
                for (DataSnapshot grade: dataSnapshot.child("quiz/" + quizCode + "/grades").getChildren()) {
                    String studentGrade = grade.getValue(String.class);
                    runningTotal += Float.valueOf(studentGrade);
                    total++;
                }
                average = runningTotal / total;
                TVaverage.setText(res.getString(R.string.averageGrade, average));
                */

                int totalStudents, totalQuestions = 0;
                double runningTimeOnQuestion, runningTimeTotal = 0, averageTimeOnQuestion, averageTimeTotal;
                for(DataSnapshot question : dataSnapshot.child("quiz/" + quizCode + "/times").getChildren()) {
                    runningTimeOnQuestion = 0;
                    totalStudents = 0;
                    for(DataSnapshot studentTime : question.getChildren()) {
                        //Log.d(TAG, "current student time: " + studentTime.getValue(Long.class));
                        String studentTimeOnQuestion = String.valueOf(studentTime.getValue(Long.class));
                        runningTimeOnQuestion += Integer.valueOf(studentTimeOnQuestion);
                        totalStudents++;
                    }
                    //Log.d(TAG, "finished question " + totalQuestions);
                    averageTimeOnQuestion = runningTimeOnQuestion / totalStudents;
                    adapter.add("Question " + (Integer.valueOf(question.getKey().substring(1)) + 1) + ": " + averageTimeOnQuestion + " seconds");

                    runningTimeTotal += averageTimeOnQuestion;
                    totalQuestions++;
                }
                averageTimeTotal = runningTimeTotal / totalQuestions;
                //Log.d(TAG, "finished all times, average time " + averageTimeTotal);
                TVaverage.setText(res.getString(R.string.averageGrade, averageTimeTotal));

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        quizRef.addListenerForSingleValueEvent(getGrades);
    }

    public void getTimes() {
        adapter.clear();

        final TextView TVaverage = findViewById(R.id.averageQuizGrade);
        DatabaseReference timeRef = database.getReference();
        ValueEventListener getTime = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalStudents, totalQuestions = 0;
                double runningTimeOnQuestion, runningTimeTotal = 0, averageTimeOnQuestion, averageTimeTotal;
                for(DataSnapshot question : dataSnapshot.child("quiz/" + quizCode + "/times").getChildren()) {
                    runningTimeOnQuestion = 0;
                    totalStudents = 0;
                    for(DataSnapshot studentTime : question.getChildren()) {
                        //Log.d(TAG, "current student time: " + studentTime.getValue(Long.class));
                        String studentTimeOnQuestion = String.valueOf(studentTime.getValue(Long.class));
                        runningTimeOnQuestion += Integer.valueOf(studentTimeOnQuestion);
                        totalStudents++;
                    }
                    //Log.d(TAG, "finished question " + totalQuestions);
                    averageTimeOnQuestion = runningTimeOnQuestion / totalStudents;
                    adapter.add("Question " + (Integer.valueOf(question.getKey().substring(1)) + 1) + ": " + averageTimeOnQuestion + " seconds");

                    runningTimeTotal += averageTimeOnQuestion;
                    totalQuestions++;
                }
                averageTimeTotal = runningTimeTotal / totalQuestions;
                //Log.d(TAG, "finished all times, average time " + averageTimeTotal);
                TVaverage.setText(getResources().getString(R.string.averageGrade, averageTimeTotal));

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        timeRef.addListenerForSingleValueEvent(getTime);
    }

    public void getPercent() {
        adapter.clear();

        //final TextView TVaverage = findViewById(R.id.averageQuizGrade);

        DatabaseReference percentRef = database.getReference();
        ValueEventListener getPercent = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int numQuestions = Integer.valueOf(dataSnapshot.child("quiz/" + quizCode + "/num questions").getValue(String.class));

                String questionNum, correctAnswer;
                int totalStudents;
                double runningTotalCorrect, percentCorrect;
                for(int i = 0; i < numQuestions; i++) {
                    questionNum = "q" + i;
                    runningTotalCorrect = 0;
                    totalStudents = 0;
                    correctAnswer = dataSnapshot.child("quiz/" + quizCode + "/questions/" + questionNum + "/correct").getValue(String.class);
                    for (DataSnapshot user: dataSnapshot.child("users").getChildren()) {
                        if(user.hasChild("answers/" + quizCode + "/" + questionNum)) {
                            String answer = user.child("answers/" + quizCode + "/" + questionNum).getValue(String.class);
                            if(answer.equals(correctAnswer)) runningTotalCorrect++;
                            totalStudents++;
                        }
                    }
                    percentCorrect = (runningTotalCorrect / totalStudents)*100;
                    adapter.add("Question " + (i + 1) + " accuracy: " + percentCorrect + "%");
                }

                //TVaverage.setText(getResources().getString(R.string.averageGrade, average));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        percentRef.addListenerForSingleValueEvent(getPercent);
    }

    public void sortListTimeHighLow(){
        getTimes();
        // sort adapter
    }
    public void sortListTimeLowHigh(){
        getTimes();
        // sort adapter
    }
    public void sortListPercentHighLow(){
        getPercent();
        // sort adapter
    }
    public void sortListPercentLowHigh(){
        getPercent();
        // sort adapter
    }
}
