# StudyBlank 🧠📚

**An Intelligent Flashcard App for Anatomy & Biology Mastery**

StudyBlank is an Android application that uses **machine learning-powered spaced repetition** to help students master anatomy and biology. Built with the scientifically-proven SM-2 algorithm, it optimizes your study schedule based on your performance, ensuring maximum retention with minimum effort.

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![License](https://img.shields.io/badge/License-Educational-blue.svg)]()
[![Min SDK](https://img.shields.io/badge/Min%20SDK-15-orange.svg)]()

## 🌟 Key Features

### 🧠 ML-Powered Learning
- **Spaced Repetition Algorithm (SM-2)**: Intelligent scheduling adapts to YOUR learning pace
- **Adaptive Difficulty**: Cards automatically adjust based on your performance
- **Priority Queue**: Focus on cards that need review most urgently
- **Performance Analytics**: Track accuracy and progress for every flashcard

### 📚 Rich Content
- **Pre-loaded Gray's Anatomy Decks**: 35+ flashcards across 3 systems
  - Skeletal System (15 cards)
  - Muscular System (10 cards)
  - Nervous System (10 cards)
- Ready-to-study content from day one

### 🎯 Smart Study Sessions
- **Optimized Review Timing**: Reviews scheduled just before you forget
- **Instant Feedback**: Rate your performance (Easy, Good, Hard, Again)
- **Progress Tracking**: See due cards, total cards, and accuracy metrics
- **Session Management**: Study only what's due, when it's due

### 🔐 Secure & Personal
- **Firebase Authentication**: Secure user accounts with email/password
- **Local Data Storage**: Room database for fast, offline access
- **Per-user Progress**: Your learning journey is uniquely yours

## 📱 Screenshots

```
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│  Login Screen   │  │   Dashboard     │  │  Study Session  │
│                 │  │                 │  │                 │
│  [  Email  ]    │  │  📊 Due: 12     │  │ ┌─────────────┐ │
│  [ Password ]   │  │  📚 Total: 35   │  │ │  Question?  │ │
│                 │  │                 │  │ └─────────────┘ │
│  [ LOGIN ]      │→│  🧠 ML Learning │→│  [ Show Answer] │
│  [CREATE ACCT]  │  │  📚 Gray's Anat │  │                 │
│                 │  │  📊 Progress    │  │ [Easy][Good]    │
│                 │  │                 │  │ [Hard][Again]   │
└─────────────────┘  └─────────────────┘  └─────────────────┘
```

## 🚀 Quick Start

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 8+
- Android device/emulator (API 15+)
- Firebase account

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yeungjosh/studyblank.git
   cd studyblank
   ```

2. **Set up Firebase**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a project and add Android app
   - Package name: `com.example.bao48.studyblank`
   - Download `google-services.json` and place in `/app/` directory
   - Enable Email/Password authentication

3. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ./gradlew installDebug
   ```

   Or open in Android Studio and click ▶ Run

4. **Start Learning!**
   - Create an account
   - Login to see dashboard
   - Start studying with pre-loaded content

## 🧠 How the ML Algorithm Works

### The SM-2 Spaced Repetition Algorithm

StudyBlank uses the **SuperMemo 2 algorithm**, a proven method for optimizing memory retention:

```
┌─────────────────────────────────────────────────────────────┐
│                    LEARNING WORKFLOW                        │
└─────────────────────────────────────────────────────────────┘

You study a flashcard
        │
        ▼
Rate your answer (0-5)
        │
        ├─────────────┬─────────────┐
        ▼             ▼             ▼
    Wrong (0-2)   Okay (3-4)    Perfect (5)
        │             │             │
        ▼             ▼             ▼
   Review in      Review in     Review in
   10 minutes     1-2 days      5+ days
        │             │             │
        └─────────────┴─────────────┘
                      │
                      ▼
            Algorithm adjusts:
            • Ease Factor (difficulty)
            • Review Interval
            • Next Review Date
```

### Rating Scale

| Rating | Button | Meaning | Next Review |
|--------|--------|---------|-------------|
| 5 | **Easy** | Perfect recall | 5+ days |
| 4 | **Good** | Correct after thought | 2-3 days |
| 3 | **Good** | Correct with difficulty | 1-2 days |
| 2 | **Hard** | Incorrect but familiar | 10 minutes |
| 1 | **Hard** | Barely remembered | 10 minutes |
| 0 | **Again** | Complete blackout | 1 minute |

### Why It Works

1. **Forgetting Curve**: Reviews are scheduled just before you're likely to forget
2. **Personalized**: Each card adapts to YOUR performance
3. **Efficient**: More time on hard cards, less on easy ones
4. **Scientific**: Based on decades of memory research

## 🏗️ Architecture

### Technology Stack

- **Language**: Java
- **Database**: Room (SQLite)
- **Auth**: Firebase Authentication
- **UI**: Material Design, CardView, RecyclerView
- **Algorithm**: SM-2 Spaced Repetition
- **Min SDK**: Android 4.1 (API 16)
- **Target SDK**: Android 9.0 (API 28)

### Project Structure

```
studyblank/app/src/main/java/com/example/bao48/studyblank/
├── algorithm/                    ← ML Core
│   └── SpacedRepetitionAlgorithm.java
├── database/                     ← Data Layer
│   ├── AppDatabase.java
│   ├── DatabaseInitializer.java
│   ├── DeckDao.java
│   ├── FlashcardDao.java
│   └── StudyProgressDao.java
├── models/                       ← Entities
│   ├── Deck.java
│   ├── Flashcard.java
│   └── StudyProgress.java        ← ML State
├── Activities (root package)     ← UI Layer
│   ├── home.java                 (Login)
│   ├── createAccount.java        (Register)
│   ├── MainActivity.java         (Dashboard)
│   ├── StudyActivity.java        (Study)
│   └── DeckListActivity.java     (Browse)
└── app/src/main/res/             ← Resources
    ├── layout/
    │   ├── activity_study.xml
    │   ├── activity_deck_list.xml
    │   └── content_main.xml
    └── values/
        ├── strings.xml
        └── colors.xml
```

### Database Schema

```
┌──────────┐    1:N    ┌─────────────┐    1:N    ┌────────────────┐
│  Decks   │───────────│ Flashcards  │───────────│ StudyProgress  │
└──────────┘           └─────────────┘           └────────────────┘
    │                        │                           │
    │                        │                           │
    ├─ name                  ├─ question                 ├─ easeFactor  ←─┐
    ├─ description           ├─ answer                   ├─ repetitions   │
    ├─ category              ├─ imageUrl                 ├─ interval      │ ML
    └─ cardCount             └─ deckId                   ├─ nextReview   │ State
                                                          ├─ accuracy    ←─┘
                                                          └─ userId
```

## 📖 Documentation

For detailed technical documentation, see [DOCUMENTATION.md](DOCUMENTATION.md):

- Complete architecture diagrams
- ML algorithm deep dive with formulas
- Database schema with ERD
- User flow diagrams
- Code structure guide
- API reference
- Testing guide

## 🎓 Usage Guide

### First Time Setup

1. **Create Account**
   - Open app → "Create Account"
   - Enter email and password
   - Verify email (check spam folder)
   - Login with credentials

2. **Explore Dashboard**
   - View statistics (cards due, total cards)
   - Check out features
   - Open navigation drawer (☰)

3. **Start Studying**
   - Click "Study" from menu
   - View flashcard question
   - Click "Show Answer"
   - Rate your knowledge (Easy/Good/Hard/Again)
   - Algorithm schedules next review

4. **Browse Decks**
   - Click "My Decks" from menu
   - View all available decks
   - See card counts
   - Tap deck to study

### Study Best Practices

✅ **DO:**
- Study daily for consistency
- Be honest with ratings
- Review all due cards
- Take breaks between sessions

❌ **DON'T:**
- Cram all cards at once
- Always click "Easy" (defeats the algorithm)
- Skip days (breaks the schedule)
- Study when tired

## 🔬 The Science Behind It

### Why Spaced Repetition?

Spaced repetition is based on the **forgetting curve**, discovered by psychologist Hermann Ebbinghaus:

```
Memory Retention Over Time

100% ├─┐
     │ └─┐                         ← With spaced repetition
     │   └─┐                       (reviews at optimal times)
 50% │     └─┐─────────────────
     │       └─┐
     │         └─┐         ← Without review
  0% └───────────└──────────────────────────
     0   1   3   7   14  30 days
```

### Research Backing

- **Ebbinghaus (1885)**: Forgetting curve research
- **Wozniak (1990)**: SuperMemo algorithm development
- **Cepeda et al. (2006)**: Meta-analysis on spacing effects
- **Dunlosky et al. (2013)**: Rated spaced repetition as highly effective

## 🛠️ Development

### Build from Source

```bash
# Clone
git clone https://github.com/yeungjosh/studyblank.git
cd studyblank

# Build
./gradlew clean
./gradlew assembleDebug

# Install
./gradlew installDebug

# Run tests
./gradlew test
```

### Dependencies

```gradle
// Room Database
implementation 'android.arch.persistence.room:runtime:1.0.0'
annotationProcessor 'android.arch.persistence.room:compiler:1.0.0'

// Firebase
implementation 'com.google.firebase:firebase-auth:11.0.4'

// UI
implementation 'com.android.support:recyclerview-v7:26.1.0'
implementation 'com.android.support:cardview-v7:26.1.0'

// JSON
implementation 'com.google.code.gson:gson:2.8.2'
```

## 🚧 Roadmap

### Version 2.0 (Planned)
- [ ] Statistics dashboard with charts
- [ ] Custom deck creation
- [ ] Import/export decks (JSON, CSV)
- [ ] Dark mode
- [ ] Card flip animations
- [ ] Image support for diagrams

### Version 3.0 (Future)
- [ ] Cloud sync across devices
- [ ] Collaborative decks
- [ ] Voice input
- [ ] Multiple algorithm options
- [ ] Gamification (achievements, XP)
- [ ] Social features (leaderboards, sharing)

## 🤝 Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is for educational purposes. Gray's Anatomy content is used for demonstration.

## 🙏 Acknowledgments

- **SuperMemo**: For the SM-2 algorithm
- **Gray's Anatomy**: For anatomical content
- **Firebase**: For authentication services
- **Android Team**: For excellent development tools

## 📞 Contact & Support

- **Issues**: [GitHub Issues](https://github.com/yeungjosh/studyblank/issues)
- **Discussions**: [GitHub Discussions](https://github.com/yeungjosh/studyblank/discussions)

## 📊 Project Stats

- **Total Code**: ~3,000 lines
- **Activities**: 5
- **Database Tables**: 3
- **Pre-loaded Cards**: 35
- **Supported Languages**: English
- **Min Android Version**: 4.1 (Jelly Bean)
- **Target Android Version**: 9.0 (Pie)

---

**Made with ❤️ for students struggling with anatomy**

*"The best time to plant a tree was 20 years ago. The second best time is now. The same applies to learning."*
