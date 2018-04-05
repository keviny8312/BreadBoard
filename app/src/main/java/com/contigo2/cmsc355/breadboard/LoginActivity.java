package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    TextView timerField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.BteacherLogin) {
            Intent i = new Intent(LoginActivity.this, teacherLogin.class);
            startActivity(i);
        }
        if(v.getId() == R.id.BstudentLogin) {
            Intent i = new Intent(LoginActivity.this, studentLogin.class);
            startActivity(i);
        }
    }
}
