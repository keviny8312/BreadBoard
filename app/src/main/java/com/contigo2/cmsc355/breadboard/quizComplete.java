package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class quizComplete extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {        // finish quiz and display confirmation code
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_complete);

        String quizCode = getIntent().getStringExtra("quizCode");

        TextView TVquizConfirmation = findViewById(R.id.quizConfirmationField);
        String confirmationCode = generateConfirmationCode();
        TVquizConfirmation.setText(confirmationCode);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference newConfCode = database.getReference("quiz/" + quizCode + "/confCodes");

        Map<String, Object> confCode = new HashMap<>();
        confCode.put(user.getUid(), confirmationCode);
        newConfCode.updateChildren(confCode);

        confCode.clear();

        newConfCode = database.getReference("users/" + user.getUid() + "/confCodes");
        confCode.put(quizCode, confirmationCode);
        newConfCode.updateChildren(confCode);
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.returnToDashboard) {               // return to dashboard
            Intent i = new Intent(quizComplete.this, StudentHome.class);
            startActivity(i);
            finish();
        }
    }

    public String generateConfirmationCode() {                  // generate a confirmation code
        final String alphanum = "0123456789ABCDE";
        final int len = alphanum.length();
        final int CODE_LENGTH = 8;
        Random r = new Random();

        String tempCode = "c_";
        for(int i = 0; i < CODE_LENGTH/2; i++) tempCode += alphanum.charAt(r.nextInt(len));
        tempCode += "-";
        for(int i = 0; i < CODE_LENGTH/2; i++) tempCode += alphanum.charAt(r.nextInt(len));

        return tempCode;
    }
}