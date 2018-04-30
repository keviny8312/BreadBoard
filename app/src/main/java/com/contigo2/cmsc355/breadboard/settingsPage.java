package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class settingsPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {        // change account settings
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.changeEmail) {         // change email
            Intent i = new Intent(settingsPage.this, changeEmail.class);
            startActivity(i);
        }

        if(v.getId() == R.id.changePassword) {      // change pass
            Intent i = new Intent(settingsPage.this, changePassword.class);
            startActivity(i);
        }

        if(v.getId() == R.id.changeFontSize) {      // change font, doesn't work
            Intent i = new Intent(settingsPage.this, changeFontSize.class);
            startActivity(i);
        }

        if(v.getId() == R.id.about) {               // view about page
            Intent i = new Intent(settingsPage.this, AboutApp.class);
            startActivity(i);
        }
    }
}
