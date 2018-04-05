package com.contigo2.cmsc355.breadboard;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class quizConfirmationNumbers extends ListActivity {
    private String quizCode;
    ArrayList<String> listQuizzes = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_confirmation_numbers);
        quizCode = getIntent().getStringExtra("quizCode");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listQuizzes);
        setListAdapter(adapter);

        updateConfirmationNumberList();
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.returnToStats) {
            Intent i = new Intent(quizConfirmationNumbers.this, quizStatistics.class);
            i.putExtra("quizCode", quizCode);
            startActivity(i);
            finish();
        }
    }

    public void updateConfirmationNumberList() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        DatabaseReference quizRef = database.getReference();
        ValueEventListener getConfNums = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot codeSnapshot: dataSnapshot.child("quiz/" + quizCode + "/confCodes").getChildren()) {
                    String studentUID = codeSnapshot.getKey();
                    String studentName = dataSnapshot.child("users/" + studentUID + "/name").getValue(String.class);
                    String studentConfNum = codeSnapshot.getValue(String.class);

                    adapter.add(studentName + " - " + studentConfNum);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        quizRef.addListenerForSingleValueEvent(getConfNums);
    }
}
