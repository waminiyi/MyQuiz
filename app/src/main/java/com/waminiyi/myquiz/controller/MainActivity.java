package com.waminiyi.myquiz.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.waminiyi.myquiz.R;
import com.waminiyi.myquiz.model.User;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView mGreetingTextView;
    private EditText mNameEditText;
    private Button mPlayButton;
    private int mHighScore;
    private static final int GAME_ACTIVITY_REQUEST_CODE = 42;// nombre arbitraire qui servira de code de requête pour la GameActivity
    private static final String SHARED_PREF_USER_INFO = "SHARED_PREF_USER_INFO";//constante qui servina de nom au fichier dans lequel enregistrer les préférences du joueur
    private static final String SHARED_PREF_USER_INFO_NAME = "SHARED_PREF_USER_INFO_NAME";//clé pour stocker le nom du joueur
    private static final String SHARED_PREF_USER_INFO_SCORE = "SHARED_PREF_USER_INFO_SCORE";
    private TextView mHighScoreTextView;
    private User mUser = new User();

    /**
     * surcharge de la méthode onActivityResult() pour récupérer le résultat renvoyé par GameActivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //vérifie le requeste code et que l'activité s'est bien terminé
        if (GAME_ACTIVITY_REQUEST_CODE == requestCode && RESULT_OK == resultCode) {
            //récupération du score
            int score = data.getIntExtra(GameActivity.BUNDLE_EXTRA_SCORE, 0);
            if (score>mHighScore){
                updateHighscore(score);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateScreen();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGreetingTextView = findViewById(R.id.main_textview_greeting);
        mNameEditText = findViewById(R.id.main_edittext_name);
        mPlayButton = findViewById(R.id.main_button_play);
        mHighScoreTextView=findViewById(R.id.main_textview_highscore);

        mNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPlayButton.setEnabled(!s.toString().isEmpty());

            }
        });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUser.setFirstName(mNameEditText.getText().toString());
                getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE)
                        .edit()
                        .putString(SHARED_PREF_USER_INFO_NAME, mUser.getFirstName())
                        .apply();

                Intent gameActivityIntent = new Intent(MainActivity.this, GameActivity.class);
                startActivityForResult(gameActivityIntent, GAME_ACTIVITY_REQUEST_CODE);

            }

        });

    }

    private void updateScreen() {
        String firstName, welcomeText, playButtonText;
        firstName = getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getString(SHARED_PREF_USER_INFO_NAME, null);//récupération du nom s'il y en a un

        if (firstName == null) {
            mPlayButton.setEnabled(false);
            welcomeText = "Bienvenue dans MyQuiz !";
            playButtonText = "Jouer";
            mHighScoreTextView.setVisibility(View.GONE);
        } else {
            welcomeText = "Bon retour " + firstName + " !";
            playButtonText = "Faire mieux";
        }

        mGreetingTextView.setText(welcomeText);
        mPlayButton.setText(playButtonText);
        mNameEditText.setText(firstName);
        loadHighscore();
    }

    private void loadHighscore() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE);
        mHighScore = prefs.getInt(SHARED_PREF_USER_INFO_SCORE, 0);
        mHighScoreTextView.setText("Votre meilleur score: " + mHighScore);
    }

    private void updateHighscore(int newHighScore) {
        mHighScore = newHighScore;
        mHighScoreTextView.setText("Votre meilleur score: " + mHighScore);

        SharedPreferences prefs = getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(SHARED_PREF_USER_INFO_SCORE, mHighScore);
        editor.apply();
    }

}