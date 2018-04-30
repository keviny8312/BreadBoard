package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class newTeacherAccount extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_teacher_account);

        if(getIntent().hasExtra("email")) {
            EditText e = findViewById(R.id.teacher_email_nac);
            e.setText(getIntent().getStringExtra("email"));
        }
        if(getIntent().hasExtra("pass")) {
            EditText p = findViewById(R.id.teacher_password_nac);
            p.setText(getIntent().getStringExtra("pass"));
        }

    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.cna_teacher) {
            EditText n = findViewById(R.id.teacher_name_nac);
            EditText e = findViewById(R.id.teacher_email_nac);
            EditText p = findViewById(R.id.teacher_password_nac);
            final String name = n.getText().toString();
            final String email = e.getText().toString();
            final String group = "TEACHER";
            final String pass = p.getText().toString();
            final FirebaseDatabase database = FirebaseDatabase.getInstance();

            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(newTeacherAccount.this, "Account created!",
                                        Toast.LENGTH_SHORT).show();

                                FirebaseUser user = mAuth.getCurrentUser();
                                DatabaseReference ref = database.getReference("users/" + user.getUid());
                                ref.setValue(new User(name, email, group));

                                Intent i = new Intent(newTeacherAccount.this, TeacherHome.class);
                                startActivity(i);

                            }
                            else {
                                Toast.makeText(newTeacherAccount.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
}
