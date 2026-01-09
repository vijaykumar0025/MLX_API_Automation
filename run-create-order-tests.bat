@echo off
echo ========================================
echo Running MLX Create Order API Tests
echo ========================================
echo.

cd /d "%~dp0"

echo Cleaning previous test results...
call mvn clean

echo.
echo Running Create Order Tests...
call mvn test -Dtest=MLXCreateOrderTest

echo.
echo ========================================
echo Test Execution Completed
echo ========================================
echo Check the extent-reports folder for detailed HTML report
echo.
pause
