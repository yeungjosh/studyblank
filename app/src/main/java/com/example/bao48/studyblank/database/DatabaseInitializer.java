package com.example.bao48.studyblank.database;

import com.example.bao48.studyblank.models.Deck;
import com.example.bao48.studyblank.models.Flashcard;
import com.example.bao48.studyblank.models.StudyProgress;

/**
 * Database Initializer
 * Populates the database with sample Gray's Anatomy flashcards
 */
public class DatabaseInitializer {

    public static void initializeIfEmpty(AppDatabase database, String userId) {
        // Check if database already has data
        if (database.deckDao().getAll().size() > 0) {
            return; // Already initialized
        }

        // Create Gray's Anatomy decks
        long skeletalId = createSkeletalSystemDeck(database, userId);
        long muscularId = createMuscularSystemDeck(database, userId);
        long nervousId = createNervousSystemDeck(database, userId);
    }

    private static long createSkeletalSystemDeck(AppDatabase database, String userId) {
        // Create deck
        Deck deck = new Deck(
                "Gray's Anatomy - Skeletal System",
                "Learn the bones and structure of the human skeleton",
                "Anatomy"
        );
        long deckId = database.deckDao().insert(deck);

        // Add flashcards
        String[][] flashcards = {
                {"What is the largest bone in the human body?",
                        "The femur (thigh bone)"},
                {"How many bones are in the adult human body?",
                        "206 bones"},
                {"What is the scientific name for the collarbone?",
                        "Clavicle"},
                {"Which bone protects the brain?",
                        "The skull (cranium)"},
                {"What is the name of the kneecap bone?",
                        "Patella"},
                {"Which bone connects the arm to the torso?",
                        "Scapula (shoulder blade)"},
                {"What is the smallest bone in the human body?",
                        "Stapes (in the middle ear)"},
                {"How many vertebrae are in the human spine?",
                        "33 vertebrae (7 cervical, 12 thoracic, 5 lumbar, 5 sacral fused, 4 coccygeal fused)"},
                {"What is the scientific name for the shin bone?",
                        "Tibia"},
                {"Which bones protect the heart and lungs?",
                        "Ribs (rib cage/thoracic cage)"},
                {"What is the name of the bone in the upper arm?",
                        "Humerus"},
                {"How many ribs does a typical human have?",
                        "24 ribs (12 pairs)"},
                {"What is the heel bone called?",
                        "Calcaneus"},
                {"Which bone is commonly called the tailbone?",
                        "Coccyx"},
                {"What are the two bones in the forearm?",
                        "Radius and Ulna"}
        };

        for (String[] card : flashcards) {
            Flashcard flashcard = new Flashcard((int) deckId, card[0], card[1], null);
            long flashcardId = database.flashcardDao().insert(flashcard);

            // Create initial study progress
            StudyProgress progress = new StudyProgress((int) flashcardId, userId);
            database.studyProgressDao().insert(progress);
        }

        // Update card count
        deck.setId((int) deckId);
        deck.setCardCount(flashcards.length);
        database.deckDao().update(deck);

        return deckId;
    }

    private static long createMuscularSystemDeck(AppDatabase database, String userId) {
        Deck deck = new Deck(
                "Gray's Anatomy - Muscular System",
                "Master the muscles and their functions",
                "Anatomy"
        );
        long deckId = database.deckDao().insert(deck);

        String[][] flashcards = {
                {"What is the largest muscle in the human body?",
                        "Gluteus maximus"},
                {"Which muscle is responsible for pumping blood?",
                        "Cardiac muscle (the heart)"},
                {"What is the muscle at the back of the upper arm called?",
                        "Triceps brachii"},
                {"Which muscle is located at the front of the thigh?",
                        "Quadriceps femoris"},
                {"What is the primary muscle used for breathing?",
                        "Diaphragm"},
                {"Which muscle is known as the 'six-pack' muscle?",
                        "Rectus abdominis"},
                {"What is the calf muscle called?",
                        "Gastrocnemius"},
                {"Which muscle allows you to bend your arm?",
                        "Biceps brachii"},
                {"What type of muscle is found in the walls of organs?",
                        "Smooth muscle"},
                {"Which muscle group is located at the back of the thigh?",
                        "Hamstrings"}
        };

        for (String[] card : flashcards) {
            Flashcard flashcard = new Flashcard((int) deckId, card[0], card[1], null);
            long flashcardId = database.flashcardDao().insert(flashcard);

            StudyProgress progress = new StudyProgress((int) flashcardId, userId);
            database.studyProgressDao().insert(progress);
        }

        deck.setId((int) deckId);
        deck.setCardCount(flashcards.length);
        database.deckDao().update(deck);

        return deckId;
    }

    private static long createNervousSystemDeck(AppDatabase database, String userId) {
        Deck deck = new Deck(
                "Gray's Anatomy - Nervous System",
                "Understand the brain, nerves, and neural pathways",
                "Anatomy"
        );
        long deckId = database.deckDao().insert(deck);

        String[][] flashcards = {
                {"What is the largest part of the human brain?",
                        "Cerebrum"},
                {"Which part of the brain controls balance and coordination?",
                        "Cerebellum"},
                {"What connects the brain to the spinal cord?",
                        "Brain stem (medulla oblongata)"},
                {"What are the cells that transmit nerve signals called?",
                        "Neurons"},
                {"How many pairs of cranial nerves are there?",
                        "12 pairs"},
                {"What is the protective covering of the brain and spinal cord?",
                        "Meninges"},
                {"Which lobe of the brain processes visual information?",
                        "Occipital lobe"},
                {"What is the longest nerve in the human body?",
                        "Sciatic nerve"},
                {"What substance insulates nerve fibers?",
                        "Myelin sheath"},
                {"Which part of the brain regulates body temperature?",
                        "Hypothalamus"}
        };

        for (String[] card : flashcards) {
            Flashcard flashcard = new Flashcard((int) deckId, card[0], card[1], null);
            long flashcardId = database.flashcardDao().insert(flashcard);

            StudyProgress progress = new StudyProgress((int) flashcardId, userId);
            database.studyProgressDao().insert(progress);
        }

        deck.setId((int) deckId);
        deck.setCardCount(flashcards.length);
        database.deckDao().update(deck);

        return deckId;
    }
}
