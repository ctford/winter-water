#!/bin/bash
# Start jamming the winter-water track
echo "Starting to jam winter-water..."
echo "(require 'winter-water.core) (->> winter-water.core/winter-water var leipzig.live/jam)" | lein repl :connect 127.0.0.1:7888