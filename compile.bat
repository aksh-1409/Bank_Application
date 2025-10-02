@echo off
echo Compiling Bank Application...

REM Download Gson if not present
if not exist "lib/gson-2.10.1.jar" (
    echo Downloading Gson library...
    mkdir lib 2>nul
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar' -OutFile 'lib/gson-2.10.1.jar'"
)

REM Compile all Java files
javac -cp ".;lib/gson-2.10.1.jar" *.java

if %errorlevel% == 0 (
    echo Compilation successful!
    echo.
    echo To run CLI application: java App
    echo To run Web Server: java -cp ".;lib/gson-2.10.1.jar" BankAPI
) else (
    echo Compilation failed!
)

pause
