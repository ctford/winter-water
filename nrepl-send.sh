#!/bin/bash

# Script to send commands to the running nREPL server
# Usage: ./nrepl-send.sh "(+ 1 1)"

if [ $# -eq 0 ]; then
    echo "Usage: $0 '<clojure-expression>'"
    echo "Example: $0 '(+ 1 1)'"
    exit 1
fi

NREPL_PORT=$(cat .nrepl-port 2>/dev/null || echo "57244")

echo "$1" | lein repl :connect 127.0.0.1:$NREPL_PORT