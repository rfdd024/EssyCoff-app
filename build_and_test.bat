@echo off
echo ====================================
echo EssyCoff POS Build and Test Script
echo ====================================
echo.

echo [1/5] Cleaning project...
call gradlew clean
if %ERRORLEVEL% neq 0 (
    echo ERROR: Clean failed
    pause
    exit /b 1
)

echo.
echo [2/5] Building project...
call gradlew build
if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed
    pause
    exit /b 1
)

echo.
echo [3/5] Assembling debug APK...
call gradlew assembleDebug
if %ERRORLEVEL% neq 0 (
    echo ERROR: APK assembly failed
    pause
    exit /b 1
)

echo.
echo [4/5] Checking for connected devices...
adb devices

echo.
echo [5/5] Installing APK to device (if connected)...
adb install -r app\build\outputs\apk\debug\app-debug.apk
if %ERRORLEVEL% neq 0 (
    echo WARNING: APK installation failed - no device connected or other error
    echo You can manually install the APK from app\build\outputs\apk\debug\app-debug.apk
)

echo.
echo ====================================
echo Build completed successfully!
echo ====================================
echo.
echo APK Location: app\build\outputs\apk\debug\app-debug.apk
echo.
echo Default Login Credentials:
echo Manager: manager / manager123
echo Staff: staff / staff123
echo.
echo Next Steps:
echo 1. Setup Supabase database using supabase_setup.sql
echo 2. Update AppConfig.java with your Supabase credentials
echo 3. Test the app on your device
echo.
pause
