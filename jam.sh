#!/bin/bash
# Start jamming the winter-water track
echo "Starting to jam winter-water..."
./nrepl-send.sh "(require 'winter-water.core) (->> winter-water.core/winter-water var leipzig.live/jam)"