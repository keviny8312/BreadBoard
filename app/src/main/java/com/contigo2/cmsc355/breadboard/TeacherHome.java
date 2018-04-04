package com.contigo2.cmsc355.breadboard;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listQuizzes = new ArrayList<>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    //RECORDING HOW MANY TIMES THE BUTTON HAS BEEN CLICKED
    int numQuizzes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listQuizzes);
        setListAdapter(adapter);
        ListView list = findViewById(android.R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(TeacherHome.this, "button " + position + " pressed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        updateQuizList();
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.CNQ_BTN) {
            Intent i = new Intent(TeacherHome.this, createNewQuiz.class);
            startActivity(i);
        }
        if(v.getId() == R.id.testButton) {
            updateQuizList();
        }






        //LayoutInflater inflater = TeacherHome.this.getLayoutInflater();
        //View rowView = inflater.inflate
        //deleteButton = (Button)rowView.findViewById(R.id.delete_bn);


    }

    //METHOD WHICH WILL HANDLE DYNAMIC INSERTION
    public void updateQuizList() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        /*
        DatabaseReference quizNumRef = database.getReference("/users/" + user.getUid() + "/quizzes");
        ValueEventListener getNumQuizzes = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numQuizzes = (int)dataSnapshot.getChildrenCount();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // fndbsgyivfeksh
            }
        };
        quizNumRef.addListenerForSingleValueEvent(getNumQuizzes);
        */

        // above isnt even needed anymore because the next listener doesnt need to know length
        // im gonna die by the way

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
            public void onCancelled(DatabaseError databaseError) {
                // fndbsgyivfeksh
            }
        };
        quizRef.addListenerForSingleValueEvent(getQuizzes);

        // i dont know how i managed to make this work but it does. fuck you
    }

}
