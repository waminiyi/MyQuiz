package com.waminiyi.myquiz.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.waminiyi.myquiz.R;
import com.waminiyi.myquiz.model.Question;
import com.waminiyi.myquiz.model.QuestionBank;

import java.util.Arrays;
import java.util.Locale;

public class GameActivity extends AppCompatActivity {

    private TextView mQuestionTextView;
    private RadioButton mAnswerButton1;
    private RadioButton mAnswerButton2;
    private RadioButton mAnswerButton3;
    private RadioButton mAnswerButton4;
    private Button mConfirmButton;
    private RadioGroup mRadioGroup;
    private int mGameHighScore;
    private final int mTotalQuestionCount = 4;
    private Question mCurrentQuestion;
    private QuestionBank mQuestionBank;
    private TextView mScoreTextView;
    private TextView mQuestionCountTextView;
    private ProgressBar mProgressBar;
    private TextView mTimerTextView;
    private int progress;
    private int mScore;
    private boolean mAnswered;
    private final int COUNTDOWN_IN_MILLIS = 30000;
    public static final String BUNDLE_EXTRA_SCORE = "BUNDLE_EXTRA_SCORE"; //clé associée au score
    public static final String STATE_SCORE = "BUNDLE_STATE_SCORE";// à utiliser comme clé pour sauvegarder le score dans le bundle
    public static final String STATE_QUESTION_LIST = "BUNDLE_STATE_QUESTION_LIST";
    private CountDownTimer mCountDownTimer;//pour le minuteur
    private long mTimeLeftInMillis;
    private static final String STATE_MILLIS_LEFT = "STATE_MILLIS_LEFT";
    private static final String STATE_ANSWER = "STATE_ANSWER";



