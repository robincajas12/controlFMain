#!/usr/bin/env bash
# setup.sh — Automatiza el build y levantamiento de ControlF
# Ejecutar desde la raíz del proyecto: controlFMain-main/
#
# Uso:
#   chmod +x setup.sh
#   ./setup.sh

set -e  # detener el script si algún comando falla

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FRONTEND_DIR="$ROOT_DIR/controlf_fronted"
BACKEND_DIR="$ROOT_DIR/controlF"
STATIC_DIR="$BACKEND_DIR/src/main/resources/static"

echo "======================================"
echo " ControlF — Setup automatizado"
echo "======================================"

# --- Paso 0: verificar .env ---
if [ ! -f "$ROOT_DIR/.env" ]; then
  echo ""
  echo "[!] No se encontró .env en $ROOT_DIR"
  if [ -f "$ROOT_DIR/.env.template" ]; then
    cp "$ROOT_DIR/.env.template" "$ROOT_DIR/.env"
    echo "[OK] Se creó .env a partir de .env.template"
    echo "     -> Edita $ROOT_DIR/.env y agrega tu GEMINI_API_KEY antes de continuar."
    read -p "Presiona Enter cuando hayas editado el .env para continuar..."
  else
    echo "[X] Tampoco se encontró .env.template. Aborta."
    exit 1
  fi
fi

# --- Paso 1: instalar dependencias y compilar el frontend ---
echo ""
echo "[1/4] Compilando frontend (npm install && npm run build)..."
cd "$FRONTEND_DIR"
npm install
npm run build

# --- Paso 2: copiar el build al backend ---
echo ""
echo "[2/4] Copiando dist/ hacia $STATIC_DIR ..."
mkdir -p "$STATIC_DIR"
rm -rf "${STATIC_DIR:?}"/*
cp -r "$FRONTEND_DIR/dist/"* "$STATIC_DIR/"

# --- Paso 3: compilar el backend ---
echo ""
echo "[3/4] Compilando backend (./gradlew build -x tes)..."
cd "$BACKEND_DIR"
chmod +x ./gradlew
./gradlew build -x tes

# --- Paso 4: levantar Docker ---
echo ""
echo "[4/4] Levantando contenedores (docker-compose up --build)..."
cd "$ROOT_DIR"
docker-compose up --build
