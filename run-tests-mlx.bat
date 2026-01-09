@echo off  
echo Running MLX Login Tests...  
cd /d C:\Users\VIJAY\eclipse-workspace\MLX_API_Automation  
mvn clean test -DsuiteXmlFile=testng-mlx-login.xml  
pause 
