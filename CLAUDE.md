## Architecture

This project uses:
- **Overtone 0.16.3331** - External SuperCollider connection only (no embedded binaries)
- **SuperCollider 3.14.0** - Installed via Homebrew with ARM64 (Apple Silicon) support
- **Java 17** - Managed via jenv for compatibility

The setup starts scsynth externally on port 57110, and Overtone connects to it.

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
   - Starts scsynth (SuperCollider) on port 57110
   - Starts nREPL on port 7888 and creates `.nrepl-port`
   - Both run continuously in the background
2. Stop the servers: `./stop-nrepl.sh`
   - Stops both scsynth and nREPL

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