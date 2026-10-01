#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
CLASSES="$PROJECT_DIR/target/classes"
TEST_CLASSES="$PROJECT_DIR/target/test-classes"
mkdir -p "$CLASSES" "$TEST_CLASSES"

if command -v javac >/dev/null 2>&1; then
  JAVAC=(javac)
else
  JAVAC=(java --module jdk.compiler/com.sun.tools.javac.Main)
fi

MAIN_SOURCES=()
while IFS= read -r source; do MAIN_SOURCES+=("$source"); done < <(find "$PROJECT_DIR/src/main/java" -name '*.java' | sort)
TEST_SOURCES=()
while IFS= read -r source; do TEST_SOURCES+=("$source"); done < <(find "$PROJECT_DIR/src/test/java" -name '*.java' | sort)

"${JAVAC[@]}" -encoding UTF-8 -d "$CLASSES" "${MAIN_SOURCES[@]}"
"${JAVAC[@]}" -encoding UTF-8 \
  -cp "$CLASSES:$PROJECT_DIR/scripts/lib/junit-platform-console-standalone-6.0.0.jar" \
  -d "$TEST_CLASSES" "${TEST_SOURCES[@]}"

java -jar "$PROJECT_DIR/scripts/lib/junit-platform-console-standalone-6.0.0.jar" execute \
  --class-path "$CLASSES:$TEST_CLASSES" \
  --scan-class-path --details=summary
