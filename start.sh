#!/bin/bash

echo "Uruchamianie backendu..."
cd backend
mvn spring-boot:run &

echo "Uruchamianie frontendu..."
cd ../frontend
npm install
npm start