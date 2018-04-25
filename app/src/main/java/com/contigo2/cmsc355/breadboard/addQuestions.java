package com.contigo2.cmsc355.breadboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class addQuestions extends AppCompatActivity {

    Quiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_questions);
        quiz = (Quiz)getIntent().getSerializableExtra("passQuiz");
    }

    public void onButtonClick(View v) {
        if(v.getId() == R.id.finishQuiz) {
            if(!emptyQuestion()) {
                addCurrentQuestionToQuiz();
                quiz.generateCode();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                DatabaseReference quizRef = database.getReference("/quiz");
                quiz.submitQuizToDatabase(quizRef);

                Intent i = new Intent(addQuestions.this, quizConfirmation.class);
                i.putExtra("passQuiz", quiz);

                startActivity(i);
            }
        }

        if(v.getId() == R.id.nextQuestion) {
            //TODO dynamic number of answer choices

            if(!emptyQuestion()) {
                addCurrentQuestionToQuiz();

                Intent i = getIntent();
                i.putExtra("passQuiz", quiz);
                finish();
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        }
    }

    public void addCurrentQuestionToQuiz() {
        EditText qText  = findViewById(R.id.questionText);
        EditText aText1 = findViewById(R.id.answerText1);
        EditText aText2 = findViewById(R.id.answerText2);
        EditText aText3 = findViewById(R.id.answerText3);
        EditText aText4 = findViewById(R.id.answerText4);

        String questionText = qText.getText().toString();
        String answerText1 = aText1.getText().toString();
        String answerText2 = aText2.getText().toString();
        String answerText3 = aText3.getText().toString();
        String answerText4 = aText4.getText().toString();
        String[] answers = {answerText1, answerText2, answerText3, answerText4};

        CheckBox cAnsBox1 = findViewById(R.id.checkBox1);
        CheckBox cAnsBox2 = findViewById(R.id.checkBox2);
        CheckBox cAnsBox3 = findViewById(R.id.checkBox3);
        CheckBox cAnsBox4 = findViewById(R.id.checkBox4);

        ArrayList<Integer> correct = new ArrayList<>();
        if(cAnsBox1.isChecked()) correct.add(1);
        if(cAnsBox2.isChecked()) correct.add(2);
        if(cAnsBox3.isChecked()) correct.add(3);
        if(cAnsBox4.isChecked()) correct.add(4);

        QuizQuestion question = new QuizQuestion(questionText, answers, answers.length, correct);
        quiz.addQuestion(question);
    }

    public boolean emptyQuestion() {
        EditText qText  = findViewById(R.id.questionText);
        String question = qText.getText().toString();
        if(question.isEmpty()) {
            AlertDialog.Builder emptyQuestion = new AlertDialog.Builder(addQuestions.this);
            emptyQuestion.setMessage(R.string.emptyQuestionWarning);
            emptyQuestion.setTitle(R.string.alertTitle);
            emptyQuestion.setPositiveButton(R.string.alertOK, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            emptyQuestion.create().show();
            return true;
        }
        return false;
    }
}
