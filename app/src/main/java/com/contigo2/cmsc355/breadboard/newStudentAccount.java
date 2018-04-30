package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class newStudentAccount extends AppCompatActivity {
    private boolean valid;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_student_account);

        if(getIntent().hasExtra("email")) {
            EditText e = findViewById(R.id.student_email_nac);
            e.setText(getIntent().getStringExtra("email"));
        }
        if(getIntent().hasExtra("pass")) {
            EditText p = findViewById(R.id.student_password_nac);
            p.setText(getIntent().getStringExtra("pass"));
        }

    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.cna_student) {
            //create acct in firebase
            EditText n = findViewById(R.id.student_name_nac);
            EditText e = findViewById(R.id.student_email_nac);
            EditText p = findViewById(R.id.student_password_nac);
            EditText q = findViewById(R.id.student_init_quiz);

            final String name = n.getText().toString().trim();
            final String email = e.getText().toString().trim();
            final String pass = p.getText().toString().trim();
            final String group = "STUDENT";
            final String quiz = q.getText().toString().trim();

            if(validInput()) {
                mAuth = FirebaseAuth.getInstance();
                Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(newStudentAccount.this, "Account created!",
                                            Toast.LENGTH_SHORT).show();

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference ref = database.getReference("users/" + user.getUid());
                                    ref.setValue(new User(name, email, group));

                                    Intent i = new Intent(newStudentAccount.this, StudentHome.class);
                                    i.putExtra("quizCode", quiz);
                                    startActivity(i);

                                } else {
                                    Toast.makeText(newStudentAccount.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });
            }


        }

    }

    public boolean validInput() {
        EditText n = findViewById(R.id.student_name_nac);
        EditText e = findViewById(R.id.student_email_nac);
        EditText p = findViewById(R.id.student_password_nac);
        EditText q = findViewById(R.id.student_init_quiz);

        String name = n.getText().toString().trim();
        String email = e.getText().toString().trim();
        String pass = p.getText().toString().trim();
        String quiz = q.getText().toString().trim();

        if(name.isEmpty()) {
            Toast.makeText(newStudentAccount.this, "Please enter your name.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(email.isEmpty()) {
            Toast.makeText(newStudentAccount.this, "Please enter your email.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(pass.isEmpty()) {
            Toast.makeText(newStudentAccount.this, "Please enter a password.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(quiz.isEmpty()) {
            Toast.makeText(newStudentAccount.this, "Please enter a quiz code.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }


}
