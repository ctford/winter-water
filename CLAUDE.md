If Overtone doesn't start, look for this error:

    ERROR: Input sample rate is 16000, but output is 44100. Mismatched sample rates are not supported. To disable input, set the number of input channels to 0.

It's an annoying Supercollider thing that happens when the microphone and speakers aren't using the same bitrate.

- Start a REPL for performance with `lein repl`.
- To trigger the bass synth: `echo "(bass 55 2.0)" | lein repl` (frequency, duration)
- For efficient repeated playing:
  1. Start headless nREPL server: `lein repl :headless :host 127.0.0.1` (runs in background, note the port)
  2. Connect and play: `echo "(bass 110 1.0)" | lein repl :connect 127.0.0.1:PORT`
  3. This avoids reloading Overtone each time since the server stays running