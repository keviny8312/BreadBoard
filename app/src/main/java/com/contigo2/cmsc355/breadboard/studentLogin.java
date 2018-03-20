package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class studentLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.BcreateNewAccount) {
            // need to carry over email and password fields if they typed it in
            Intent i = new Intent(studentLogin.this, newStudentAccount.class);
            startActivity(i);
        }
    }
}