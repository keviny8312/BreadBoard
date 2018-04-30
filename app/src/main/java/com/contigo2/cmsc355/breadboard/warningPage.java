package com.contigo2.cmsc355.breadboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class warningPage extends AppCompatActivity {
    Button confirmBTN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning_page);
        confirmBTN = findViewById(R.id.warningContinue);
    }
    public void onButtonClick(View view){
        if(view.getId() == R.id.warningContinue){
            super.onBackPressed();
        }
    }

}
