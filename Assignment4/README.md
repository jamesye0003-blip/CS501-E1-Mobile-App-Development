## Assignment 4 — Q1–Q3 Overview

This module contains three independent sample apps implemented with Kotlin, Jetpack Compose, ViewModel, StateFlow and Coroutines. Each screen is an `Activity` and can be launched from Android Studio directly.

Activities registered in `AndroidManifest.xml`:
- `Q1Activity` — LifeTracker
- `Q2Activity` — Counter++
- `Q3Activity` — Temperature Dashboard

All three activities are marked `exported=true` so they can be run individually.

### Q1 — LifeTracker (Lifecycle-Aware Activity Logger)
- Purpose: Log and visualize Android lifecycle events in real time.
- UI: 
  - Current lifecycle state card with color indicator
  - A log list (`LazyColumn`) of events including timestamp and color code
- State model (in `ui/LifeTrackerViewModel.kt`):
  - `LifecycleEvent(state: String, timestamp: Long)`
  - `LifeTrackerUiState(currentState: String, events: List<LifecycleEvent>)`
- ViewModel responsibilities:
  - Expose `uiState: StateFlow<LifeTrackerUiState>`
  - `logLifecycleEvent(state: String)` appends a new event and updates current state
  - Survives configuration changes
- Lifecycle integration:
  - `Q1Activity` uses `LifecycleEventObserver` to listen for `ON_START/ON_RESUME/ON_PAUSE/ON_STOP/ON_DESTROY` and logs them via the ViewModel.
- Feedback pattern:
  - UI collects state via `collectAsStateWithLifecycle()` and re-composes declaratively.

### Q2 — Counter++ (Reactive UI with StateFlow & Coroutines)
- Purpose: Counter with manual +/-/Reset and an optional auto-increment that runs every N milliseconds.
- UI:
  - Title: "Counter++"
  - Status banner: shows "Auto mode: ON/OFF"
  - Counter card with buttons: `-`, `Reset`, `+`
  - Button to toggle auto mode (Start/Stop Auto)
- State model (in `ui/CounterPlusPlusViewModel.kt`):
  - `CounterPlusPlusUiState(count: Int, isAutoMode: Boolean, autoInterval: Long, status: String)`
- ViewModel responsibilities:
  - Expose `uiState: StateFlow<...>`
  - Intents: `increment()`, `decrement()`, `reset()`, `toggleAutoMode()`, `setAutoInterval(intervalMs)`
  - When auto mode is ON, a coroutine in `viewModelScope` increments by 1 after each `autoInterval` delay
  - Cancels the job when auto mode turns OFF or on `onCleared()`

### Q3 — Temperature Dashboard (Simulated Sensor Data)
- Purpose: Simulate temperature readings and visualize stats reactively.
- UI:
  - Start/Pause data generation button
  - Stats: Current / Average / Min / Max
  - Recent readings list (last 20, newest first)
- State model (in `ui/TemperatureViewModel.kt`):
  - `TemperatureReading(timestamp: Long, value: Float)`
  - `TemperatureUiState(readings: List<TemperatureReading>, isGenerating: Boolean, current: Float, average: Float, min: Float, max: Float)`
- ViewModel responsibilities:
  - Expose `uiState: StateFlow<...>`
  - `toggleDataGeneration()` starts/stops a coroutine that emits a random reading every 2 seconds in [65°F, 85°F]
  - Maintains only the last 20 readings and derives current/avg/min/max from the list

### Common Architecture Notes
- Compose collects state using `collectAsStateWithLifecycle()` from the Lifecycle Compose library.
- All mutation happens inside each screen's ViewModel, following unidirectional data flow.
- Long-running/periodic work is hosted in `viewModelScope` coroutines and is cancelled appropriately.

### Build & Run
1. Open the project in Android Studio (Giraffe+ recommended).
2. Sync Gradle. Dependencies used include:
   - compose BOM, material3, activity-compose
   - lifecycle-runtime-compose, lifecycle-viewmodel-compose
3. Run any of the activities directly:
   - Q1: `com.example.assignment4/.Q1Activity`
   - Q2: `com.example.assignment4/.Q2Activity`
   - Q3: `com.example.assignment4/.Q3Activity`

Optional: For Q2 auto-interval customization, call `setAutoInterval(ms)` from settings or a future UI control.


