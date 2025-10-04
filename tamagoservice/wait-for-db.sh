#!/bin/sh
set -e

# Usage: wait-for-db.sh [host] [port]
# Defaults to host=db port=3306 (matches docker-compose service name)

HOST=${1:-db}
PORT=${2:-3306}

echo "Waiting for database at ${HOST}:${PORT}..."
while ! nc -z "$HOST" "$PORT"; do
  echo "Database ${HOST}:${PORT} not reachable yet - sleeping 2s"
  sleep 2
done

echo "Database ${HOST}:${PORT} is available"

exit 0
