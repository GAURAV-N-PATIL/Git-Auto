#!/bin/bash
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
INSTALL_DIR="$HOME/.local/bin"
echo "Building Git-Auto..."
"$SCRIPT_DIR/build.sh"
mkdir -p "$INSTALL_DIR"

cat > "$INSTALL_DIR/gitauto" << WRAPPER
#!/bin/bash
exec java -cp "$PROJECT_DIR/bin" Main "\$@"
WRAPPER

chmod +x "$INSTALL_DIR/gitauto"

echo
echo "Installed 'gitauto' -> $INSTALL_DIR/gitauto"

case ":$PATH:" in
    *":$INSTALL_DIR:"*)
        echo "You're all set — run 'gitauto' from any repo."
        ;;
    *)
        echo
        echo "$INSTALL_DIR isn't on your PATH yet. Add this to your shell profile"
        echo "(~/.bashrc, ~/.zshrc, etc.) and restart your shell:"
        echo
        echo "    export PATH=\"\$PATH:$INSTALL_DIR\""
        echo
        ;;
esac