    /*méthode qui gère la sauvegarde des données du jeu en cours pour qu'on les récupère en
    cas de destruction de l'activité
    */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SCORE, mScore);
        //outState.putInt(STATE_QUESTION_INDEX, mQuestionBank.getQuestionIndex());
        outState.putParcelable(STATE_QUESTION_LIST, mQuestionBank);
        outState.putLong(STATE_MILLIS_LEFT, mTimeLeftInMillis);
        outState.putBoolean(STATE_ANSWER, mAnswered);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mQuestionTextView = findViewById(R.id.game_activity_textview_question);
        mAnswerButton1 = findViewById(R.id.game_activity_button_1);
        mAnswerButton2 = findViewById(R.id.game_activity_button_2);
        mAnswerButton3 = findViewById(R.id.game_activity_button_3);
        mAnswerButton4 = findViewById(R.id.game_activity_button_4);
        mRadioGroup = findViewById(R.id.game_activity_radio_group);
        mScoreTextView = findViewById(R.id.game_activity_textview_score);
        mConfirmButton = findViewById(R.id.button_confirm_next);
        mQuestionCountTextView = findViewById(R.id.game_activity_textview_question_count);
        mProgressBar = findViewById(R.id.progress_bar);
        mTimerTextView = findViewById(R.id.game_activity_timer_text);

        mProgressBar.setMax(COUNTDOWN_IN_MILLIS);

        if (savedInstanceState != null) {// null si rien n'avait été sauvegardé
            resumeCurrentGame(savedInstanceState);
        } else {
            startNewGame();
        }
        mScoreTextView.setText("Score : " + mScore);

        // Le même listener est utilisé pour les 4 boutons en implémentant View.OnClickListener pour la GameActivity
        // L'id des boutons permettra de distinguer celui qui a été cliqué

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //vérifie que le joueur a bien choisi une réponse avant la validation
                if (!mAnswered) {
                    if (mAnswerButton1.isChecked() || mAnswerButton2.isChecked() || mAnswerButton3.isChecked() || mAnswerButton4.isChecked()) {
                        checkAnswer();
                    } else {
                        Toast.makeText(GameActivity.this, "Merci de choisir une réponse", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //c'est ici qu'il faudrait incrémenter la question
                    mQuestionBank.setQuestionIndex(mQuestionBank.getQuestionIndex() + 1);
                    displayNextQuestion(COUNTDOWN_IN_MILLIS,false);
                }
            }
        });
    }


    private void startNewGame() {
        mScore = 0;
        mScoreTextView.setText("Score : " + mScore);
        mQuestionBank = generateQuestions();
        mQuestionBank.setQuestionIndex(0);
        displayNextQuestion(COUNTDOWN_IN_MILLIS, false);
    }

    private void resumeCurrentGame(Bundle bundle) {
        mQuestionBank = bundle.getParcelable(STATE_QUESTION_LIST);
        mScore = bundle.getInt(STATE_SCORE);
        mTimeLeftInMillis = bundle.getLong(STATE_MILLIS_LEFT);
        mAnswered = bundle.getBoolean(STATE_ANSWER);
        displayNextQuestion(mTimeLeftInMillis, mAnswered);
        if (mAnswered) {
            showAnswer();
        }
    }

    private void displayNextQuestion(long duration, boolean answered) {//ajouter des arguments à cette méthode notamment la durée, si l'on a déja répondu,afin de déterminer s'il faut lancer le compte à rebours
        // mise à jour de l'interface en affichant l'énoncé de la question et les choix possibles
        mAnswerButton1.setTextColor(Color.BLACK);
        mAnswerButton2.setTextColor(Color.BLACK);
        mAnswerButton3.setTextColor(Color.BLACK);
        mAnswerButton4.setTextColor(Color.BLACK);
        mRadioGroup.clearCheck();
        mAnswered=answered;


        if (mQuestionBank.getQuestionIndex() < mTotalQuestionCount) {

            mCurrentQuestion = mQuestionBank.getCurrentQuestion();

            mQuestionTextView.setText(mCurrentQuestion.getQuestionText());
            mAnswerButton1.setText(mCurrentQuestion.getChoiceList().get(0));
            mAnswerButton2.setText(mCurrentQuestion.getChoiceList().get(1));
            mAnswerButton3.setText(mCurrentQuestion.getChoiceList().get(2));
            mAnswerButton4.setText(mCurrentQuestion.getChoiceList().get(3));

            mQuestionCountTextView.setText("Question : " + (mQuestionBank.getQuestionIndex()+1) + "/" + mTotalQuestionCount);
            mConfirmButton.setText("Confimer");
            mTimeLeftInMillis = duration;

            if (mAnswered){
                updateCountDownText();
            }else{

                startCountDown();
            }

        } else {
            endGame();
        }

    }

    //méthode qui gère le démarrage du minuteur
    private void startCountDown() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                progress = (int) mTimeLeftInMillis;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimeLeftInMillis = 0;
                updateCountDownText();
                checkAnswer();
            }
        }.start();
    }

    // méthode qui gère l'affichage du minuteur
    private void updateCountDownText() {
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        //formatage  du minuteur minute:seconde
        String timeFormatted = String.format(Locale.getDefault(), "%02d", seconds);
        mTimerTextView.setText(timeFormatted);
        mProgressBar.setProgress(progress);
        //changement de la couleur du tetxe quand le temps restant est inférieur à 10 secondes
        if (mTimeLeftInMillis < 10000) {
            mTimerTextView.setTextColor(Color.RED);
        } else {
            mTimerTextView.setTextColor(Color.GREEN);
        }
    }


    private void checkAnswer() {
        //mise en place du lien entre le bouton cliqué et l'indice du choix correspondant
        mCountDownTimer.cancel();
        mAnswered = true;
        RadioButton answerSelected = findViewById(mRadioGroup.getCheckedRadioButtonId());
        int answerIndex = mRadioGroup.indexOfChild(answerSelected); //indice du bouton radio cliqué

        if (answerIndex == mCurrentQuestion.getAnswerIndex()) {
            Toast.makeText(this, "Réponse correcte!", Toast.LENGTH_SHORT).show();
            int gain;
            gain = (int) ((300 * mTimeLeftInMillis) / COUNTDOWN_IN_MILLIS);
            if (gain <= 10) {
                mScore = mScore + 10;
            } else {
                mScore = mScore + gain;
            }

            mScoreTextView.setText("Score : " + mScore);
        } else {
            Toast.makeText(this, "Réponse incorrecte!", Toast.LENGTH_SHORT).show();
        }

        showAnswer();

        /*Objet qui un temps d'exécution
        permet de faire patienter l'affichage de la question suivante le temps que le toaast disparaisse
         */
        /*new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                //code dont l'exécution doit atteindre la fin du toast
                displayNextQuestion(COUNTDOWN_IN_MILLIS);
            }
        }, 3_000); // temps d'exécution*/

    }

    private void showAnswer() {
        mAnswerButton1.setTextColor(Color.RED);
        mAnswerButton2.setTextColor(Color.RED);
        mAnswerButton3.setTextColor(Color.RED);
        mAnswerButton4.setTextColor(Color.RED);

        switch (mCurrentQuestion.getAnswerIndex()) {
            case 0:
                mAnswerButton1.setTextColor(Color.GREEN);
                break;
            case 1:
                mAnswerButton2.setTextColor(Color.GREEN);
                break;
            case 2:
                mAnswerButton3.setTextColor(Color.GREEN);
                break;
            case 3:
                mAnswerButton4.setTextColor(Color.GREEN);
                break;
        }
        if (mQuestionBank.getQuestionIndex()+1 < mTotalQuestionCount) {
            mConfirmButton.setText("Suivant");
        } else {
            mConfirmButton.setText("Terminer");
        }
    }

    private void endGame() {
        if (mScore > mGameHighScore) {
            mGameHighScore = mScore;
        }
        //plus de questions? affichage du score final et fin du jeu
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.gameEnd) // définition du titre de la boîte de dialogue
                .setMessage("Votre score est : " + mScore) // définition du texte à afficher dans la boite de dialogue
                .setPositiveButton(R.string.endGame, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //création d'un Intent pour enregistrer le score et le récupérer dans MainActivity
                        Intent intent = new Intent();
                        intent.putExtra(BUNDLE_EXTRA_SCORE, mGameHighScore);
                        setResult(RESULT_OK, intent);// indique au système que l'activité s'est terminée correctement
                        finish();
                    }
                })
                .setNegativeButton(R.string.newGame, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startNewGame();
                    }
                });
        builder.create();
        builder.show();
    }

    private QuestionBank generateQuestions() {
        // création des questions
        Question question1 = new Question(
                "Quel animal est le roitelet?",
                Arrays.asList("Un canidé", "Un poisson", "Un félin", "Un oiseau"),
                3
        );

        Question question2 = new Question(
                "À quelle famille animale appartient le zébu?",
                Arrays.asList("Les batraciens", "Les bovidés", "Les insectes", "Les reptiles"),
                1
        );

        Question question3 = new Question(
                "Quel animal est à l’honneur dans le film «Jappeloup»?",
                Arrays.asList("Un renard", "Un serpent", "Un tigre", "Un cheval"),
                3
        );
        Question question4 = new Question(
                "Quel animal ne vole pas?",
                Arrays.asList("Le pingouin", "La cigogne", "Le manchot", "Le pélican"),
                2
        );

        Question question5 = new Question(
                "Qu’étudie un arachnologue?",
                Arrays.asList("Les rats", "Les serpents", "Les araignées", "Les aigles"),
                2
        );

        Question question6 = new Question(
                "Quel est le cri de la brebis?",
                Arrays.asList("Le beuglement", "Le bêlement", "Le jappement", "Le vagissement"),
                1
        );


        Question question7 = new Question(
                "Quel animal a pour symbolisme la grâce ?",
                Arrays.asList("Le flamant", "Le paon", "Le cygne", "Le kangourou"),
                2
        );

        Question question8 = new Question(
                "Qu’est-ce qu’une pipistrelle?",
                Arrays.asList("Une brebis", "Une chauve-souris", "Une girafe", "Une otarie"),
                1
        );

        Question question9 = new Question(
                "Quel animal est associé à Panurge dans une célèbre expression?",
                Arrays.asList("Le renard", "Un mouton", "Le lion", "Le zèbre"),
                1
        );


        Question question10 = new Question(
                "De quelle race de chien est Bill, fidèle compagnon de Boule?",
                Arrays.asList("Un caniche", "Un beagle", "Un cocker", "Un labrador"),
                2
        );

        Question question11 = new Question(
                "Quel nom porte l’habitat du castor?",
                Arrays.asList("Une hutte", "Une tanière", "Une aire", "Un terrier"),
                0
        );

        Question question12 = new Question(
                "Quel animal brame?",
                Arrays.asList("Le cerf", "Le canard", "La pintade", "Le loup"),
                0
        );

        Question question13 = new Question(
                "Quel animal est un baudet ?",
                Arrays.asList("Un âne", "Un singe", "Un chameau", "Un bœuf"),
                0
        );

        Question question14 = new Question(
                "Quel est le cri de la cigale et de la cigogne?",
                Arrays.asList("Elles craquettent", "Elles stridulent", "Elles criaillent", "Elles zinzinulent"),
                0
        );

        Question question15 = new Question(
                "Complétez cette expression «Faire des yeux de… frits»?",
                Arrays.asList("Serpent", "Hibou", "Merlan", "Cigale"),
                2
        );

        Question question16 = new Question(
                "Combien d’équipes étaient présentes lors de l’Euro 2016 de foot ?",
                Arrays.asList("12", "16", "20", "24"),
                3
        );

        Question question17 = new Question(
                "Quel club de foot anglais joue dans l’enceinte de l’Emirates Stadium ?",
                Arrays.asList("Manchester United", "Arsenal FC", "Sunderland AFC", "Manchester City"),
                1
        );

        Question question18 = new Question(
                "Parmi ces villes françaises, laquelle n’a pas acceuilli de match de l’Euro 2016 ?",
                Arrays.asList("Nice", "Saint-Étienne", "Nantes", "Toulouse"),
                2
        );

        Question question19 = new Question(
                "Quel footballeur est surnommé la Puce ?",
                Arrays.asList("Mathieu Valbuena", "Andres Iniesta", "Carlos Tevez", "Lionel Messi"),
                3
        );

        Question question20 = new Question(
                "Quelles sont les 3 équipes figurant dans le groupe de la France pour l’Euro 2016 de foot ?",
                Arrays.asList("Autriche/Hongrie/Islande", "Roumanie/Albanie/Suisse", "Angleterre/Russie/Slovaquie", "Croatie/République tchèque/Turquie"),
                1
        );

        Question question21 = new Question(
                "En quelle année Zinédine Zidane a-t-il pris sa retraite en tant que joueur ?",
                Arrays.asList("1999", "2002", "2006", "2008"),
                2
        );

        Question question22 = new Question(
                "Quelle équipe de foot joue dans l’enceinte du stade de Santiago Bernabeu ?",
                Arrays.asList("Le FC Porto", "Le Málaga CF", "Le Real Madrid", "Le FC Barcelone"),
                2
        );

        Question question23 = new Question(
                "Quel est le nom du sélectionneur des Bleus en 2016 ?",
                Arrays.asList("Michel Platini", "Didier Deschamps", "Laurent Blanc", "Claude Puel"),
                1
        );

        Question question24 = new Question(
                "De quel pays africain, les Éléphants constituent-ils le nom de l’équipe de foot ?",
                Arrays.asList("Ghana", "Tunisie", "Cameroun", "Côte d’Ivoire"),
                3
        );

        Question question25 = new Question(
                "Quelle équipe a gagné l'Euro de 2012 ?",
                Arrays.asList("Espagne", "Allemagne", "Italie", "Angleterre"),
                0
        );

        Question question26 = new Question(
                "Qui a remplacé Sepp Blatter à la tête de la Fifa en 2016 ?",
                Arrays.asList("Ali ben Al Hussein", "Gianni Infantino", "Issa Hayatou", "Michel Platini"),
                1
        );

        Question question27 = new Question(
                "Quel est le nom du stade de la ville de Bordeaux qui a accueilli des matchs de l’Euro 2016 ?",
                Arrays.asList("Allianz Riviera", "Stade Geoffroy-Guichard", "Stade Chaban-Delmas", "Matmut Atlantique"),
                3
        );

        Question question28 = new Question(
                "Quelle équipe a éliminé le PSG en quarts de finale de la Ligue des Champions 2016 ?",
                Arrays.asList("Manchester City", "Chelsea", "Athletico Madrid", "Borussia Dortmund"),
                0
        );

        Question question29 = new Question(
                "Quel est le nom de la mascotte de l’Euro 2016 ?",
                Arrays.asList("Péno", "Super Victor", "Goalix", "Footix"),
                1
        );

        Question question30 = new Question(
                "Quel footballeur a été élu Ballon d’or 2015 ?",
                Arrays.asList("Cristiano Ronaldo", "Neymar", "Lionel Messi", "Thomas Müller"),
                2
        );

        // renvoi de la banque de question
        return new QuestionBank(Arrays.asList(question1, question2, question3, question4, question5, question6,
                question7, question8, question9, question10, question11, question12, question13, question14, question15,
                question16, question17, question18, question19, question20, question21, question22, question23, question24,
                question25, question26, question27, question28, question29, question30));


    }


}