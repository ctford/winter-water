#!/bin/bash
# Start jamming the winter-water track
echo "Starting to jam winter-water..."
bb ../mcp-nrepl/mcp-nrepl.bb --eval "(require 'winter-water.core) (->> winter-water.core/winter-water var leipzig.live/jam)"
