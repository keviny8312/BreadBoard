package com.contigo2.cmsc355.breadboard;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class StudentHome extends ListActivity {

    ArrayList<String> listQuizzes = new ArrayList<>();
    ArrayAdapter<String> adapter;
    public final String TAG = "StudentHome";

    @Override
    protected void onCreate(Bundle savedInstanceState) {        // student home page
        super.onCreate(savedInstanceState);
        if(getIntent().hasExtra("removeQuiz")) adapter.remove(getIntent().getStringExtra("removeQuiz"));

        setContentView(R.layout.activity_student_home);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listQuizzes);
        setListAdapter(adapter);

        final String initCode = getIntent().getStringExtra("quizCode");
        if(initCode != null) {
            final Map<String, Object> addQuiz = new HashMap<>();

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            final FirebaseUser user = mAuth.getCurrentUser();
            final FirebaseDatabase qDatabase = FirebaseDatabase.getInstance();
            DatabaseReference nameRef = qDatabase.getReference("quiz/" + initCode + "/name");
            ValueEventListener getName = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    addQuiz.put(initCode, dataSnapshot.getValue());
                    DatabaseReference quizRef = qDatabase.getReference("users/" + user.getUid() + "/quizzes");
                    quizRef.updateChildren(addQuiz);
                    adapter.add(dataSnapshot.getValue().toString());
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            nameRef.addValueEventListener(getName);
        }

        ListView list = findViewById(android.R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                final FirebaseUser user = mAuth.getCurrentUser();
                final int pos = position;

                DatabaseReference quizRef = database.getReference();
                ValueEventListener getQuizzes = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int i = 0;
                        for (DataSnapshot codeSnapshot: dataSnapshot.child("users/" + user.getUid() + "/quizzes").getChildren()) {
                            if(i == pos) {
                                Log.d(TAG, "entry " + pos + " clicked");
                                String quizCode = codeSnapshot.getKey();

                                Calendar currentDate = Calendar.getInstance();
                                int month = currentDate.get(Calendar.MONTH) + 1;
                                int day   = currentDate.get(Calendar.DAY_OF_MONTH);
                                int year  = currentDate.get(Calendar.YEAR);
                                Log.d(TAG, "date  " + day + " " + month + " " + year);
                                String dueDate = dataSnapshot.child("quiz/" + quizCode + "/due date").getValue(String.class);
                                int dueMonth = Integer.valueOf(dueDate.substring(0, dueDate.indexOf('-')));
                                int dueDay = Integer.valueOf(dueDate.substring(dueDate.indexOf('-') + 1, dueDate.lastIndexOf('-')));
                                int dueYear = Integer.valueOf(dueDate.substring(dueDate.lastIndexOf('-') + 1));
                                Log.d(TAG, "due date  " + dueDay + " " + dueMonth + " " + dueYear);
                                if(dueYear < year || (dueYear == year && dueMonth < month) || (dueYear == year && dueMonth == month && dueDay < day)) {
                                    Log.d(TAG, "past due!");
                                    if(!dataSnapshot.child("users/" + user.getUid() + "finished").hasChild(quizCode)) {
                                        Log.d(TAG, "wasn't finished");
                                        Map<String, Object> zero = new HashMap<>();
                                        zero.put(user.getUid(), "0");
                                        DatabaseReference zeroGrade = FirebaseDatabase.getInstance().getReference("quiz/" + quizCode + "/grades/");
                                        zeroGrade.updateChildren(zero);

                                        DatabaseReference addFinished = FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/finished/" + quizCode);
                                        Map<String, Object> finished = new HashMap<>();
                                        finished.put(quizCode, "0");
                                        addFinished.updateChildren(finished);

                                        Intent toPostQuizInfo = new Intent(StudentHome.this, postQuizInfo.class);
                                        toPostQuizInfo.putExtra("quizCode", quizCode);
                                        startActivity(toPostQuizInfo);
                                        break;
                                    }
                                }

                                else if(dataSnapshot.child("users/" + user.getUid() + "/finished").hasChild(quizCode)) {
                                    Intent toPostQuizInfo = new Intent(StudentHome.this, postQuizInfo.class);
                                    toPostQuizInfo.putExtra("quizCode", quizCode);
                                    startActivity(toPostQuizInfo);
                                    break;
                                }
                                else {
                                    Intent toQuizInfo = new Intent(StudentHome.this, preQuizInfo.class);
                                    toQuizInfo.putExtra("quizCode", quizCode);
                                    startActivity(toQuizInfo);
                                    break;
                                }
                            }
                            else i++;
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };
                quizRef.addListenerForSingleValueEvent(getQuizzes);

            }
        });

        updateQuizList();
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.CNQ_BTN) {                 // add new quiz using quiz code
            EditText ETnewCode = findViewById(R.id.newQuizCode);
            String newCode = ETnewCode.getText().toString();
            enterNewQuizCode(newCode);
        }
        if(v.getId() == R.id.Bsettings) {               // go to settings
            Intent i = new Intent(StudentHome.this, settingsPage.class);
            startActivity(i);
        }
    }

    public void updateQuizList() {                      // update list with changes
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        DatabaseReference quizRef = database.getReference("/users/" + user.getUid() + "/quizzes");
        ValueEventListener getQuizzes = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot codeSnapshot: dataSnapshot.getChildren()) {
                    String quizCode = codeSnapshot.getValue(String.class);
                    adapter.add(quizCode);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        quizRef.addListenerForSingleValueEvent(getQuizzes);
    }

    public void enterNewQuizCode(String code) {         // enter new quiz code into list
        final String tempCode = code;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        final Map<String, Object> addQuiz = new HashMap<>();

        final DatabaseReference quizRef = database.getReference("quiz/" + code + "/name");
        ValueEventListener getQuizzes = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                addQuiz.put(tempCode, dataSnapshot.getValue());
                DatabaseReference quizRef = database.getReference("users/" + user.getUid() + "/quizzes");
                quizRef.updateChildren(addQuiz);
                adapter.add(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        quizRef.addListenerForSingleValueEvent(getQuizzes);
    }

}
