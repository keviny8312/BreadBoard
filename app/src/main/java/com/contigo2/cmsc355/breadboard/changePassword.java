package com.contigo2.cmsc355.breadboard;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class changePassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.submitNewPasswordField) {
            EditText EToldPass = findViewById(R.id.oldPasswordField);
            EditText ETnewPass = findViewById(R.id.newPasswordField);
            EditText ETcnfPass = findViewById(R.id.confirmNewPasswordField);

            String oldPass = EToldPass.getText().toString();
            String newPass = ETnewPass.getText().toString();
            String cnfPass = ETcnfPass.getText().toString();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if(isValidInput(oldPass, newPass, cnfPass, user)) {
                user.updatePassword(newPass)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(changePassword.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(changePassword.this, "Authentication error.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        }
    }

    public boolean isValidInput(String oldPass, String newPass, String cnfPass, FirebaseUser user) {
        if(oldPass.isEmpty()) {
            Toast.makeText(changePassword.this, "Please enter your current password.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(newPass.isEmpty()) {
            Toast.makeText(changePassword.this, "Please enter your new password.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(cnfPass.isEmpty()) {
            Toast.makeText(changePassword.this, "Please confirm your new password.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!newPass.equals(cnfPass)) {
            Toast.makeText(changePassword.this, "New passwords must match.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
