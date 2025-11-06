# StudyBlank - Complete Technical Documentation

## Table of Contents
1. [Project Overview](#project-overview)
2. [Architecture](#architecture)
3. [Machine Learning Component](#machine-learning-component)
4. [Database Schema](#database-schema)
5. [User Flow](#user-flow)
6. [Installation & Setup](#installation--setup)
7. [Code Structure](#code-structure)
8. [Future Enhancements](#future-enhancements)

---

## Project Overview

**StudyBlank** is an Android flashcard application designed for Biology and Anatomy memorization, featuring an intelligent **Spaced Repetition Algorithm** (SM-2) that adapts to each user's learning patterns.

### Key Features
- 🧠 **ML-Powered Spaced Repetition**: Intelligent scheduling based on user performance
- 📚 **Pre-loaded Content**: Gray's Anatomy flashcards (35+ cards across 3 decks)
- 📊 **Progress Tracking**: Accuracy metrics and performance analytics
- 🔐 **Firebase Authentication**: Secure user accounts
- 💾 **Room Database**: Local data persistence
- 🎯 **Adaptive Learning**: Difficulty adjusts based on your answers

### Technology Stack
- **Language**: Java
- **Database**: Room (SQLite)
- **Authentication**: Firebase Auth
- **UI**: Material Design with CardView/RecyclerView
- **Architecture**: MVVM-inspired with AsyncTask
- **Min SDK**: 15 (Android 4.0.3)
- **Target SDK**: 26 (Android 8.0)

---

## Architecture

### High-Level Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │    home      │  │ createAccount│  │  MainActivity│          │
│  │  (Login)     │─▶│  (Register)  │─▶│  (Dashboard) │          │
│  └──────────────┘  └──────────────┘  └──────┬───────┘          │
│                                              │                   │
│                         ┌────────────────────┼─────────────┐    │
│                         │                    │             │    │
│                         ▼                    ▼             ▼    │
│              ┌──────────────────┐  ┌─────────────────┐  [...]  │
│              │  StudyActivity   │  │ DeckListActivity│          │
│              │ (Flashcard Quiz) │  │  (Browse Decks) │          │
│              └─────────┬────────┘  └─────────────────┘          │
│                        │                                         │
└────────────────────────┼─────────────────────────────────────────┘
                         │
┌────────────────────────┼─────────────────────────────────────────┐
│                        │       BUSINESS LOGIC LAYER              │
├────────────────────────┼─────────────────────────────────────────┤
│                        ▼                                          │
│          ┌──────────────────────────────┐                        │
│          │ SpacedRepetitionAlgorithm    │◀── ML COMPONENT        │
│          │      (SM-2 Algorithm)        │                        │
│          └──────────────────────────────┘                        │
│                        │                                          │
│                        │ Updates progress based on performance   │
│                        ▼                                          │
└────────────────────────┼─────────────────────────────────────────┘
                         │
┌────────────────────────┼─────────────────────────────────────────┐
│                        │         DATA LAYER                      │
├────────────────────────┼─────────────────────────────────────────┤
│                        ▼                                          │
│              ┌──────────────────┐                                │
│              │   AppDatabase    │                                │
│              │   (Room/SQLite)  │                                │
│              └────────┬─────────┘                                │
│                       │                                           │
│        ┌──────────────┼──────────────┐                           │
│        │              │              │                           │
│        ▼              ▼              ▼                           │
│  ┌─────────┐  ┌────────────┐  ┌──────────────┐                 │
│  │ DeckDao │  │FlashcardDao│  │StudyProgress │                 │
│  │         │  │            │  │     Dao      │                 │
│  └────┬────┘  └─────┬──────┘  └──────┬───────┘                 │
│       │             │                │                           │
│       ▼             ▼                ▼                           │
│  ┌─────────┐  ┌────────────┐  ┌──────────────┐                 │
│  │  Deck   │  │ Flashcard  │  │StudyProgress │                 │
│  │ Entity  │  │   Entity   │  │   Entity     │                 │
│  └─────────┘  └────────────┘  └──────────────┘                 │
│                                                                   │
└───────────────────────────────────────────────────────────────────┘
          │                        │
          ▼                        ▼
┌──────────────────┐    ┌──────────────────┐
│ Firebase Auth    │    │ Local SQLite DB  │
│ (User Accounts)  │    │ (Flashcard Data) │
└──────────────────┘    └──────────────────┘
```

### Component Descriptions

#### Presentation Layer
- **home.java**: Login screen with Firebase authentication
- **createAccount.java**: User registration with email verification
- **MainActivity.java**: Dashboard showing stats and navigation
- **StudyActivity.java**: Flashcard study session with rating buttons
- **DeckListActivity.java**: Browse available flashcard decks

#### Business Logic Layer
- **SpacedRepetitionAlgorithm.java**: The ML component that calculates optimal review intervals

#### Data Layer
- **AppDatabase.java**: Room database singleton
- **DAOs**: Data access objects for CRUD operations
- **Entities**: Deck, Flashcard, StudyProgress

---

## Machine Learning Component

### Spaced Repetition Algorithm (SM-2)

The app uses the **SuperMemo 2 (SM-2)** algorithm, a scientifically-proven method for optimizing memory retention through intelligent scheduling.

### Algorithm Overview

```
┌─────────────────────────────────────────────────────────────┐
│              USER STUDIES A FLASHCARD                       │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
           ┌─────────────────────────────┐
           │  User Rates Answer Quality  │
           │  (0 = Wrong, 5 = Perfect)   │
           └────────────┬────────────────┘
                        │
          ┌─────────────┴─────────────┐
          │                           │
          ▼                           ▼
    Quality < 3                  Quality ≥ 3
   (Incorrect)                   (Correct)
          │                           │
          ▼                           ▼
┌─────────────────────┐    ┌──────────────────────┐
│ RESET LEARNING      │    │ ADVANCE LEARNING     │
├─────────────────────┤    ├──────────────────────┤
│ • Repetitions = 0   │    │ • Repetitions += 1   │
│ • Interval = 0      │    │ • Calculate new      │
│ • Next review:      │    │   interval based on  │
│   10 minutes        │    │   ease factor        │
└─────────────────────┘    └──────────────────────┘
          │                           │
          └───────────┬───────────────┘
                      │
                      ▼
        ┌──────────────────────────────┐
        │  UPDATE EASE FACTOR          │
        │  (How "easy" is this card)   │
        │                              │
        │  EF_new = EF_old +           │
        │  (0.1 - (5-q)*(0.08+(5-q)*0.02))│
        └──────────────┬───────────────┘
                       │
                       ▼
        ┌──────────────────────────────┐
        │  UPDATE STUDY PROGRESS       │
        │  • Next review date          │
        │  • Accuracy percentage       │
        │  • Correct/incorrect count   │
        └──────────────────────────────┘
```

### Interval Calculation Logic

```
IF quality < 3 (incorrect answer):
    ├─ repetitions = 0
    ├─ interval = 0
    └─ next_review = now + 10 minutes

ELSE IF quality ≥ 3 (correct answer):
    ├─ repetitions += 1
    │
    ├─ IF repetitions == 1:
    │      interval = 1 day
    │
    ├─ ELSE IF repetitions == 2:
    │      interval = 6 days
    │
    └─ ELSE (repetitions > 2):
           interval = previous_interval × ease_factor

    next_review = now + (interval × 24 hours)
```

### Ease Factor Formula

The **Ease Factor (EF)** represents how "easy" a card is for the user. It starts at 2.5 and adjusts based on performance:

```
EF_new = EF_old + (0.1 - (5 - quality) × (0.08 + (5 - quality) × 0.02))

EF_new = max(EF_new, 1.3)  // Minimum ease factor is 1.3
```

**Example Calculations:**

| Quality | Description | EF Change | Example EF (from 2.5) |
|---------|-------------|-----------|----------------------|
| 5       | Perfect recall | +0.10 | 2.60 (easier) |
| 4       | Correct after hesitation | +0.00 | 2.50 (unchanged) |
| 3       | Correct with difficulty | -0.14 | 2.36 (harder) |
| 2       | Incorrect but familiar | -0.32 | 2.18 (much harder) |
| 1       | Incorrect, slight memory | -0.54 | 1.96 (very hard) |
| 0       | Complete blackout | -0.80 | 1.70 (extremely hard) |

### Priority Scoring

When multiple cards are due, the algorithm calculates a priority score to determine which cards need review most urgently:

```
priority_score = (days_overdue × 10)
                 + ((100 - accuracy) × 0.5)
                 + ((3.0 - ease_factor) × 20)
```

**Higher scores = higher priority**

- Cards overdue by many days get higher priority
- Cards with low accuracy (struggling cards) get higher priority
- Cards with low ease factor (difficult cards) get higher priority

### ML Benefits

This algorithm provides several **intelligent adaptive learning** features:

1. **Personalized Scheduling**: Each card's review interval adapts to YOUR performance
2. **Forgetting Curve Optimization**: Reviews happen just before you're likely to forget
3. **Efficient Learning**: Focus time on difficult cards, less time on easy ones
4. **Long-term Retention**: Gradually spaces out reviews as mastery increases
5. **Performance Tracking**: Accuracy metrics show your progress over time

---

## Database Schema

### Entity Relationship Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                          DATABASE SCHEMA                        │
└─────────────────────────────────────────────────────────────────┘

┌──────────────────────┐
│       DECKS          │
├──────────────────────┤
│ id (PK)             │
│ name                 │
│ description          │
│ category             │
│ createdAt            │
│ cardCount            │
└──────────┬───────────┘
           │
           │ 1:N
           │
           ▼
┌──────────────────────┐
│     FLASHCARDS       │
├──────────────────────┤
│ id (PK)             │
│ deckId (FK) ────────┼─────▶ Deck.id
│ question             │
│ answer               │
│ imageUrl             │
│ createdAt            │
└──────────┬───────────┘
           │
           │ 1:N
           │
           ▼
┌──────────────────────┐
│   STUDY_PROGRESS     │ ◀─── ML ALGORITHM USES THIS
├──────────────────────┤
│ id (PK)             │
│ flashcardId (FK) ───┼─────▶ Flashcard.id
│ userId               │
│ easeFactor           │ ◀─── SM-2 Algorithm parameter
│ repetitions          │ ◀─── SM-2 Algorithm parameter
│ interval             │ ◀─── SM-2 Algorithm parameter
│ nextReviewDate       │ ◀─── When to show card next
│ lastReviewDate       │
│ correctCount         │
│ incorrectCount       │
│ accuracy             │
└──────────────────────┘
```

### Entity Descriptions

#### Deck Entity
Represents a collection of flashcards (e.g., "Gray's Anatomy - Skeletal System")

**Fields:**
- `id`: Primary key (auto-generated)
- `name`: Deck name
- `description`: Brief description
- `category`: Category (e.g., "Anatomy", "Biology")
- `createdAt`: Timestamp of creation
- `cardCount`: Number of flashcards in this deck

#### Flashcard Entity
Individual question/answer pairs

**Fields:**
- `id`: Primary key (auto-generated)
- `deckId`: Foreign key to Deck
- `question`: Front of the flashcard
- `answer`: Back of the flashcard
- `imageUrl`: Optional path to anatomy diagram
- `createdAt`: Timestamp of creation

#### StudyProgress Entity
**Critical for ML algorithm** - tracks user's learning progress per card

**Fields:**
- `id`: Primary key (auto-generated)
- `flashcardId`: Foreign key to Flashcard
- `userId`: Firebase user ID
- `easeFactor`: SM-2 ease factor (default 2.5)
- `repetitions`: Number of successful reviews
- `interval`: Days until next review
- `nextReviewDate`: Timestamp of next scheduled review
- `lastReviewDate`: Timestamp of last review
- `correctCount`: Total correct answers
- `incorrectCount`: Total incorrect answers
- `accuracy`: Percentage correct (calculated)

---

## User Flow

### Complete User Journey

```
START
  │
  ▼
┌─────────────────┐
│  Login Screen   │
│   (home.java)   │
└────────┬────────┘
         │
   ┌─────┴─────┐
   │           │
   ▼           ▼
[Login]    [Create Account]
   │           │
   │           ▼
   │     ┌──────────────────┐
   │     │ createAccount.   │
   │     │  - Enter email   │
   │     │  - Enter password│
   │     │  - Verify email  │
   │     └────────┬─────────┘
   │              │
   └──────┬───────┘
          │
          ▼
   ┌──────────────────┐
   │ Firebase Auth    │
   │  Authentication  │
   └────────┬─────────┘
            │
            ▼
   ┌──────────────────┐
   │  MainActivity    │
   │   Dashboard      │
   │                  │
   │  Shows:          │
   │  • Cards due: X  │
   │  • Total cards: Y│
   │  • Features list │
   └────────┬─────────┘
            │
      ┌─────┴─────┬─────────┬────────┐
      │           │         │        │
      ▼           ▼         ▼        ▼
 ┌────────┐  ┌─────────┐ [Stats] [Settings]
 │ Study  │  │  Decks  │
 │        │  │  List   │
 └───┬────┘  └────┬────┘
     │            │
     │            ▼
     │     ┌──────────────────┐
     │     │ DeckListActivity │
     │     │  • Skeletal Sys  │
     │     │  • Muscular Sys  │
     │     │  • Nervous Sys   │
     │     └────────┬─────────┘
     │              │
     │     [Select Deck]
     │              │
     └──────────────┘
                │
                ▼
      ┌──────────────────────┐
      │   StudyActivity      │
      │                      │
      │  1. Show Question    │
      │  2. User clicks      │
      │     "Show Answer"    │
      │  3. Display Answer   │
      │  4. Rating Buttons:  │
      │     ┌──────────────┐ │
      │     │ Easy   │ Good│ │
      │     │ Hard   │Again│ │
      │     └──────────────┘ │
      └──────────┬───────────┘
                 │
                 ▼
      ┌──────────────────────┐
      │ ML ALGORITHM         │
      │ SpacedRepetition     │
      │  - Calculate EF      │
      │  - Update interval   │
      │  - Schedule next     │
      │    review            │
      └──────────┬───────────┘
                 │
                 ▼
      ┌──────────────────────┐
      │ Update Database      │
      │  StudyProgress       │
      └──────────┬───────────┘
                 │
                 ▼
      ┌──────────────────────┐
      │  Next Card or        │
      │  Session Complete    │
      └──────────────────────┘
```

### Study Session Flow (Detailed)

```
STUDY SESSION STARTS
         │
         ▼
┌─────────────────────────┐
│ Load Due Cards          │
│ (Query StudyProgress    │
│  where nextReviewDate   │
│  <= current time)       │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│ Sort by Priority        │
│ (Most urgent first)     │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│ Display Card #1         │
│ ┌─────────────────────┐ │
│ │   What is the      │ │
│ │   largest bone in   │ │
│ │   the human body?   │ │
│ └─────────────────────┘ │
│  [Show Answer Button]   │
└────────┬────────────────┘
         │
    [User clicks]
         │
         ▼
┌─────────────────────────┐
│ Show Answer             │
│ ┌─────────────────────┐ │
│ │ The femur           │ │
│ │ (thigh bone)        │ │
│ └─────────────────────┘ │
│                         │
│ How well did you know?  │
│ ┌────────┬────────┐    │
│ │  Easy  │  Good  │    │
│ │(5 days)│(2 days)│    │
│ ├────────┼────────┤    │
│ │  Hard  │ Again  │    │
│ │(10 min)│(1 min) │    │
│ └────────┴────────┘    │
└────────┬────────────────┘
         │
    [User selects rating]
         │
         ▼
┌─────────────────────────┐
│ ML ALGORITHM PROCESSES  │
│                         │
│ If rating ≥ 3 (Good):   │
│   ✓ Increase interval   │
│   ✓ Update ease factor  │
│   ✓ Schedule future     │
│                         │
│ If rating < 3 (Hard):   │
│   ✗ Reset interval      │
│   ✗ Review soon         │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│ Show Toast Notification │
│ "Next review: in 2 days"│
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│ Move to Next Card       │
│ OR                      │
│ "Session Complete!"     │
└─────────────────────────┘
```

---

## Installation & Setup

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 8 or higher
- Android SDK (API 26)
- Firebase account (for authentication)

### Step 1: Clone the Repository

```bash
git clone https://github.com/yourusername/studyblank.git
cd studyblank
```

### Step 2: Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or use existing
3. Add an Android app:
   - Package name: `com.example.bao48.studyblank`
   - Download `google-services.json`
4. Place `google-services.json` in `/app/` directory
5. Enable **Email/Password** authentication in Firebase Console

### Step 3: Build and Run

```bash
# Open in Android Studio
# Build > Make Project
# Run > Run 'app'
```

Or via command line:

```bash
./gradlew assembleDebug
./gradlew installDebug
```

### Step 4: Test the App

1. Launch app on emulator or device
2. Create an account with email/password
3. Login
4. View dashboard showing 35 pre-loaded cards
5. Start a study session
6. Rate cards and observe ML scheduling

---

## Code Structure

### Directory Layout

```
studyblank/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/bao48/studyblank/
│   │   │   │   ├── algorithm/
│   │   │   │   │   └── SpacedRepetitionAlgorithm.java  ◀── ML CORE
│   │   │   │   ├── database/
│   │   │   │   │   ├── AppDatabase.java
│   │   │   │   │   ├── DatabaseInitializer.java
│   │   │   │   │   ├── DeckDao.java
│   │   │   │   │   ├── FlashcardDao.java
│   │   │   │   │   └── StudyProgressDao.java
│   │   │   │   ├── models/
│   │   │   │   │   ├── Deck.java
│   │   │   │   │   ├── Flashcard.java
│   │   │   │   │   └── StudyProgress.java  ◀── ML STATE
│   │   │   │   ├── home.java                 (Login)
│   │   │   │   ├── createAccount.java        (Register)
│   │   │   │   ├── MainActivity.java         (Dashboard)
│   │   │   │   ├── StudyActivity.java        (Study Session)
│   │   │   │   └── DeckListActivity.java     (Browse Decks)
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_study.xml
│   │   │   │   │   ├── activity_deck_list.xml
│   │   │   │   │   ├── content_main.xml
│   │   │   │   │   └── ...
│   │   │   │   └── values/
│   │   │   │       ├── strings.xml
│   │   │   │       ├── colors.xml
│   │   │   │       └── styles.xml
│   │   │   └── AndroidManifest.xml
│   ├── build.gradle  ◀── Dependencies
│   └── google-services.json  ◀── Firebase config (NOT in repo)
├── build.gradle
├── settings.gradle
├── README.md
└── DOCUMENTATION.md  ◀── This file
```

### Key Files

#### SpacedRepetitionAlgorithm.java
**Location**: `algorithm/SpacedRepetitionAlgorithm.java`
**Purpose**: The ML core of the application

**Key Methods**:
- `updateProgress(StudyProgress, int quality)`: Updates learning progress based on user rating
- `isDueForReview(StudyProgress)`: Checks if card is due for review
- `calculatePriority(StudyProgress)`: Calculates urgency score
- `getNextReviewDescription(StudyProgress)`: Human-readable next review time

#### StudyActivity.java
**Location**: `StudyActivity.java`
**Purpose**: Main study session interface

**Features**:
- Displays flashcards one at a time
- Shows question, then reveals answer
- Four rating buttons (Easy, Good, Hard, Again)
- Integrates with ML algorithm
- Updates database asynchronously

#### AppDatabase.java
**Location**: `database/AppDatabase.java`
**Purpose**: Room database singleton

**Features**:
- Manages SQLite database
- Provides DAO instances
- Thread-safe singleton pattern

#### DatabaseInitializer.java
**Location**: `database/DatabaseInitializer.java`
**Purpose**: Populates sample data

**Content**:
- 3 Gray's Anatomy decks
- 35 flashcards total
- Covers Skeletal, Muscular, and Nervous systems

---

## Future Enhancements

### Planned Features

1. **Statistics Dashboard**
   - Daily/weekly/monthly study charts
   - Accuracy trends over time
   - Most difficult cards report
   - Study streak tracking

2. **Deck Management**
   - Create custom decks
   - Edit flashcards
   - Import/export functionality (JSON, CSV)
   - Share decks with other users

3. **Enhanced ML Features**
   - Multiple algorithm options (Leitner, FSRS)
   - ML-based difficulty prediction
   - Optimal study time suggestions
   - Learning pattern analysis

4. **UI/UX Improvements**
   - Dark mode
   - Card flip animations
   - Gesture-based navigation (swipe for ratings)
   - Voice input for answers

5. **Content Enhancements**
   - Image occlusion for anatomy diagrams
   - Audio pronunciation support
   - Video explanations
   - Interactive 3D models

6. **Social Features**
   - Share progress with friends
   - Leaderboards
   - Study groups
   - Collaborative decks

7. **Cloud Sync**
   - Firebase Firestore integration
   - Multi-device sync
   - Backup and restore
   - Offline-first architecture

8. **Gamification**
   - Achievement badges
   - XP and levels
   - Daily challenges
   - Rewards system

---

## Performance Considerations

### Database Optimization
- Indexed foreign keys for fast queries
- Cascade deletes to maintain referential integrity
- Singleton pattern prevents multiple DB instances

### Memory Management
- AsyncTask for background operations
- Lazy loading of flashcard images
- Efficient RecyclerView for large lists

### Algorithm Efficiency
- O(1) time complexity for priority calculation
- O(log n) sorting for due cards
- Minimal database queries per session

---

## Testing

### Unit Tests (Recommended)
```java
// Test SM-2 algorithm calculations
@Test
public void testEaseFactorCalculation() {
    StudyProgress progress = new StudyProgress(1, "user123");
    progress = SpacedRepetitionAlgorithm.updateProgress(progress, 5);
    assertEquals(2.6, progress.getEaseFactor(), 0.01);
}

// Test interval scheduling
@Test
public void testIntervalProgression() {
    StudyProgress progress = new StudyProgress(1, "user123");
    progress = SpacedRepetitionAlgorithm.updateProgress(progress, 3);
    assertEquals(1, progress.getInterval()); // First correct = 1 day

    progress = SpacedRepetitionAlgorithm.updateProgress(progress, 4);
    assertEquals(6, progress.getInterval()); // Second correct = 6 days
}
```

### Manual Testing Checklist
- [ ] User registration works
- [ ] Login/logout functionality
- [ ] Dashboard displays correct stats
- [ ] Flashcards display properly
- [ ] Rating buttons update progress
- [ ] ML algorithm calculates correct intervals
- [ ] Database persists data
- [ ] Navigation drawer works
- [ ] Deck list shows all decks

---

## Troubleshooting

### Common Issues

**Issue**: App crashes on login
**Solution**: Ensure `google-services.json` is in `/app/` directory

**Issue**: No cards due for review
**Solution**: Cards start with `nextReviewDate = now`, so they should appear immediately. Check database initialization.

**Issue**: Database not populating
**Solution**: Clear app data and restart. Check `DatabaseInitializer.initializeIfEmpty()`

**Issue**: Firebase authentication fails
**Solution**: Enable Email/Password authentication in Firebase Console

---

## License

This project is for educational purposes. Gray's Anatomy content is used for demonstration.

---

## Credits

- **Algorithm**: SuperMemo 2 (SM-2) by Piotr Wozniak
- **Content**: Gray's Anatomy
- **Framework**: Android SDK, Firebase, Room

---

## Contact

For questions or contributions, please open an issue on GitHub.

---

**Last Updated**: November 2025
**Version**: 1.0.0
