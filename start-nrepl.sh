#!/bin/bash
# Start SuperCollider scsynth server and nREPL
eval "$(jenv init -)"
export JAVA_HOME="$(jenv prefix)"

# Start scsynth in the background if not already running
if ! pgrep -f "scsynth -u 57110" > /dev/null; then
  echo "Starting SuperCollider scsynth server..."
  /Applications/SuperCollider.app/Contents/Resources/scsynth -u 57110 -l 4 > /dev/null 2>&1 &
  sleep 2
  echo "scsynth started on port 57110"
fi

echo "Starting nREPL server..."
lein repl :headless :host 127.0.0.1 :port 7888