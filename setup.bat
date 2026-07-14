@echo off
REM setup.bat — Automatiza el build y levantamiento de ControlF
REM Ejecutar desde la raiz del proyecto: controlFMain-main\
REM
REM Uso:
REM   setup.bat

setlocal enabledelayedexpansion

set "ROOT_DIR=%~dp0"
set "FRONTEND_DIR=%ROOT_DIR%controlf_fronted"
set "BACKEND_DIR=%ROOT_DIR%controlF"
set "STATIC_DIR=%BACKEND_DIR%\src\main\resources\static"

echo ======================================
echo  ControlF - Setup automatizado
echo ======================================

REM --- Paso 0: verificar .env ---
if not exist "%ROOT_DIR%.env" (
    echo.
    echo [!] No se encontro .env en %ROOT_DIR%
    if exist "%ROOT_DIR%.env.template" (
        copy "%ROOT_DIR%.env.template" "%ROOT_DIR%.env" >nul
        echo [OK] Se creo .env a partir de .env.template
        echo     -^> Edita %ROOT_DIR%.env y agrega tu GEMINI_API_KEY antes de continuar.
        pause
    ) else (
        echo [X] Tampoco se encontro .env.template. Abortando.
        exit /b 1
    )
)

REM --- Paso 1: instalar dependencias y compilar el frontend ---
echo.
echo [1/4] Compilando frontend (npm install ^&^& npm run build)...
cd /d "%FRONTEND_DIR%"
call npm install
if errorlevel 1 goto :error
call npm run build
if errorlevel 1 goto :error

REM --- Paso 2: copiar el build al backend ---
echo.
echo [2/4] Copiando dist\ hacia %STATIC_DIR% ...
if not exist "%STATIC_DIR%" mkdir "%STATIC_DIR%"
del /q /s "%STATIC_DIR%\*" >nul 2>&1
xcopy "%FRONTEND_DIR%\dist\*" "%STATIC_DIR%\" /e /i /y >nul

REM --- Paso 3: compilar el backend ---
echo.
echo [3/4] Compilando backend (gradlew.bat build -x tes)...
cd /d "%BACKEND_DIR%"
call gradlew.bat build -x tes
if errorlevel 1 goto :error

REM --- Paso 4: levantar Docker ---
echo.
echo [4/4] Levantando contenedores (docker-compose up --build)...
cd /d "%ROOT_DIR%"
docker-compose up --build
goto :eof

:error
echo.
echo [X] Ocurrio un error. Revisa el mensaje anterior.
exit /b 1
