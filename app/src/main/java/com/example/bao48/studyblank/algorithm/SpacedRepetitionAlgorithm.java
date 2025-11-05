package com.example.bao48.studyblank.algorithm;

import com.example.bao48.studyblank.models.StudyProgress;

/**
 * Spaced Repetition Algorithm (SM-2)
 *
 * This is the MACHINE LEARNING component of the app that intelligently schedules
 * flashcard reviews based on user performance. The algorithm adapts to each user's
 * learning patterns, making the study process more efficient.
 *
 * SM-2 Algorithm Overview:
 * - Tracks "ease factor" for each card (how easy it is for the user)
 * - Increases review intervals for cards answered correctly
 * - Resets intervals for cards answered incorrectly
 * - Adapts difficulty based on performance (quality rating)
 *
 * Quality Ratings:
 * 5 - Perfect response (immediate recall)
 * 4 - Correct response after hesitation
 * 3 - Correct response with difficulty
 * 2 - Incorrect response where correct answer seemed easy
 * 1 - Incorrect response with some memory
 * 0 - Complete blackout (no memory)
 */
public class SpacedRepetitionAlgorithm {

    private static final double MIN_EASE_FACTOR = 1.3;
    private static final int MILLISECONDS_PER_DAY = 86400000;

    /**
     * Update study progress based on user's answer quality
     *
     * @param progress Current study progress for the flashcard
     * @param quality User's performance rating (0-5)
     * @return Updated StudyProgress with new scheduling parameters
     */
    public static StudyProgress updateProgress(StudyProgress progress, int quality) {

        // Validate quality rating
        if (quality < 0 || quality > 5) {
            throw new IllegalArgumentException("Quality must be between 0 and 5");
        }

        // Update performance metrics
        if (quality >= 3) {
            progress.setCorrectCount(progress.getCorrectCount() + 1);
        } else {
            progress.setIncorrectCount(progress.getIncorrectCount() + 1);
        }
        progress.updateAccuracy();

        // Calculate new ease factor using SM-2 formula
        double newEaseFactor = progress.getEaseFactor() +
                              (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02));

        // Ensure ease factor doesn't go below minimum
        if (newEaseFactor < MIN_EASE_FACTOR) {
            newEaseFactor = MIN_EASE_FACTOR;
        }
        progress.setEaseFactor(newEaseFactor);

        // Update repetitions and intervals based on performance
        if (quality < 3) {
            // Answer was incorrect - reset the learning process
            progress.setRepetitions(0);
            progress.setInterval(0);
            // Schedule for immediate review (within 10 minutes)
            progress.setNextReviewDate(System.currentTimeMillis() + (10 * 60 * 1000));
        } else {
            // Answer was correct - increase the interval
            int repetitions = progress.getRepetitions() + 1;
            progress.setRepetitions(repetitions);

            int interval;
            if (repetitions == 1) {
                interval = 1; // Review in 1 day
            } else if (repetitions == 2) {
                interval = 6; // Review in 6 days
            } else {
                // Use ease factor to calculate interval: previous interval * ease factor
                interval = (int) Math.round(progress.getInterval() * newEaseFactor);
            }

            progress.setInterval(interval);

            // Calculate next review date (interval in days)
            long nextReview = System.currentTimeMillis() + (interval * MILLISECONDS_PER_DAY);
            progress.setNextReviewDate(nextReview);
        }

        // Update last review timestamp
        progress.setLastReviewDate(System.currentTimeMillis());

        return progress;
    }

    /**
     * Check if a flashcard is due for review
     *
     * @param progress Study progress for the flashcard
     * @return true if card should be reviewed now
     */
    public static boolean isDueForReview(StudyProgress progress) {
        return System.currentTimeMillis() >= progress.getNextReviewDate();
    }

    /**
     * Calculate priority score for scheduling (higher = more urgent)
     * Used for intelligent sorting of review queue
     *
     * @param progress Study progress for the flashcard
     * @return Priority score
     */
    public static double calculatePriority(StudyProgress progress) {
        long overdue = System.currentTimeMillis() - progress.getNextReviewDate();
        double daysOverdue = overdue / (double) MILLISECONDS_PER_DAY;

        // Priority increases with:
        // 1. How overdue the card is
        // 2. Lower accuracy (cards you struggle with)
        // 3. Lower ease factor (difficult cards)

        double priorityScore = daysOverdue * 10;
        priorityScore += (100 - progress.getAccuracy()) * 0.5;
        priorityScore += (3.0 - progress.getEaseFactor()) * 20;

        return Math.max(0, priorityScore);
    }

    /**
     * Get a human-readable description of when to review next
     *
     * @param progress Study progress for the flashcard
     * @return String like "in 3 days" or "overdue by 2 days"
     */
    public static String getNextReviewDescription(StudyProgress progress) {
        long diff = progress.getNextReviewDate() - System.currentTimeMillis();
        long days = Math.abs(diff / MILLISECONDS_PER_DAY);

        if (diff > 0) {
            if (days == 0) {
                return "today";
            } else if (days == 1) {
                return "tomorrow";
            } else {
                return "in " + days + " days";
            }
        } else {
            if (days == 0) {
                return "due now";
            } else if (days == 1) {
                return "overdue by 1 day";
            } else {
                return "overdue by " + days + " days";
            }
        }
    }

    /**
     * Get difficulty level description based on ease factor
     *
     * @param easeFactor The ease factor from study progress
     * @return Difficulty description
     */
    public static String getDifficultyLevel(double easeFactor) {
        if (easeFactor >= 2.5) {
            return "Easy";
        } else if (easeFactor >= 2.0) {
            return "Medium";
        } else if (easeFactor >= 1.5) {
            return "Hard";
        } else {
            return "Very Hard";
        }
    }
}
