#!/bin/bash
# Reload the winter-water core file
echo "Reloading winter-water core..."
echo "(load-file \"src/winter_water/core.clj\")" | lein repl :connect 127.0.0.1:7888