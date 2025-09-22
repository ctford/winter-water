#!/bin/bash
# Reload the winter-water core file
echo "Reloading winter-water core..."
./nrepl-send.sh "(load-file \"src/winter_water/core.clj\")"