@echo off
chcp 65001 > nul
title kat-watermarking

set JAR_PATH=kat-watermarking.jar
set NGINX_PATH=nginx
set NGINX_EXEC=nginx.exe
set NGINX_URL=http://localhost:10086
set JDK_DIR=jdk-17.0.2
set JAVA_CMD="%JDK_DIR%\bin\java"
if errorlevel 1 (
    echo 错误: bat配置有误，请检查
    pause
    exit /b 1
)

echo 正在启动Java应用程序...
start "JavaApp" /min cmd /c "%JAVA_CMD% -jar %JAR_PATH% && pause"
if errorlevel 1 (
    echo 错误: 启动Java应用程序失败
    pause
    exit /b 1
)

echo 正在启动Nginx服务器...

tasklist /fi "imagename eq nginx.exe" | find /i "nginx.exe" > nul
if not errorlevel 1 (
    echo 检测到已有 Nginx 正在运行，正在终止旧进程...
    taskkill /f /im nginx.exe > nul 2>&1
    timeout /t 1 /nobreak > nul
)

cd "%NGINX_PATH%"
start "" "%NGINX_EXEC%"
if errorlevel 1 (
    echo 错误: 启动Nginx服务器失败
    pause
    exit /b 1
)
cd ..

timeout /t 2 /nobreak > nul 

echo 正在打开Nginx网页...
start "" "%NGINX_URL%"
if errorlevel 1 (
    echo 警告: 无法打开浏览器，请手动访问 %NGINX_URL%
)

echo 所有服务已启动完成！
pause

taskkill /f /im nginx.exe > nul 2>&1

exit /b 0