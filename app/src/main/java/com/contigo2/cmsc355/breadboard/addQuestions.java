package com.contigo2.cmsc355.breadboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

            addQuestionToQuiz();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            DatabaseReference quizRef = database.getReference("/quiz/" + user.getUid());
            quiz.submitQuizToDatabase(quizRef);

            Intent i = new Intent(addQuestions.this, quizConfirmation.class);
            i.putExtra("passQuiz", quiz);

            startActivity(i);
        }

        if(v.getId() == R.id.nextQuestion) {
            //TODO check for empty questions
            //TODO dynamic number of answer choices

            addQuestionToQuiz();

            Intent i = getIntent();
            i.putExtra("passQuiz", quiz);
            finish();
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
        }
    }

    public void addQuestionToQuiz() {
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

        QuizQuestion question = new QuizQuestion(questionText, answers, answers.length, 1);
        quiz.addQuestion(question);
    }
}
