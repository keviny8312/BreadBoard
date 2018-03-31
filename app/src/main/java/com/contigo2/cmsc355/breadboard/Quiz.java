package com.contigo2.cmsc355.breadboard;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;

public class Quiz {
    private String name, dueDate, code;
    private int numQuestions;
    private ArrayList<QuizQuestion> questions = new ArrayList<>();

    Quiz() {
        name = "default_name";
        dueDate = "default_due_date";
        code = "default_code";
        questions = new ArrayList<>();
    }

    Quiz(String n, String dd, String c, int num) {
        name = n;
        dueDate = dd;
        code = c;
        numQuestions = num;
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

    public QuizQuestion getQuestion(int i) {
        if(i > numQuestions) return null;
        else return questions.get(i);
    }

    public void addQuestion(QuizQuestion q) {
        questions.add(q);
        //numQuestions++;
    }

    public void submitQuizToDatabase(DatabaseReference myRef) {
        HashMap<String, String> values = new HashMap<>();
        // quiz info
        myRef = myRef.child("/" + this.getName());
            values.put("name", this.getName());
            values.put("code", this.getCode());
            values.put("due date", this.getDueDate());
            values.put("num questions", Integer.toString(this.getNumQuestions()));
            myRef.setValue(values);
            // question info
            DatabaseReference qRef = myRef.child("/questions");
                for(int i = 0; i < this.getNumQuestions(); i++) {
                    myRef = qRef.child("/q" + i);
                    values = new HashMap<>();
                    values.put("question", this.getQuestion(i).getQuestion());
                    values.put("correct", Integer.toString(this.getQuestion(i).getCorrectAnswer()));
                    myRef.setValue(values);
                    // answer info
                    DatabaseReference aRef = myRef.child("/answers");
                        values = new HashMap<>();
                        for(int k = 0; k < this.getQuestion(i).getNumAnswers(); k++) {
                            values.put("ans" + k, this.getQuestion(i).getAnswer(k));
                        }
                        aRef.setValue(values);
                }
    }

}
