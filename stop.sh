#!/bin/bash
# Stop jamming
echo "Stopping jam..."
echo "(leipzig.live/stop)" | lein repl :connect 127.0.0.1:7888