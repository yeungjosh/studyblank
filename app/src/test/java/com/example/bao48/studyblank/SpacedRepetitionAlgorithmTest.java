package com.example.bao48.studyblank;

import com.example.bao48.studyblank.algorithm.SpacedRepetitionAlgorithm;
import com.example.bao48.studyblank.models.StudyProgress;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Local unit tests for the SM-2 scheduler. */
public class SpacedRepetitionAlgorithmTest {

    private static final long DAY_MS = 86400000L;

    private StudyProgress fresh() {
        return new StudyProgress(1, "user");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsQualityAboveFive() {
        SpacedRepetitionAlgorithm.updateProgress(fresh(), 6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNegativeQuality() {
        SpacedRepetitionAlgorithm.updateProgress(fresh(), -1);
    }

    @Test
    public void firstTwoCorrectReviewsUseOneAndSixDays() {
        StudyProgress p = fresh();
        SpacedRepetitionAlgorithm.updateProgress(p, 5);
        assertEquals(1, p.getInterval());
        SpacedRepetitionAlgorithm.updateProgress(p, 5);
        assertEquals(6, p.getInterval());
        assertEquals(2, p.getRepetitions());
    }

    @Test
    public void thirdReviewMultipliesIntervalByEaseFactor() {
        StudyProgress p = fresh();
        SpacedRepetitionAlgorithm.updateProgress(p, 5);
        SpacedRepetitionAlgorithm.updateProgress(p, 5);
        SpacedRepetitionAlgorithm.updateProgress(p, 5);
        // ease factor is 2.5 + 3 * 0.1 = 2.8 after three perfect answers; 6 * 2.8 = 16.8 -> 17
        assertEquals(17, p.getInterval());
    }

    @Test
    public void failedReviewResetsRepetitionsAndInterval() {
        StudyProgress p = fresh();
        SpacedRepetitionAlgorithm.updateProgress(p, 5);
        SpacedRepetitionAlgorithm.updateProgress(p, 5);
        SpacedRepetitionAlgorithm.updateProgress(p, 1);
        assertEquals(0, p.getRepetitions());
        assertEquals(0, p.getInterval());
        assertEquals(1, p.getIncorrectCount());
        long delta = p.getNextReviewDate() - System.currentTimeMillis();
        assertTrue(delta > 0 && delta <= 10 * 60 * 1000);
    }

    @Test
    public void easeFactorNeverDropsBelowMinimum() {
        StudyProgress p = fresh();
        for (int i = 0; i < 20; i++) {
            SpacedRepetitionAlgorithm.updateProgress(p, 0);
        }
        assertEquals(1.3, p.getEaseFactor(), 1e-9);
    }

    @Test
    public void longIntervalsScheduleInTheFutureWithoutIntOverflow() {
        StudyProgress p = fresh();
        p.setRepetitions(5);
        p.setInterval(40); // next interval ~ 40 * 2.6 = 104 days; 104 * 86_400_000 overflows int
        long before = System.currentTimeMillis();
        SpacedRepetitionAlgorithm.updateProgress(p, 5);
        long expected = p.getInterval() * DAY_MS;
        assertTrue(p.getInterval() > 24);
        assertTrue(p.getNextReviewDate() - before >= expected);
        assertTrue(p.getNextReviewDate() - before < expected + 60000);
    }

    @Test
    public void accuracyTracksCorrectAnswers() {
        StudyProgress p = fresh();
        SpacedRepetitionAlgorithm.updateProgress(p, 4);
        SpacedRepetitionAlgorithm.updateProgress(p, 2);
        assertEquals(50.0, p.getAccuracy(), 1e-9);
    }

    @Test
    public void difficultyLevelBoundaries() {
        assertEquals("Easy", SpacedRepetitionAlgorithm.getDifficultyLevel(2.5));
        assertEquals("Medium", SpacedRepetitionAlgorithm.getDifficultyLevel(2.0));
        assertEquals("Hard", SpacedRepetitionAlgorithm.getDifficultyLevel(1.5));
        assertEquals("Very Hard", SpacedRepetitionAlgorithm.getDifficultyLevel(1.3));
    }

    private static final long HOUR_MS = 3600000L;

    @Test
    public void newCardIsDueImmediately() {
        assertTrue(SpacedRepetitionAlgorithm.isDueForReview(fresh()));
    }

    @Test
    public void cardWithFutureReviewDateIsNotDue() {
        StudyProgress p = fresh();
        p.setNextReviewDate(System.currentTimeMillis() + DAY_MS);
        assertFalse(SpacedRepetitionAlgorithm.isDueForReview(p));
        p.setNextReviewDate(System.currentTimeMillis() - HOUR_MS);
        assertTrue(SpacedRepetitionAlgorithm.isDueForReview(p));
    }

    @Test
    public void failedCardIsScheduledWithinMinutesAndBecomesDueSoon() {
        StudyProgress p = fresh();
        long before = System.currentTimeMillis();
        SpacedRepetitionAlgorithm.updateProgress(p, 0);
        long delay = p.getNextReviewDate() - before;
        assertTrue(delay >= 10 * 60 * 1000L - 1000);
        assertTrue(delay < HOUR_MS);
        assertFalse(SpacedRepetitionAlgorithm.isDueForReview(p));
    }

    @Test
    public void priorityGrowsWithOverdueDays() {
        StudyProgress recent = fresh();
        recent.setNextReviewDate(System.currentTimeMillis() - DAY_MS);
        StudyProgress stale = fresh();
        stale.setNextReviewDate(System.currentTimeMillis() - 10 * DAY_MS);
        assertTrue(SpacedRepetitionAlgorithm.calculatePriority(stale)
                > SpacedRepetitionAlgorithm.calculatePriority(recent));
    }

    @Test
    public void priorityIsHigherForLowAccuracyAndHardCards() {
        long due = System.currentTimeMillis() - DAY_MS;
        StudyProgress good = fresh();
        good.setNextReviewDate(due);
        good.setAccuracy(100.0);
        good.setEaseFactor(2.5);
        StudyProgress struggling = fresh();
        struggling.setNextReviewDate(due);
        struggling.setAccuracy(20.0);
        struggling.setEaseFactor(1.3);
        assertTrue(SpacedRepetitionAlgorithm.calculatePriority(struggling)
                > SpacedRepetitionAlgorithm.calculatePriority(good));
    }

    @Test
    public void priorityIsNeverNegative() {
        StudyProgress p = fresh();
        p.setNextReviewDate(System.currentTimeMillis() + 365 * DAY_MS);
        p.setAccuracy(100.0);
        p.setEaseFactor(3.5);
        assertEquals(0.0, SpacedRepetitionAlgorithm.calculatePriority(p), 1e-9);
    }

    @Test
    public void nextReviewDescriptionForUpcomingCards() {
        StudyProgress p = fresh();
        long now = System.currentTimeMillis();
        p.setNextReviewDate(now + HOUR_MS);
        assertEquals("today", SpacedRepetitionAlgorithm.getNextReviewDescription(p));
        p.setNextReviewDate(now + DAY_MS + HOUR_MS);
        assertEquals("tomorrow", SpacedRepetitionAlgorithm.getNextReviewDescription(p));
        p.setNextReviewDate(now + 3 * DAY_MS + HOUR_MS);
        assertEquals("in 3 days", SpacedRepetitionAlgorithm.getNextReviewDescription(p));
    }

    @Test
    public void nextReviewDescriptionForOverdueCards() {
        StudyProgress p = fresh();
        long now = System.currentTimeMillis();
        p.setNextReviewDate(now - HOUR_MS);
        assertEquals("due now", SpacedRepetitionAlgorithm.getNextReviewDescription(p));
        p.setNextReviewDate(now - DAY_MS - HOUR_MS);
        assertEquals("overdue by 1 day", SpacedRepetitionAlgorithm.getNextReviewDescription(p));
        p.setNextReviewDate(now - 4 * DAY_MS - HOUR_MS);
        assertEquals("overdue by 4 days", SpacedRepetitionAlgorithm.getNextReviewDescription(p));
    }
}
