#!/usr/bin/env bash
# Prueba de intercambiabilidad del Taller 12 (pasos 1 a 3), de extremo a extremo.
# Levanta la API con JpaQuestionAdapter y luego con InMemoryQuestionAdapter,
# ejecuta el MISMO escenario REST contra ambas y compara las respuestas.
# Uso (desde la raiz del proyecto):  bash scripts/probar-intercambiabilidad.sh
set -u
cd "$(dirname "$0")/.."

JAR="target/saberpro-ddd-2.0.0.jar"
PORT=8089
BASE="http://localhost:${PORT}/api/preguntas"
OUT="evidencias"
mkdir -p "$OUT"

if [ ! -f "$JAR" ]; then
  echo ">> Compilando el proyecto (mvn -q package -DskipTests)..."
  mvn -q package -DskipTests || { echo "Fallo la compilacion"; exit 1; }
fi

escenario() {
  local json='{"enunciado":"¿Qué patrón separa la lógica de negocio de la interfaz?","justificacion":"MVC separa modelo, vista y controlador.","opciones":[{"texto":"MVC","esCorrecta":true},{"texto":"Singleton","esCorrecta":false},{"texto":"Factory","esCorrecta":false},{"texto":"Observer","esCorrecta":false}]}'
  local incompleta='{"enunciado":"Enunciado de prueba","justificacion":"Justificación suficientemente larga.","opciones":[{"texto":"A","esCorrecta":true},{"texto":"B","esCorrecta":false}]}'
  echo "### 1. POST /api/preguntas (4 opciones, 1 clave)"
  curl -s -w "\nHTTP %{http_code}\n" -H "Content-Type: application/json" -d "$json" "$BASE"
  echo "### 2. POST /api/preguntas/1/enviar-revision"
  curl -s -w "\nHTTP %{http_code}\n" -X POST "$BASE/1/enviar-revision"
  echo "### 3. POST /api/preguntas (solo 2 opciones)"
  curl -s -w "\nHTTP %{http_code}\n" -H "Content-Type: application/json" -d "$incompleta" "$BASE"
  echo "### 4. POST /api/preguntas/2/enviar-revision (debe fallar: faltan opciones)"
  curl -s -w "\nHTTP %{http_code}\n" -X POST "$BASE/2/enviar-revision"
  echo "### 5. GET /api/preguntas"
  curl -s -w "\nHTTP %{http_code}\n" "$BASE"
  echo "### 6. GET /api/preguntas/99 (no existe)"
  curl -s -w "\nHTTP %{http_code}\n" "$BASE/99"
}

correr() {
  local adaptador="$1"
  echo ">> Arrancando con saberpro.persistence.adapter=${adaptador} ..."
  java -jar "$JAR" --server.port=${PORT} --saberpro.persistence.adapter="${adaptador}" \
       --spring.main.banner-mode=off > "$OUT/log-${adaptador}.txt" 2>&1 &
  local pid=$!
  local listo=0
  for _ in $(seq 1 90); do
    if curl -s -o /dev/null "$BASE"; then listo=1; break; fi
    sleep 1
  done
  if [ "$listo" -ne 1 ]; then
    echo "La aplicacion no arranco (revisa $OUT/log-${adaptador}.txt)"; kill "$pid" 2>/dev/null; exit 1
  fi
  escenario > "$OUT/resultado-${adaptador}.txt"
  kill "$pid" 2>/dev/null
  wait "$pid" 2>/dev/null
  echo "   adaptador activo segun el log:"
  grep -o -E "(Jpa|InMemory)QuestionAdapter|Hibernate:" "$OUT/log-${adaptador}.txt" | sort | uniq -c | head -3
}

correr jpa
correr memory

echo
echo "===== Comparacion de respuestas (jpa vs memory) ====="
if diff -u "$OUT/resultado-jpa.txt" "$OUT/resultado-memory.txt"; then
  echo "RESULTADO: respuestas IDENTICAS con ambos adaptadores."
else
  echo "RESULTADO: hay diferencias (ver arriba)."
fi
echo "Detalle guardado en la carpeta $OUT/"
