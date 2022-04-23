package com.waminiyi.myquiz.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

//classe qui définit l'objet question
public class Question implements Parcelable {
    private final String mQuestionText;
    private final List<String> mChoiceList;
    private final int mAnswerIndex;

    public Question(String questionText, List<String> choiceList, int answerIndex) {
        mQuestionText = questionText;
        mChoiceList = choiceList;
        mAnswerIndex = answerIndex;
    }

    protected Question(Parcel in) {
        mQuestionText = in.readString();
        mChoiceList = in.createStringArrayList();
        mAnswerIndex = in.readInt();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    public String getQuestionText() {
        return mQuestionText;
    }

    public List<String> getChoiceList() {
        return mChoiceList;
    }

    public int getAnswerIndex() {
        return mAnswerIndex;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mQuestionText);
        dest.writeStringList(mChoiceList);
        dest.writeInt(mAnswerIndex);
    }
}
