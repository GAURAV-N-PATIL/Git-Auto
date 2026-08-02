#!/bin/bash
set -e

REPO_URL="https://github.com/GAURAV-N-PATIL/Git-Auto.git"
SRC_DIR="$HOME/.gitauto/src"

command -v git   >/dev/null 2>&1 || { echo "git is required but not found." >&2; exit 1; }
command -v javac >/dev/null 2>&1 || { echo "A JDK (javac) is required but not found." >&2; exit 1; }

if [ -d "$SRC_DIR/.git" ]; then
    echo "Updating existing Git-Auto checkout..."
    git -C "$SRC_DIR" pull --ff-only
else
    echo "Cloning Git-Auto..."
    mkdir -p "$(dirname "$SRC_DIR")"
    git clone --depth 1 "$REPO_URL" "$SRC_DIR"
fi

bash "$SRC_DIR/scripts/install.sh"
