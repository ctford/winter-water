## Winter Water - Song Overview

**Genre:** Progressive electronic
**Key:** F major
**Time Signature:** Mainly 7/8 (2+2+3 grouping), Bridge in 4/4
**Tempo:** 120 BPM (main sections), 60 BPM (bridge - halftime)
**Duration:** ~2 minutes 10 seconds (145.75 beats)

### Song Structure

- **Intro** (no drums) → **Intro** (with hihat)
- **A × 2** → **B × 2** → **A-doubled × 2** → **B-harmony × 2**
- **Intro-reprise × 2** (with bass) → **Bridge × 2** (reggae 4/4)
- **Double-chorus × 4** → **Outro** (texture only)

### Chord Progressions

- **Intro:** Bbsus2 → Dm7 → Bbmaj7 → Dm9 (power chord voicings)
- **Main sections:** Various triads in 7/8 time
- **Bridge:** F → C → Bb → Bb (reggae stabs on offbeats)

### Code Organization

The code is organized with clear section dividers:
- **Constants** - Bass filter/overdrive parameters
- **Shared Material** - Chord progressions, rhythms, melodies
- **Intro Section** - Opening material
- **Main Sections** - A & B sections with variations
- **Bridge Section** - Reggae 4/4 material
- **Instruments** - Synth definitions
- **Play Methods** - Instrument routing & mixing

## Architecture

This project uses:
- **Overtone 0.16.3331** - External SuperCollider connection only (no embedded binaries)
- **SuperCollider 3.14.0** - Installed via Homebrew with ARM64 (Apple Silicon) support
- **Java 17** - Managed via jenv for compatibility

The setup starts scsynth externally on port 57110, and Overtone connects to it.

**Note:** The primary branch is `main` (not `master`).

- Start a REPL for performance with `lein repl`.
- To trigger the bass synth: `echo "(bass 55 2.0)" | lein repl` (frequency, duration)
- For efficient repeated playing:
  1. Start headless nREPL server: `lein repl :headless :host 127.0.0.1` (runs in background, note the port)
  2. Connect and play: `echo "(bass 110 1.0)" | lein repl :connect 127.0.0.1:PORT`
  3. This avoids reloading Overtone each time since the server stays running

## Live Coding with MCP-nREPL

This project uses MCP-nREPL for both fast shell scripts and AI-assisted development.

### Setup
1. Start the servers: `./start-nrepl.sh`
   - Starts scsynth (SuperCollider) on port 57110 in background
   - Starts nREPL on port 7888 and creates `.nrepl-port` (runs in foreground)
   - Run in a separate terminal, or use `./start-nrepl.sh &` to background it
2. Stop the servers: `./stop-nrepl.sh`
   - Stops both scsynth and nREPL
   - Cleans up `.nrepl-port` file

### Two Ways to Control the Session

#### Option 1: Shell Scripts (Fast Performance Control)

For live performance where milliseconds matter:

- `./jam.sh` - Start jamming the winter-water track
- `./reload.sh` - Reload code changes
- `./stop.sh` - Stop jamming

These scripts use `mcp-nrepl.bb --eval` for direct nREPL execution (~0.1s response time).

#### Option 2: Claude Code (AI-Assisted Development)

For development, experimentation, and iteration:

**Start jamming:**
- "Start jamming winter-water"

**Reload code changes:**
- "Reload the core file"

**Stop playing:**
- "Stop the jam"

**Experiment:**
- "Try making the bass slower"
- "Play just the melody line"
- Claude can evaluate code, inspect state, and iterate

Claude Code connects via the `.mcp.json` configuration and has access to:
- `eval-clojure` - Evaluate any Clojure expression
- `load-file` - Reload Clojure source files
- `set-ns` - Switch namespaces
- `apropos` - Search for symbols

### Best of Both Worlds

- **Shell scripts** for muscle memory and instant control during performance
- **Claude Code** for exploratory coding, debugging, and trying variations
- **Same nREPL connection** - both approaches talk to the same running session
- **No Python dependency** - everything uses babashka via mcp-nrepl

## Development Tips

### Session State Persistence
After using `load-file` to reload code, the nREPL session retains all context. You can evaluate expressions directly without repeated `in-ns` or `require` calls:

```clojure
;; After load-file, just evaluate directly:
(duration bass-line)
(take 4 winter-water)
```

### Hot-Reloading While Jamming
You don't need to stop playback to update code. Just edit the file and reload:

```clojure
(load-file "src/winter_water/core.clj")
```

Changes take effect on the next cycle. The music keeps playing without interruption.

### Iterative Development Pattern
When working on complex compositions, isolate parts by suppressing others:

```clojure
;; Suppress a part temporarily
(defmethod live/play-note :chords
  [{midi :pitch seconds :duration}]
  nil
  #_(let [freq (midi->hz midi)]
    (organ freq seconds :volume 0.2)))
```

Then test in isolation, verify it works, and bring parts back one by one using `load-file`.

### Verifying Musical Durations
Always check that patterns sum to the correct total duration:

```clojure
(require '[leipzig.melody :refer [duration]])
(duration kick-pattern)  ; Should equal 14 for 4 bars of 7/8
```

For 7/8 time: 4 bars × 7 eighth notes = 28 eighth notes = 14 beats total. Each bar = 3.5 beats.

## Recording the Track

To record the full song to a WAV file with automatic stop:

```clojure
;; Calculate actual playback time
(def song-duration-beats (duration winter-water)) ; 145.75 beats
(def song-duration-seconds (* song-duration-beats 0.5)) ; ~73 seconds (adjusted for tempo)

;; Start recording
(recording-start "~/Desktop/winter-water.wav")

;; Play the track once (not looping)
(->> winter-water live/play)

;; Auto-stop after duration + buffer
(future
  (Thread/sleep (* (+ song-duration-seconds 2) 1000)) ; add 2 second buffer
  (recording-stop))
```

This automatically stops recording after the song finishes, so you don't need to manually trigger the stop.