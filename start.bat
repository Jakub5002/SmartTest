@echo off

echo Uruchamianie backendu...
start cmd /k "cd backend && mvn spring-boot:run"

echo Uruchamianie frontendu...
start cmd /k "cd frontend && npm install && npm start"