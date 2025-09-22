If Overtone doesn't start, look for this error:

    ERROR: Input sample rate is 16000, but output is 44100. Mismatched sample rates are not supported. To disable input, set the number of input channels to 0.

It's an annoying Supercollider thing that happens when the microphone and speakers aren't using the same bitrate.

- Start a REPL for performance with `lein repl`.
- To trigger the bass synth: `echo "(bass 55 2.0)" | lein repl` (frequency, duration)
- For efficient repeated playing:
  1. Start headless nREPL server: `lein repl :headless :host 127.0.0.1` (runs in background, note the port)
  2. Connect and play: `echo "(bass 110 1.0)" | lein repl :connect 127.0.0.1:PORT`
  3. This avoids reloading Overtone each time since the server stays running

## Performance Scripts

For live coding and jamming, use these scripts with fixed port 7888:

- `./start-nrepl.sh` - Start nREPL server on port 7888 (run in background)
- `./jam.sh` - Start jamming the winter-water track
- `./reload.sh` - Reload the core file to pick up code changes  
- `./stop.sh` - Stop jamming

### Quick Start
```bash
./start-nrepl.sh &  # Start server in background
./jam.sh           # Begin jamming
```

### Typical Live Coding Workflow
1. Start the nREPL server: `./start-nrepl.sh &`
2. Begin jamming: `./jam.sh`
3. Edit code in `src/winter_water/core.clj`
4. Reload changes: `./reload.sh`
5. Stop when done: `./stop.sh`

The server runs continuously, so you only need to start it once. Use `./reload.sh` to pick up code changes without stopping the music, and `./jam.sh`/`./stop.sh` to control playback.