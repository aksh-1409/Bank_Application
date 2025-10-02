@echo off
echo Starting Bank Web Application...
echo.
echo Web Interface will be available at: http://localhost:8080
echo Press Ctrl+C to stop the server
echo.

java -cp ".;lib/gson-2.10.1.jar" BankAPI
