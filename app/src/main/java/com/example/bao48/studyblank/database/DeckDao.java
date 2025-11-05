package com.example.bao48.studyblank.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.bao48.studyblank.models.Deck;

import java.util.List;

/**
 * Data Access Object for Deck operations
 */
@Dao
public interface DeckDao {

    @Insert
    long insert(Deck deck);

    @Update
    void update(Deck deck);

    @Delete
    void delete(Deck deck);

    @Query("SELECT * FROM decks WHERE id = :id")
    Deck getById(int id);

    @Query("SELECT * FROM decks ORDER BY createdAt DESC")
    List<Deck> getAll();

    @Query("SELECT * FROM decks WHERE category = :category ORDER BY name ASC")
    List<Deck> getByCategory(String category);

    @Query("SELECT DISTINCT category FROM decks ORDER BY category ASC")
    List<String> getAllCategories();
}
