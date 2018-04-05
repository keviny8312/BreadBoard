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

public class studentLogin extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.BcreateNewAccount) {
            Intent i = new Intent(studentLogin.this, newStudentAccount.class);

            EditText e = findViewById(R.id.email_field_student);
            EditText p = findViewById(R.id.password_field_student);

            String email = e.getText().toString();
            String pass = p.getText().toString();
            i.putExtra("email", email);
            i.putExtra("pass", pass);

            startActivity(i);
        }

        if(v.getId() == R.id.BstudentLogin) {
            EditText e = findViewById(R.id.email_field_student);
            EditText p = findViewById(R.id.password_field_student);
            String email = e.getText().toString();
            String pass = p.getText().toString();

            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(studentLogin.this, "Login successful!",
                                        Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();

                                Intent i = new Intent(studentLogin.this, StudentHome.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(studentLogin.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }
}