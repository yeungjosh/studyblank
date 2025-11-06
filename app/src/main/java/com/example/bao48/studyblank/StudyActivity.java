package com.example.bao48.studyblank;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;

import java.lang.ref.WeakReference;
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

        // Check if user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        userId = currentUser.getUid();

        // Check if studying a specific deck or all due cards
        Intent intent = getIntent();
        int deckId = intent.getIntExtra("DECK_ID", -1);

        // Load flashcards due for review
        new LoadDueCardsTask(this, database, userId, deckId).execute();

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
        if (currentIndex < flashcards.size() && currentIndex < progressList.size()) {
            StudyProgress progress = progressList.get(currentIndex);
            new RateCardTask(this, database, progress, quality, currentIndex).execute();
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
     * Static inner class with WeakReference to prevent memory leaks
     */
    private static class LoadDueCardsTask extends AsyncTask<Void, Void, LoadDueCardsResult> {
        private final WeakReference<StudyActivity> activityRef;
        private final AppDatabase database;
        private final String userId;
        private final int deckId;

        LoadDueCardsTask(StudyActivity activity, AppDatabase database, String userId, int deckId) {
            this.activityRef = new WeakReference<>(activity);
            this.database = database;
            this.userId = userId;
            this.deckId = deckId;
        }

        @Override
        protected LoadDueCardsResult doInBackground(Void... voids) {
            List<StudyProgress> progressList;

            // Get cards due for review - either for specific deck or all decks
            if (deckId != -1) {
                progressList = database.studyProgressDao().getDueCardsByDeck(userId, deckId, System.currentTimeMillis());
            } else {
                progressList = database.studyProgressDao().getDueCards(userId, System.currentTimeMillis());
            }

            List<Flashcard> flashcards = new ArrayList<>();
            for (StudyProgress progress : progressList) {
                Flashcard card = database.flashcardDao().getById(progress.getFlashcardId());
                if (card != null) {
                    flashcards.add(card);
                }
            }

            return new LoadDueCardsResult(flashcards, progressList);
        }

        @Override
        protected void onPostExecute(LoadDueCardsResult result) {
            StudyActivity activity = activityRef.get();
            if (activity != null && !activity.isFinishing()) {
                activity.flashcards = result.flashcards;
                activity.progressList = result.progressList;

                if (result.flashcards.isEmpty()) {
                    activity.tvQuestion.setText("No cards due for review! Check back later.");
                    activity.btnShowAnswer.setVisibility(View.GONE);
                } else {
                    activity.displayCard();
                }
            }
        }
    }

    /**
     * Result holder for LoadDueCardsTask
     */
    private static class LoadDueCardsResult {
        final List<Flashcard> flashcards;
        final List<StudyProgress> progressList;

        LoadDueCardsResult(List<Flashcard> flashcards, List<StudyProgress> progressList) {
            this.flashcards = flashcards;
            this.progressList = progressList;
        }
    }

    /**
     * AsyncTask to rate card and update ML algorithm
     * Static inner class with WeakReference to prevent memory leaks
     */
    private static class RateCardTask extends AsyncTask<Void, Void, String> {
        private final WeakReference<StudyActivity> activityRef;
        private final AppDatabase database;
        private final StudyProgress progress;
        private final int quality;
        private final int cardIndex;

        RateCardTask(StudyActivity activity, AppDatabase database, StudyProgress progress, int quality, int cardIndex) {
            this.activityRef = new WeakReference<>(activity);
            this.database = database;
            this.progress = progress;
            this.quality = quality;
            this.cardIndex = cardIndex;
        }

        @Override
        protected String doInBackground(Void... voids) {
            // Update progress using ML algorithm
            StudyProgress updatedProgress = SpacedRepetitionAlgorithm.updateProgress(progress, quality);
            database.studyProgressDao().update(updatedProgress);

            return SpacedRepetitionAlgorithm.getNextReviewDescription(updatedProgress);
        }

        @Override
        protected void onPostExecute(String nextReview) {
            StudyActivity activity = activityRef.get();
            if (activity != null && !activity.isFinishing()) {
                if (nextReview != null) {
                    Toast.makeText(activity,
                            "Next review: " + nextReview,
                            Toast.LENGTH_SHORT).show();
                }

                activity.currentIndex++;
                activity.displayCard();
            }
        }
    }
}
