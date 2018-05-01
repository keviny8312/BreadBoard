package com.contigo2.cmsc355.breadboard;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TeacherHome extends ListActivity {

    ArrayList<String> listQuizzes = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {                // teacher homepage
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listQuizzes);
        setListAdapter(adapter);
        ListView list = findViewById(android.R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                final int pos = position;

                DatabaseReference quizRef = database.getReference("/users/" + user.getUid() + "/quizzes");
                ValueEventListener getQuizzes = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int i = 0;
                        for (DataSnapshot codeSnapshot: dataSnapshot.getChildren()) {
                            if(i == pos) {
                                String quizCode = codeSnapshot.getKey();

                                Intent toQuizInfo = new Intent(TeacherHome.this, QuizInformation.class);
                                toQuizInfo.putExtra("quizCode", quizCode);
                                startActivity(toQuizInfo);
                                break;
                            }
                            else i++;
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };
                quizRef.addValueEventListener(getQuizzes);


            }
        });

        updateQuizList();
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.CNQ_BTN) {             // go to create new quiz
            Intent i = new Intent(TeacherHome.this, createNewQuiz.class);
            startActivity(i);
            finish();
        }
        if(v.getId() == R.id.Bsettings) {           // go to settings
            Intent i = new Intent(TeacherHome.this, settingsPage.class);
            startActivity(i);
        }
        if(v.getId() == R.id.logout) {
            Intent i = new Intent(TeacherHome.this, LoginActivity.class);
            finishAffinity();
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(TeacherHome.this, "You have been logged out.", Toast.LENGTH_SHORT).show();
            startActivity(i);
            finish();
        }
    }

    public void updateQuizList() {                  // update quiz list with current quizzes
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        DatabaseReference quizRef = database.getReference("users/" + user.getUid() + "/quizzes");
        ValueEventListener getQuizzes = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot codeSnapshot: dataSnapshot.getChildren()) {
                    String quizName = codeSnapshot.getValue(String.class);
                    adapter.add(quizName);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        quizRef.addListenerForSingleValueEvent(getQuizzes);
    }

}
