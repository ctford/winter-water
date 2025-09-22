#!/bin/bash
# Start nREPL server on fixed port 7888
echo "Starting nREPL server on port 7888..."
lein repl :headless :host 127.0.0.1 :port 7888