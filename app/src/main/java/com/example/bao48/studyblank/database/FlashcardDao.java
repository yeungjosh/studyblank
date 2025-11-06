package com.example.bao48.studyblank.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.bao48.studyblank.models.Flashcard;

import java.util.List;

/**
 * Data Access Object for Flashcard operations
 */
@Dao
public interface FlashcardDao {

    @Insert
    long insert(Flashcard flashcard);

    @Update
    void update(Flashcard flashcard);

    @Delete
    void delete(Flashcard flashcard);

    @Query("SELECT * FROM flashcards WHERE id = :id")
    Flashcard getById(int id);

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId")
    List<Flashcard> getByDeckId(int deckId);

    @Query("SELECT * FROM flashcards")
    List<Flashcard> getAll();

    @Query("SELECT COUNT(*) FROM flashcards WHERE deckId = :deckId")
    int getCardCountByDeck(int deckId);

    @Query("DELETE FROM flashcards WHERE deckId = :deckId")
    void deleteByDeckId(int deckId);
}
