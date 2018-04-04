package com.contigo2.cmsc355.breadboard;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Quiz implements Serializable{
    private String name, dueDate, code, time, className;
    private int numQuestions;
    private ArrayList<QuizQuestion> questions = new ArrayList<>();

    Quiz() {
        this.name = "default_name";
        this.dueDate = "default_due_date";
        this.code = "default_code";
        this.className = "default_class";
        this.numQuestions = 0;
        this.time = "0";
    }

    Quiz(String name, String dueDate) {
        this.name = name;
        this.dueDate = dueDate;
        this.code = "default_code";
        this.className = "default_class";
        this.numQuestions = 0;
        this.time = "0";
    }

    Quiz(String name, String dueDate, String className, String time) {
        this.name = name;
        this.dueDate = dueDate;
        this.code = "default_code";
        this.className = className;
        this.numQuestions = 0;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getCode() {
        return code;
    }

    public int getNumQuestions() {
        return numQuestions;
    }

    public String getTime() {
        return time;
    }

    public String getClassName() {
        return className;
    }

    public QuizQuestion getQuestion(int i) {
        if(i > numQuestions) return null;
        else return questions.get(i);
    }

    public void addQuestion(QuizQuestion q) {
        questions.add(q);
        numQuestions++;
    }

    public void submitQuizToDatabase(DatabaseReference myRef) {
        Map<String, Object> values = new HashMap<>();

        myRef = myRef.child("/" + this.getCode());
            values.put("name", this.getName());
            values.put("code", this.getCode());
            values.put("due date", this.getDueDate());
            values.put("time", this.getTime());
            values.put("className", this.getClassName());
            values.put("num questions", Integer.toString(this.getNumQuestions()));
            myRef.updateChildren(values);
            // question info
            DatabaseReference qRef = myRef.child("/questions");
                for(int i = 0; i < this.getNumQuestions(); i++) {
                    myRef = qRef.child("/q" + i);
                    values.clear();
                    values.put("question", this.getQuestion(i).getQuestion());
                    values.put("correct", this.getQuestion(i).getCorrectAnswers());
                    myRef.updateChildren(values);
                    // answer info
                    DatabaseReference aRef = myRef.child("/answers");
                        values.clear();
                        for(int k = 0; k < this.getQuestion(i).getNumAnswers(); k++) {
                            values.put("ans" + k, this.getQuestion(i).getAnswer(k));
                        }
                        aRef.updateChildren(values);
                }
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDatabase qDatabase = FirebaseDatabase.getInstance();
        DatabaseReference quizRef = qDatabase.getReference("users/" + user.getUid() + "/quizzes");

        Map<String, Object> addQuiz = new HashMap<>();
        addQuiz.put(this.getCode(), this.getName());
        quizRef.updateChildren(addQuiz);
    }

    public void generateCode() {
        final String alphanum = "0123456789ABCDE";
        final int len = alphanum.length();
        final int CODE_LENGTH = 8;
        Random r = new Random();

        String tempCode = "q_";
        for(int i = 0; i < CODE_LENGTH; i++) {
            tempCode += alphanum.charAt(r.nextInt(len));
        }
        this.code = tempCode;
    }

}
