package com.contigo2.cmsc355.breadboard;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
    ArrayList<String> listQuizzes = new ArrayList<>();
    ArrayAdapter<String> adapter;
    Spinner sortByMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_statistics);
        quizCode = getIntent().getStringExtra("quizCode");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listQuizzes);
        setListAdapter(adapter);

        updateGradesList();
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

    public void updateGradesList() {
        final TextView TVaverage = findViewById(R.id.averageQuizGrade);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        final Resources res = getResources();

        DatabaseReference quizRef = database.getReference();
        ValueEventListener getGrades = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int total = 0;
                double runningTotal = 0, average = 0;
                for (DataSnapshot codeSnapshot: dataSnapshot.child("quiz/" + quizCode + "/grades").getChildren()) {
                    String studentUID = codeSnapshot.getKey();
                    String studentName = dataSnapshot.child("users/" + studentUID + "/name").getValue(String.class);
                    String studentGrade = codeSnapshot.getValue(String.class);

                    runningTotal += Float.valueOf(studentGrade);

                    adapter.add(studentName + " - " + studentGrade);
                    total++;
                }
                average = runningTotal / total;
                TVaverage.setText(res.getString(R.string.averageGrade, average));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        quizRef.addListenerForSingleValueEvent(getGrades);
    }
    public void sortLista(){

    }
    public void sortListb(){

    }
    public void sortListc(){

    }
    public void sortListd(){

    }
}
