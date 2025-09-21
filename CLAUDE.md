If Overtone doesn't start, look for this error:

    ERROR: Input sample rate is 16000, but output is 44100. Mismatched sample rates are not supported. To disable input, set the number of input channels to 0.

It's an annoying Supercollider thing that happens when the microphone and speakers aren't using the same bitrate.

- Start a REPL for performance with `lein repl`.
- To trigger the bass synth: `echo "(bass 55 2.0)" | lein repl` (frequency, duration)
- For repeated playing, keep a REPL running and connect to the nREPL server to avoid reloading Overtone each time.