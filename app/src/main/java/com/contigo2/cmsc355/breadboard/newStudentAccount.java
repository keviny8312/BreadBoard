package com.contigo2.cmsc355.breadboard;

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

public class newStudentAccount extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_student_account);

        if(getIntent().hasExtra("email")) {
            EditText e = (EditText)findViewById(R.id.student_email_nac);
            e.setText(getIntent().getStringExtra("email"));
        }
        if(getIntent().hasExtra("pass")) {
            EditText p = (EditText)findViewById(R.id.student_password_nac);
            p.setText(getIntent().getStringExtra("pass"));
        }

    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.cna_student) {
            //create acct in firebase
            EditText e = (EditText)findViewById(R.id.student_email_nac);
            EditText p = (EditText)findViewById(R.id.student_password_nac);
            String email = e.getText().toString();
            String pass = e.getText().toString();

            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "createUserWithEmail:success");
                                Toast.makeText(newStudentAccount.this, "Account created!",
                                        Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(newStudentAccount.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }

                            // ...
                        }
                    });
        }

    }

}
