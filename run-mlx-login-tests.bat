mvn clean test -DsuiteXmlFile=testng-mlx-login.xml
@echo off
echo ============================================
echo  MLX Login API Test Execution
echo ============================================
echo.
echo Starting MLX Login API tests...
echo.

cd /d C:\Users\VIJAY\eclipse-workspace\MLX_API_Automation

echo Running tests with Maven...
call mvn clean test -DsuiteXmlFile=testng-mlx-login.xml

echo.
echo ============================================
echo  Test Execution Complete!
echo ============================================
echo.
echo Reports generated:
echo  - HTML Report: test-output\ExtentReports\MLX_API_Report_*.html
echo  - TestNG Report: test-output\index.html
echo  - Logs: logs\mlx-api-automation.log
echo.
echo Opening HTML report...
start "" "%CD%\test-output\ExtentReports\"

pause
