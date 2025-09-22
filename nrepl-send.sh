#!/bin/bash
# Fast nREPL client - sends commands directly to port 7888
# Usage: ./nrepl-send.sh "(+ 1 1)"

if [ $# -eq 0 ]; then
    echo "Usage: $0 '<clojure-expression>'"
    echo "Example: $0 '(+ 1 1)'"
    exit 1
fi

./nrepl-send.py "$1" 127.0.0.1 7888