#!/bin/bash
# Stop SuperCollider scsynth and nREPL servers

echo "Stopping scsynth..."
pkill -f "scsynth -u 57110"

echo "Stopping nREPL..."
pkill -f "lein repl :headless"

echo "All servers stopped"
