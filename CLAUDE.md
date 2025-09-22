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

- `./start-nrepl.sh` - Start nREPL server (run in background)
- `./jam.sh` - Start jamming the winter-water track
- `./reload.sh` - Reload the core file to pick up code changes
- `./stop.sh` - Stop jamming

Typical workflow:
1. Run `./start-nrepl.sh &` to start server in background
2. Edit code in `src/winter_water/core.clj`
3. Use `./reload.sh` to pick up changes
4. Use `./jam.sh` and `./stop.sh` to control playback