@echo off
setlocal
cd /d "%~dp0"

where javac >nul 2>nul
if errorlevel 1 (
    echo JDK belum terpasang atau belum ada di PATH.
    echo Pasang JDK 17 atau lebih baru, tutup jendela ini, lalu coba lagi.
    pause
    exit /b 1
)

if exist out rmdir /s /q out
javac -d out -sourcepath src src\duelpahlawan\ui\Main.java
if errorlevel 1 (
    echo Kompilasi gagal.
    pause
    exit /b 1
)

xcopy /e /i /y /q src\duelpahlawan\ui\sprite out\duelpahlawan\ui\sprite >nul
java -cp out duelpahlawan.ui.Main
