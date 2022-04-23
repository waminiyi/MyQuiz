package com.waminiyi.myquiz.model;

import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//classe qui définit un ensemble de quetions
public class QuestionBank  {//parcelable pour sauvegarder des données

    private List<Question> mQuestionList;
    private int mQuestionIndex;

    public QuestionBank(List<Question> questionList) {
        mQuestionList = questionList;
        Collections.shuffle(mQuestionList); //mélange les questions avant de les stocker

    }

    public ArrayList<? extends Parcelable> getQuestionList() {
        return (ArrayList<? extends Parcelable>) mQuestionList;
    }

    public void setQuestionList(List<Question> questionList) {
        mQuestionList = questionList;
    }

    public int getQuestionIndex() {
        return mQuestionIndex;
    }

    public void setQuestionIndex(int questionIndex) {
        mQuestionIndex = questionIndex;
    }

    public Question getCurrentQuestion() {
        return mQuestionList.get(mQuestionIndex);
    }

    public Question getNextQuestion() {
        mQuestionIndex++; //passe à la question suivante
        return getCurrentQuestion();
    }


}
