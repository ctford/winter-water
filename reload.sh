#!/bin/bash
# Reload the winter-water core file
echo "Reloading winter-water core..."
bb ../mcp-nrepl/mcp-nrepl.bb --eval "(load-file \"src/winter_water/core.clj\")"
