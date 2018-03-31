package com.contigo2.cmsc355.breadboard;

import java.io.Serializable;

public class QuizQuestion implements Serializable{
    private String question;
    private String[] answers;
    private int numAnswers;
    private int correct;

    QuizQuestion() {
        question = "default_question";
        answers = new String[]{"ans1", "ans2", "ans3", "ans4"};
        numAnswers = 4;
        correct = 1;    //TODO multiple correct answers (just make this an arraylist or something)
    }

    QuizQuestion(String q, String a[], int n, int c) {
        question = q;
        answers = a;
        numAnswers = n;
        correct = c;
    }

    public String getQuestion() {
        return question;
    }

    public int getCorrectAnswer() {
        return correct;
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

    public void setCorrect(int c) {
        correct = c;
    }

}