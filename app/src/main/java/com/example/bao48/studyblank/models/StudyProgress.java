package com.example.bao48.studyblank.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * StudyProgress Entity
 * Tracks user's progress with spaced repetition algorithm (SM-2)
 * This is the ML/intelligent component that adapts to user performance
 */
@Entity(tableName = "study_progress",
        foreignKeys = @ForeignKey(entity = Flashcard.class,
                parentColumns = "id",
                childColumns = "flashcardId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("flashcardId")})
public class StudyProgress {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int flashcardId;
    private String userId; // Firebase user ID

    // SM-2 Algorithm Parameters
    private double easeFactor;      // How easy the card is (starts at 2.5)
    private int repetitions;        // Number of successful repetitions
    private int interval;           // Days until next review
    private long nextReviewDate;    // Timestamp of next review
    private long lastReviewDate;    // Timestamp of last review

    // Performance Metrics
    private int correctCount;       // Total correct answers
    private int incorrectCount;     // Total incorrect answers
    private double accuracy;        // Percentage correct

    public StudyProgress(int flashcardId, String userId) {
        this.flashcardId = flashcardId;
        this.userId = userId;
        // Initialize SM-2 defaults
        this.easeFactor = 2.5;
        this.repetitions = 0;
        this.interval = 0;
        this.nextReviewDate = System.currentTimeMillis();
        this.lastReviewDate = 0;
        this.correctCount = 0;
        this.incorrectCount = 0;
        this.accuracy = 0.0;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFlashcardId() {
        return flashcardId;
    }

    public void setFlashcardId(int flashcardId) {
        this.flashcardId = flashcardId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getEaseFactor() {
        return easeFactor;
    }

    public void setEaseFactor(double easeFactor) {
        this.easeFactor = easeFactor;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public long getNextReviewDate() {
        return nextReviewDate;
    }

    public void setNextReviewDate(long nextReviewDate) {
        this.nextReviewDate = nextReviewDate;
    }

    public long getLastReviewDate() {
        return lastReviewDate;
    }

    public void setLastReviewDate(long lastReviewDate) {
        this.lastReviewDate = lastReviewDate;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public int getIncorrectCount() {
        return incorrectCount;
    }

    public void setIncorrectCount(int incorrectCount) {
        this.incorrectCount = incorrectCount;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    /**
     * Calculate and update accuracy percentage
     */
    public void updateAccuracy() {
        int total = correctCount + incorrectCount;
        if (total > 0) {
            this.accuracy = (double) correctCount / total * 100.0;
        }
    }
}
