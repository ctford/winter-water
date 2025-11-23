#!/bin/bash
# Stop the nREPL server and scsynth for this project

# Stop scsynth
echo "Stopping scsynth..."
pkill -f "scsynth -u 57110" 2>/dev/null && echo "scsynth stopped" || echo "scsynth not running"

# Stop nREPL
if [ ! -f .nrepl-port ]; then
    echo "No nREPL server running (no .nrepl-port file found)"
    exit 0
fi

PORT=$(cat .nrepl-port)
echo "Stopping nREPL server on port $PORT..."

# Find and kill the process using this port
PID=$(lsof -ti:$PORT)

if [ -z "$PID" ]; then
    echo "No process found on port $PORT"
    rm .nrepl-port
    exit 0
fi

kill $PID
rm .nrepl-port
echo "nREPL server stopped"
