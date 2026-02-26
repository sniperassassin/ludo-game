Ludo Game — MVP Spec

Overview
- Platform: Android (Kotlin + Jetpack Compose)
- Build system: Gradle
- Backend: Separate sibling repo `ludo-game-backend` for real-time rooms (we'll use Firebase for prototyping)
- Modes: Offline single-player (optional AI later), Local pass-and-play / local multiplayer, Online multiplayer via room code

MVP Features
- Core Ludo rules: 2-4 players, dice roll, move tokens, safe squares, home, win condition
- Game modes:
  - Local pass-and-play (up to 4 players on the same device)
  - Online rooms with room code (create/join)
- Player profiles: simple display name and color selection
- Basic UI screens: Home, Create/Join Room, Lobby, Game board, Settings
- Networking: room creation, join by code, sync game state (turns, dice, token positions), minimal latency handling and reconnection
- Persistence: local save of player name/settings; optional resume of single/local games

Non-MVP (future)
- AI opponents (difficulty levels)
- Matchmaking, ranked play
- In-app purchases, ads
- Rich animations and sounds

Architecture (high-level)
- Android app (Kotlin + Jetpack Compose)
  - Modules: `app` (UI + platform), `core` (game logic, rules), `network` (sync + Firebase wrapper)
  - Use MVVM with `ViewModel`s and Kotlin Coroutines / Flow for state
- Backend (sibling repo: `ludo-game-backend`)
  - For prototyping, use Firebase Realtime Database or Firestore and Cloud Functions
  - For production/advanced control, implement Node.js WebSocket server in `ludo-game-backend`

Data model (simple)
- GameState: players[], board[], currentTurn, diceValue, moveHistory[], roomId
- Player: id, name, color, tokens[position/status]

Room workflow
- Create room → generate short room code → host waits in lobby → players join with code → host starts game
- Room owner (host) manages start; if host disconnects, choose next host
- Sync: authoritative game state stored in backend; clients receive deltas

Minimal folder layout (repo: `ludo-game`)
- /app
  - build.gradle.kts
  - src/main/java/... (Android code)
  - src/main/res/... (assets)
- /core
  - src/main/kotlin/... (game rules + unit tests)
- /network
  - src/main/kotlin/... (Firebase / networking abstractions)
- /scripts (dev scripts)
- MVP_SPEC.md
- README.md

Next steps (recommended)
1. Scaffold Gradle Android project with modules `app`, `core`, `network`.
2. Implement `core` game logic with unit tests (rules, moves, turn handling).
3. Build a simple Compose board UI and integrate `core` state for local pass-and-play.
4. Add Firebase-based room prototype and implement room code create/join.

Quick commands (when ready)
- Create project (Android Studio or CLI):

```bash
# create Gradle project (recommended via Android Studio for Android specifics)
```

Notes
- You said the backend will be a sibling repo named `ludo-game-backend`; we'll keep networking interfaces small so the backend can be swapped (Firebase vs custom WebSocket server).
- You prefer Gradle — I'll scaffold with Gradle Kotlin DSL (`build.gradle.kts`) when you confirm to scaffold the project.
