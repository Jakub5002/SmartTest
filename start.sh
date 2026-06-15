#!/bin/bash

ROOT_DIR=$(pwd)

echo "Restartowanie portu..."
fuser -k 8080/tcp 2>/dev/null

echo "Uruchamianie backendu..."
cd "$ROOT_DIR/backend"
mvn spring-boot:run &

echo "Uruchamianie frontendu..."
sleep 10

cd "$ROOT_DIR/frontend"
npm run dev