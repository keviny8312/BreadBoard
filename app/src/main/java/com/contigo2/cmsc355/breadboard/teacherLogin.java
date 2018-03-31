package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class teacherLogin extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.BcreateNewAccount) {
            // need to carry over email and password fields if they typed it in

            Intent i = new Intent(teacherLogin.this, newTeacherAccount.class);
            EditText userEmail = findViewById(R.id.email_field_teacher);
            EditText userPass = findViewById(R.id.password_field_teacher);
            String email = userEmail.getText().toString();
            String pass = userPass.getText().toString();
            i.putExtra("email", email);
            i.putExtra("pass", pass);
            startActivity(i);
        }

        if(v.getId() == R.id.BteacherLogin) {
            EditText e = findViewById(R.id.email_field_teacher);
            EditText p = findViewById(R.id.password_field_teacher);
            String email = e.getText().toString();
            String pass = p.getText().toString();

            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "signInWithEmail:success");
                                Toast.makeText(teacherLogin.this, "Login successful!",
                                        Toast.LENGTH_SHORT).show();
                                //FirebaseUser user = mAuth.getCurrentUser();

                                Intent i = new Intent(teacherLogin.this, TeacherHome.class);
                                startActivity(i);

                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                //Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(teacherLogin.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }

                            // ...
                        }
                    });
        }
    }
}
