package com.waminiyi.myquiz.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//classe qui définit un ensemble de quetions
public class QuestionBank implements Parcelable {//parcelable pour sauvegarder des données

    private List<Question> mQuestionList;
    private int mQuestionIndex;

    public QuestionBank(List<Question> questionList) {
        mQuestionList = questionList;
        Collections.shuffle(mQuestionList); //mélange les questions avant de les stocker

    }

    protected QuestionBank(Parcel in) {
        mQuestionIndex = in.readInt();
        mQuestionList=in.readArrayList(Question.class.getClassLoader());
    }

    public static final Creator<QuestionBank> CREATOR = new Creator<QuestionBank>() {
        @Override
        public QuestionBank createFromParcel(Parcel in) {
            return new QuestionBank(in);
        }

        @Override
        public QuestionBank[] newArray(int size) {
            return new QuestionBank[size];
        }
    };

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mQuestionIndex);

        dest.writeList(mQuestionList);
    }
}
