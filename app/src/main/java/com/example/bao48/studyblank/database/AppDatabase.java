package com.example.bao48.studyblank.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.bao48.studyblank.models.Deck;
import com.example.bao48.studyblank.models.Flashcard;
import com.example.bao48.studyblank.models.StudyProgress;

/**
 * Main Room Database for StudyBlank
 * Contains all entities and provides DAOs
 */
@Database(entities = {Deck.class, Flashcard.class, StudyProgress.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    // Abstract methods to get DAOs
    public abstract DeckDao deckDao();
    public abstract FlashcardDao flashcardDao();
    public abstract StudyProgressDao studyProgressDao();

    /**
     * Get singleton instance of the database
     */
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "studyblank_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Destroy the instance (useful for testing)
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }
}
