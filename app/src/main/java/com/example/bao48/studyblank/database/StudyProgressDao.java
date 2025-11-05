package com.example.bao48.studyblank.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.bao48.studyblank.models.StudyProgress;

import java.util.List;

/**
 * Data Access Object for StudyProgress operations
 * Critical for the ML/spaced repetition algorithm
 */
@Dao
public interface StudyProgressDao {

    @Insert
    long insert(StudyProgress progress);

    @Update
    void update(StudyProgress progress);

    @Delete
    void delete(StudyProgress progress);

    @Query("SELECT * FROM study_progress WHERE flashcardId = :flashcardId AND userId = :userId")
    StudyProgress getByFlashcardAndUser(int flashcardId, String userId);

    @Query("SELECT * FROM study_progress WHERE userId = :userId AND nextReviewDate <= :currentTime ORDER BY nextReviewDate ASC")
    List<StudyProgress> getDueCards(String userId, long currentTime);

    @Query("SELECT sp.* FROM study_progress sp " +
           "INNER JOIN flashcards f ON sp.flashcardId = f.id " +
           "WHERE sp.userId = :userId AND f.deckId = :deckId AND sp.nextReviewDate <= :currentTime " +
           "ORDER BY sp.nextReviewDate ASC")
    List<StudyProgress> getDueCardsByDeck(String userId, int deckId, long currentTime);

    @Query("SELECT * FROM study_progress WHERE userId = :userId ORDER BY nextReviewDate ASC")
    List<StudyProgress> getAllByUser(String userId);

    @Query("SELECT AVG(accuracy) FROM study_progress WHERE userId = :userId")
    double getAverageAccuracy(String userId);

    @Query("SELECT COUNT(*) FROM study_progress WHERE userId = :userId AND nextReviewDate <= :currentTime")
    int getDueCardsCount(String userId, long currentTime);

    @Query("DELETE FROM study_progress WHERE flashcardId = :flashcardId")
    void deleteByFlashcard(int flashcardId);
}
