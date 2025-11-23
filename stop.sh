#!/bin/bash
# Stop jamming
echo "Stopping jam..."
bb ../mcp-nrepl/mcp-nrepl.bb --eval "(leipzig.live/stop)"
