## Assignment 5 — Q1–Q3 Overview

This module contains three independent sample apps implemented with Kotlin, Jetpack Compose, Navigation Compose, ViewModel, and StateFlow. Each screen is an `Activity` and can be launched from Android Studio directly.

Activities registered in `AndroidManifest.xml`:
- `Q1Activity` — What's for Dinner? (Recipe Manager)
- `Q2Activity` — My Daily Hub (Notes & Tasks)
- `Q3Activity` — Explore Boston (Tour Guide)

All three activities are marked `exported=true` so they can be run individually.

### Q1 — What's for Dinner? (Recipe Manager with Navigation)
- Purpose: Manage recipes with ingredients and cooking steps, using Navigation Compose for multi-screen navigation.
- UI: 
  - Bottom navigation bar with Home, Add Recipe, and Settings tabs
  - Home screen: List of recipe cards in a `LazyColumn`
  - Detail screen: Full recipe view with ingredients and steps
  - Add Recipe screen: Form to create new recipes with title, ingredients, and steps
  - Settings screen: Placeholder for future settings
- Navigation:
  - Uses `NavHost` with routes: `home`, `detail/{recipeId}`, `add_recipe`, `settings`
  - Deep linking support for recipe details
  - Bottom navigation bar with Material icons
- State model (in `ui/RecipeViewModel.kt`):
  - `Recipe(id: String, title: String, ingredients: List<String>, steps: List<String>)`
  - `RecipeUiState(recipes: List<Recipe>)`
- ViewModel responsibilities:
  - Expose `uiState: StateFlow<RecipeUiState>`
  - `addRecipe(recipe: Recipe)` appends a new recipe
  - `getRecipe(id: String)` retrieves a recipe by ID
  - Initializes with sample recipes (Pasta Carbonara, Chocolate Chip Cookies)
- How I used AI:
  - Helping debuging, such as some gradle build problems.
  - Creating specific recipe information.
  - Helping checking whether the app can satisfy the requirements.

### Q2 — My Daily Hub (Multi-Tab App with Navigation)
- Purpose: Daily productivity app with Notes, Tasks, and Calendar tabs using bottom navigation.
- UI:
  - Bottom navigation bar with Notes, Tasks, and Calendar tabs
  - Notes screen: Add and delete text notes with timestamp
  - Tasks screen: Add, toggle completion, and delete tasks with checkboxes
  - Calendar screen: Placeholder for future calendar functionality
- Navigation:
  - Uses `NavHost` with routes: `notes`, `tasks`, `calendar`
  - Bottom navigation with restore state enabled
  - Each tab maintains its own state
- State models (in `ui/DailyHubViewModel.kt`):
  - `Note(id: String, text: String, timestamp: Long)`
  - `Task(id: String, text: String, isCompleted: Boolean)`
  - `NotesUiState(notes: List<Note>)`
  - `TasksUiState(tasks: List<Task>)`
- ViewModel responsibilities:
  - `NotesViewModel`: Expose `uiState: StateFlow<NotesUiState>`, `addNote(text)`, `deleteNote(noteId)`
  - `TasksViewModel`: Expose `uiState: StateFlow<TasksUiState>`, `addTask(text)`, `toggleTask(taskId)`, `deleteTask(taskId)`
- How I used AI:
  - Helping debuging, such as some gradle build problems.
  - Helping checking whether the app can satisfy the requirements.

### Q3 — Explore Boston (Tour Guide with Navigation & Animations)
- Purpose: Browse Boston attractions by category (Museums, Parks, Restaurants) with animated navigation transitions.
- UI:
  - Home screen: Welcome screen with "Start Exploring" button
  - Categories screen: List of category cards (Museums, Parks, Restaurants)
  - Category List screen: Locations filtered by selected category
  - Location Detail screen: Full information about a specific location
- Navigation:
  - Uses `NavHost` with routes: `home`, `categories`, `category/{categoryId}`, `location/{locationId}`
  - Custom enter/exit animations: fade + horizontal slide transitions (300ms tween)
  - Back button in TopAppBar when not on home screen
  - Deep linking support for categories and locations
- State model (in `ui/BostonTourViewModel.kt`):
  - `Location(id: Int, name: String, description: String, address: String, category: String)`
  - `Category(id: String, name: String, icon: String)`
- ViewModel responsibilities:
  - `BostonTourViewModel`: Holds predefined categories and locations
  - `getLocationsByCategory(categoryId: String)` filters locations by category
  - `getLocationById(id: Int)` retrieves a location by ID
- How I used AI:
  - Helping debuging, such as some gradle build problems.
  - Creating specific location information.
  - Helping checking whether the app can satisfy the requirements.
  - Smooth animated transitions between screens

### Common Architecture Notes
- All apps use Navigation Compose (`androidx.navigation.compose`) for multi-screen navigation.
- Compose collects state using `collectAsStateWithLifecycle()` from the Lifecycle Compose library.
- All mutation happens inside each screen's ViewModel, following unidirectional data flow.
- State management uses `StateFlow` and `MutableStateFlow` for reactive UI updates.
- Material Design 3 components (TopAppBar, NavigationBar, Cards, Buttons) are used throughout.
- All activities use `enableEdgeToEdge()` for modern Android edge-to-edge display.

### Build & Run
1. Open the project in Android Studio (Giraffe+ recommended).
2. Sync Gradle. Dependencies used include:
   - compose BOM, material3, activity-compose
   - lifecycle-runtime-compose, lifecycle-viewmodel-compose
   - navigation-compose
   - material-icons-extended
3. Run any of the activities directly:
   - Q1: `com.example.assignment5/.Q1Activity`
   - Q2: `com.example.assignment5/.Q2Activity`
   - Q3: `com.example.assignment5/.Q3Activity`

