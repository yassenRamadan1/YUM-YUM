# 🍲 YUMYUM2 - Food Planner App

**YUMYUM2** is a comprehensive Android food planner application built with **Java** and **XML**. It is designed to help users discover recipes, manage favorite meals, and plan their weekly diet.

The app follows a clean **MVP (Model-View-Presenter)** architectural pattern supported by the **Repository Pattern** to ensure a clear separation of concerns and a seamless offline-first experience.

---
<img width="1080" height="1920" alt="Blue Gradient Modern Smartphone With Hand Mockup Your Story" src="https://github.com/user-attachments/assets/3f275bca-37b4-44b2-820a-670976642e8a" />

<img width="1080" height="1080" alt="UI Design Social Media Professional Mockup Promotion 3D" src="https://github.com/user-attachments/assets/42094f99-ec05-4e36-b3ae-747af4289bc6" />

## 👥 Team Members

* **Yassen Ramdan** 

---

## 📱 Overview & Features

YUMYUM2 integrates **TheMealDB** API for recipe data, **Firebase** for authentication and cloud synchronization, and **Room** for local persistence.

* **📖 Guest Mode:** Browse recipes without login (saving favorites/planning disabled).
* **🔍 Advanced Search:** Filter by Category, Area (Cuisine), or Main Ingredient.
* **👨‍🍳 Meal Details:** Full ingredients, measurements, instructions, and in-app YouTube tutorials.
* **📅 Weekly Planner:** Schedule meals for specific days of the week.
* **☁️ Cloud Sync:** Auto-sync favorites and weekly plans via Firestore.
* **📶 Offline Support:** Local caching ensures access to planned meals without internet.

---

## 🛠 Tech Stack

| Property | Value |
| :--- | :--- |
| **Language** | Java 11 |
| **Minimum SDK** | API 24 (Android 7.0) |
| **Target SDK** | API 36 |
| **Compile SDK** | API 36 |
| **Architecture** | MVP + Repository Pattern |
| **Async/Reactive** | RxJava3 |
| **Network** | Retrofit |
| **Local DB** | Room (SQLite) |
| **Cloud** | Firebase (Auth, Firestore) |
| **UI** | XML, View Binding, Glide |

---

## 🏗 Architecture

The application uses the **MVP** pattern to decouple the UI (View) from the business logic (Presenter), with a **Repository** mediating data from Network, Database, and Cache.

### High-Level Architecture Diagram

```text
┌───────────────────────────────────────────────────────────────┐
│                   PRESENTATION LAYER (MVP)                    │
├───────────────────────────────────────────────────────────────┤
│  Fragments (Views)    │  Presenters         │  Contracts      │
│  - HomeScreen         │  - HomePresenter    │  - HomeContract │
│  - SearchScreen       │  - SearchPresenter  │  - SearchContract │
│  - LoginScreen        │  - LoginPresenter   │  - LoginContract  │
│  - MealDetailsScreen  │  - MealDetailsPres  │  - MealDetailsCon │
│  - FavoriteScreen     │  - FavoritePresenter│  - FavoriteContract│
│  - WeaklyMealsScreen  │  - WeeklyPresenter  │  - WeeklyContract │
└──────────────────────┴────────────────────┴───────────────────┘
                        │
             (Presenters call Repositories)
                        │
                        ▼
┌───────────────────────────────────────────────────────────────┐
│                DATA LAYER (Repository Pattern)                │
├───────────────────────────────────────────────────────────────┤
│  MealsRepository                 │  AuthRepository            │
│  ├─ MealsNetworkDataSource       │  ├─ AuthRemoteDataSource   │
│  ├─ MealLocalDataSource          │  └─ UserLocalDataSource    │
│  ├─ MealFirestoreDataSource      │     (SharedPreferences)    │
│  └─ DailyCachePreferences        │                            │
└──────────────────────────────────┴────────────────────────────┘
         │               │               │
         ▼               ▼               ▼
  ┌────────────┐   ┌────────────┐   ┌─────────────┐
  │  Retrofit  │   │   Room DB  │   │   Firebase  │
  │ (TheMealDB)│   │  (SQLite)  │   │ (Firestore) │
  └────────────┘   └────────────┘   └─────────────┘
```

### The MVP Contract
Each feature implements a Contract interface:

```java
public interface HomeContract {
    interface View {
        void showLoading();
        void showDailyMeal(Meal meal);
        void showError(String message);
    }
    interface Presenter {
        void getHomeContent();
        void onDestroy();
    }
}
```

---

## 📂 Project Structure

```text
com.example.yum_yum/
│
├── MainActivity.java                # Entry point, NavController
│
├── data/
│   ├── auth/                        # Auth Repo & Data Sources
│   ├── db/                          # Room Database & DAO
│   ├── meals/
│   │   ├── datasource/              # Local, Network, & Firestore Sources
│   │   ├── dto/                     # API Data Transfer Objects
│   │   └── repository/              # MealsRepository & Mappers
│   └── network/                     # Retrofit Client
│
└── presentation/
    ├── home/                        # Home Screen MVP
    ├── search/                      # Search & Filters MVP
    ├── login/                       # Authentication MVP
    ├── mealDetails/                 # Recipe Details MVP
    ├── profile/                     # User Profile MVP
    ├── Favorite/                    # Favorites MVP
    └── weaklyMeals/                 # Weekly Planner MVP
```

---

## 💾 Data Layer Details

### Local Database (Room)
The `MealEntity` table stores cached, favorite, and planned meals.

| Column | Type | Description |
| :--- | :--- | :--- |
| `id` | String (PK) | Unique Meal ID |
| `name` | String | Meal Name |
| `category` | String | Cuisine Category |
| `is_favorite` | boolean | Favorite Status |
| `planned_date` | String | YYYY-MM-DD for planner |
| `ingredients_json`| String | Serialized ingredients |
| `measures_json` | String | Serialized measures |
| `user_id` | String | Links data to specific user |

### Reactive Programming (RxJava)
All heavy operations are handled on background threads:
* **Retrofit** returns `Single<MealResponse>`
* **Room DAO** returns `Flowable<List<MealEntity>>` for live updates.
* **Presenters** manage `CompositeDisposable` to prevent memory leaks.

---

## 🚀 How to Run

### Prerequisites
1.  **Android Studio:** Jellyfish or newer recommended.
2.  **JDK:** Java 11.
3.  **Firebase Project:** Required for Authentication and Firestore.

### Installation & Setup

1.  **Clone the Repository**
    ```bash
    git clone [https://github.com/your-username/yumyum2.git](https://github.com/your-username/yumyum2.git)
    ```

2.  **Firebase Configuration**
    * Go to [Firebase Console](https://console.firebase.google.com/).
    * Create a project and add an Android App with package: `com.example.yum_yum`.
    * Download `google-services.json` and place it in the `app/` directory.
    * **Auth:** Enable Email/Password and Google Sign-In.
    * **Firestore:** Create a database (test mode recommended for development).

3.  **Build**
    * Open project in Android Studio.
    * Sync Gradle.
    * Run on Emulator or Device (API 24+).

---

## 🕹 How to Use

1.  **Home:** View the "Meal of the Day" and meals by country.
2.  **Search:** Use the magnifying glass icon to search by text.
3.  **Filter:** Click the filter icon in Search to browse by Category, Area, or Ingredient.
4.  **Plan:** In Meal Details, click **"Add to Plan"** to select a date.
5.  **Sync:** Log in to sync your data across devices.
