package com.example.bao48.studyblank;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bao48.studyblank.algorithm.SpacedRepetitionAlgorithm;
import com.example.bao48.studyblank.database.AppDatabase;
import com.example.bao48.studyblank.models.Flashcard;
import com.example.bao48.studyblank.models.StudyProgress;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

/**
 * Study Activity - Main flashcard study screen
 * Uses the ML-powered spaced repetition algorithm to optimize learning
 */
public class StudyActivity extends AppCompatActivity {

    private TextView tvQuestion;
    private TextView tvAnswer;
    private TextView tvProgress;
    private TextView tvStats;
    private Button btnShowAnswer;
    private Button btnEasy;
    private Button btnMedium;
    private Button btnHard;
    private Button btnAgain;

    private List<Flashcard> flashcards;
    private List<StudyProgress> progressList;
    private int currentIndex = 0;
    private boolean isAnswerVisible = false;

    private AppDatabase database;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        // Initialize views
        tvQuestion = findViewById(R.id.tv_question);
        tvAnswer = findViewById(R.id.tv_answer);
        tvProgress = findViewById(R.id.tv_progress);
        tvStats = findViewById(R.id.tv_stats);
        btnShowAnswer = findViewById(R.id.btn_show_answer);
        btnEasy = findViewById(R.id.btn_easy);
        btnMedium = findViewById(R.id.btn_medium);
        btnHard = findViewById(R.id.btn_hard);
        btnAgain = findViewById(R.id.btn_again);

        database = AppDatabase.getInstance(this);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Load flashcards due for review
        new LoadDueCardsTask().execute();

        // Set up button listeners
        btnShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnswer();
            }
        });

        btnEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateCard(5); // Perfect recall
            }
        });

        btnMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateCard(3); // Correct with difficulty
            }
        });

        btnHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateCard(1); // Incorrect but some memory
            }
        });

        btnAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateCard(0); // Complete blackout
            }
        });
    }

    /**
     * Show the answer with animation
     */
    private void showAnswer() {
        if (!isAnswerVisible) {
            Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            tvAnswer.setVisibility(View.VISIBLE);
            tvAnswer.startAnimation(fadeIn);

            btnShowAnswer.setVisibility(View.GONE);
            btnEasy.setVisibility(View.VISIBLE);
            btnMedium.setVisibility(View.VISIBLE);
            btnHard.setVisibility(View.VISIBLE);
            btnAgain.setVisibility(View.VISIBLE);

            isAnswerVisible = true;
        }
    }

    /**
     * Rate the current card and move to next using ML algorithm
     */
    private void rateCard(final int quality) {
        if (currentIndex < flashcards.size()) {
            new RateCardTask(quality).execute();
        }
    }

    /**
     * Display current flashcard
     */
    private void displayCard() {
        if (currentIndex < flashcards.size()) {
            Flashcard card = flashcards.get(currentIndex);

            tvQuestion.setText(card.getQuestion());
            tvAnswer.setText(card.getAnswer());
            tvAnswer.setVisibility(View.GONE);

            btnShowAnswer.setVisibility(View.VISIBLE);
            btnEasy.setVisibility(View.GONE);
            btnMedium.setVisibility(View.GONE);
            btnHard.setVisibility(View.GONE);
            btnAgain.setVisibility(View.GONE);

            isAnswerVisible = false;

            // Update progress display
            tvProgress.setText("Card " + (currentIndex + 1) + " of " + flashcards.size());

            // Show next review info if available
            if (currentIndex < progressList.size()) {
                StudyProgress progress = progressList.get(currentIndex);
                String stats = "Accuracy: " + String.format("%.1f", progress.getAccuracy()) + "% | " +
                               "Difficulty: " + SpacedRepetitionAlgorithm.getDifficultyLevel(progress.getEaseFactor());
                tvStats.setText(stats);
            }
        } else {
            // Finished all cards
            tvQuestion.setText("Great job! You've completed all due cards.");
            tvAnswer.setVisibility(View.GONE);
            btnShowAnswer.setVisibility(View.GONE);
            btnEasy.setVisibility(View.GONE);
            btnMedium.setVisibility(View.GONE);
            btnHard.setVisibility(View.GONE);
            btnAgain.setVisibility(View.GONE);
            tvProgress.setText("Session complete!");
        }
    }

    /**
     * AsyncTask to load cards due for review
     */
    private class LoadDueCardsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            // Get all cards due for review based on ML algorithm
            progressList = database.studyProgressDao().getDueCards(userId, System.currentTimeMillis());

            flashcards = new ArrayList<>();
            for (StudyProgress progress : progressList) {
                Flashcard card = database.flashcardDao().getById(progress.getFlashcardId());
                if (card != null) {
                    flashcards.add(card);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (flashcards.isEmpty()) {
                tvQuestion.setText("No cards due for review! Check back later.");
                btnShowAnswer.setVisibility(View.GONE);
            } else {
                displayCard();
            }
        }
    }

    /**
     * AsyncTask to rate card and update ML algorithm
     */
    private class RateCardTask extends AsyncTask<Void, Void, String> {
        private int quality;

        RateCardTask(int quality) {
            this.quality = quality;
        }

        @Override
        protected String doInBackground(Void... voids) {
            if (currentIndex < progressList.size()) {
                StudyProgress progress = progressList.get(currentIndex);

                // Update progress using ML algorithm
                progress = SpacedRepetitionAlgorithm.updateProgress(progress, quality);
                database.studyProgressDao().update(progress);

                return SpacedRepetitionAlgorithm.getNextReviewDescription(progress);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String nextReview) {
            if (nextReview != null) {
                Toast.makeText(StudyActivity.this,
                        "Next review: " + nextReview,
                        Toast.LENGTH_SHORT).show();
            }

            currentIndex++;
            displayCard();
        }
    }
}
