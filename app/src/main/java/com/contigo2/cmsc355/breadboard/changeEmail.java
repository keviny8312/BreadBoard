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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class changeEmail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.submitNewEmailBTN) {
            EditText ETpass = findViewById(R.id.confirmPassEmail);
            EditText ETnewEmail = findViewById(R.id.newEmail);
            EditText ETcnfEmail = findViewById(R.id.confirmNewEmail);

            String pass = ETpass.getText().toString();
            String newEmail = ETnewEmail.getText().toString();
            String cnfEmail = ETcnfEmail.getText().toString();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if(isValidInput(pass, newEmail, cnfEmail, user)) {
                user.updateEmail(newEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(changeEmail.this, "Email changed successfully!", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(changeEmail.this, "Authentication error.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        }
    }

    public boolean isValidInput(String pass, String email, String cnfEmail, FirebaseUser user) {
        if(pass.isEmpty()) {
            Toast.makeText(changeEmail.this, "Please enter your password.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(email.isEmpty()) {
            Toast.makeText(changeEmail.this, "Please enter your email.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(cnfEmail.isEmpty()) {
            Toast.makeText(changeEmail.this, "Please confirm your email.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!email.equals(cnfEmail)) {
            Toast.makeText(changeEmail.this, "Emails must match.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
