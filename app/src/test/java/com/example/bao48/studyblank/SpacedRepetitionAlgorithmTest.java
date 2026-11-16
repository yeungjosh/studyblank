package com.example.bao48.studyblank;

import com.example.bao48.studyblank.algorithm.SpacedRepetitionAlgorithm;
import com.example.bao48.studyblank.models.StudyProgress;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
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
}
