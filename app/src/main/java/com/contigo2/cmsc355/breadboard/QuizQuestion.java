package com.contigo2.cmsc355.breadboard;

import java.io.Serializable;
import java.util.ArrayList;

public class QuizQuestion implements Serializable{
    private String question;
    private String[] answers;
    private int numAnswers;
    private ArrayList<Integer> correct;

    QuizQuestion() {
        question = "default_question";
        answers = new String[]{"ans1", "ans2", "ans3", "ans4"};
        numAnswers = 4;
        correct = new ArrayList<>();
    }

    QuizQuestion(String q, String a[], int n, ArrayList<Integer> c) {
        question = q;
        answers = a;
        numAnswers = n;
        correct = c;
    }

    public String getQuestion() {
        return question;
    }

    public String getCorrectAnswers() {
        String ans = "";
        for(int i = 0; i < correct.size(); i++) {
            ans += correct.get(i);
        }
        return ans;
    }

    public int getNumAnswers() {
        return numAnswers;
    }

    public String getAnswer(int i) {
        return answers[i];
    }

    public void setQuestion(String q) {
        question = q;
    }

    public void setNumAnswers(int n) {
        numAnswers = n;
    }

    public void setAnswers(String[] a) {
        answers = a;
    }

    public void addCorrect(int c) {
        correct.add(c);
    }

}