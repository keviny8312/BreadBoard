package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TeacherHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.CNQ_BTN) {
            Intent i = new Intent(TeacherHome.this, createNewQuiz.class);
            startActivity(i);
        }

    }
}
