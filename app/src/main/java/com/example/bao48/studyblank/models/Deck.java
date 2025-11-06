package com.example.bao48.studyblank.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Deck Entity
 * Represents a collection of flashcards (e.g., "Gray's Anatomy - Skeletal System")
 */
@Entity(tableName = "decks")
public class Deck {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String description;
    private String category; // e.g., "Biology", "Anatomy", "Physiology"
    private long createdAt;
    private int cardCount; // Number of flashcards in this deck

    public Deck(String name, String description, String category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.createdAt = System.currentTimeMillis();
        this.cardCount = 0;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public int getCardCount() {
        return cardCount;
    }

    public void setCardCount(int cardCount) {
        this.cardCount = cardCount;
    }
}
