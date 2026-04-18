# AGENTS.md

## Project Overview
Android Connect Four game (Java, Gradle) with configurable grid size (6×6+) and 2–5 players (mix of human and random AI). Package: `com.example.learn2`.

## Architecture

### Two-layer separation
- **Game logic** (`app/src/main/java/com/example/learn2/gameLogic/`): `Board`, `Player` (abstract), `HumanPlayer`, `RandomPlayer`. Pure game state — no Android imports except `android.graphics.Point`.
- **UI/Views** (`app/src/main/java/com/example/learn2/`): Custom `View` subclasses that handle drawing and touch. `GameBoardView` is the main game view; `PlayerTurnView` shows player indicators.

### Data flow
1. `MainActivity` → collects grid size, human count, AI count via SeekBars → passes as Intent extras (`gridSize`, `humanSize`, `AISize`) to `GameWindow`.
2. `GameWindow` just sets `activity_game_window.xml` which contains `GameBoardView` and `PlayerTurnView` custom views.
3. Custom views read Intent extras from host Activity in their constructors (`((Activity) context).getIntent()`).
4. `GameBoardView` owns a `Board` instance and a `Player[]` array; manages turn order via `currentPlayerIndex` with circular indexing.

### Key patterns
- **Board uses 1-indexed columns**: `boardMakeMove(column + 1, player)` — the Board internally subtracts 1. Always pass 1-indexed column to `Board.boardMakeMove()`.
- **Win detection** returns the winning `Player` reference (identity-based, not `.equals()`). `null` means no winner.
- **Cell paint is static**: `Cell.paint` is a shared static field. Call `Cell.setHuman1LastPaint()` etc. before drawing to set the current drop color. Instance methods like `getHumanPaint1()` create new `Paint` objects for redrawing occupied cells.
- **Drop animation**: `GameBoardView.drawLastMovePlayed()` animates by incrementing `currentY` toward `targetY`, using `Handler.postDelayed` + `invalidate()` at ~13ms intervals.
- **Touch gating**: `AtomicBoolean isTouchEnabled` prevents input during animations/AI turns; re-enabled via `postDelayed`.

## Build & Run
```bash
./gradlew assembleDebug          # Build debug APK
./gradlew installDebug           # Install on connected device/emulator
./gradlew test                   # Unit tests
./gradlew connectedAndroidTest   # Instrumented tests
```
- Java 8 source compatibility, compileSdk 37, minSdk 26
- Version catalog at `gradle/libs.versions.toml`

## Known Issues / WIP
- `startWinningAnimation()` in `GameBoardView` is stubbed out (body commented out)
- `PlayerTurnView` doesn't visually indicate whose turn it is yet — `onDraw` just draws blank cells
- Bug noted in code: two consecutive `RandomPlayer` turns have timing issues (see comment "Bug here right now" ~line 167 of `GameBoardView`)
- `Cell.paint` being static is a latent bug — constructing any `Cell` overwrites the shared paint

## Adding a New Player Type
1. Create a subclass of `Player` in `gameLogic/`
2. Add color methods in `Cell` (static setter + instance getter)
3. Wire into `GameBoardView` constructor's player-array setup and `drawOldBoard()`'s identity checks

