package com.example.bao48.studyblank.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Index;

/**
 * Flashcard Entity
 * Represents a single flashcard with a question and answer
 */
@Entity(tableName = "flashcards",
        foreignKeys = @ForeignKey(entity = Deck.class,
                parentColumns = "id",
                childColumns = "deckId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("deckId")})
public class Flashcard {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int deckId;
    private String question;
    private String answer;
    private String imageUrl; // Optional: URL or path to anatomy diagram
    private long createdAt;

    public Flashcard(int deckId, String question, String answer, String imageUrl) {
        this.deckId = deckId;
        this.question = question;
        this.answer = answer;
        this.imageUrl = imageUrl;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDeckId() {
        return deckId;
    }

    public void setDeckId(int deckId) {
        this.deckId = deckId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
