package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class settingsPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.changeEmail) {
            Intent i = new Intent(settingsPage.this, changeEmail.class);
            startActivity(i);
        }

        if(v.getId() == R.id.changePassword) {
            Intent i = new Intent(settingsPage.this, changePassword.class);
            startActivity(i);
        }

        if(v.getId() == R.id.changeFontSize) {
            Intent i = new Intent(settingsPage.this, changeFontSize.class);
            startActivity(i);
        }

        if(v.getId() == R.id.about) {
            Intent i = new Intent(settingsPage.this, AboutApp.class);
            startActivity(i);
        }
    }
}
