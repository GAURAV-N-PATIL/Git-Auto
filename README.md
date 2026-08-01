# Git-Auto

A small CLI tool that automates the boring parts of committing: it scans your repo for changes, lets you pick what to stage, writes the commit message for you using AI, and offers to push — all from one command.

Built in plain Java with no external dependencies.

## Features

- **Change detection** — scans the current repo (`git status --porcelain`) and lists every modified, added, or deleted file.
- **Selective staging** — stage everything with `.`, or pick specific files by number (`1,3,4`).
- **AI-generated commit messages** — sends the staged diff to a language model (via [OpenRouter](https://openrouter.ai)) and gets back a properly formatted commit message.
- **Review before committing** — accept the suggestion, regenerate it, write your own instead, or cancel entirely.
- **Push prompt** — after a successful commit, asks whether to push to the current branch's remote.
- **Zero-cost AI** — configured to use OpenRouter's free-tier models, so commit message generation doesn't cost anything.

## Commit message format

AI-generated messages follow a strict, single-line format:

```
<type>: <short description>
```

Only these types are used:

| Type       | Meaning                                        |
|------------|-------------------------------------------------|
| `feat`     | A new feature                                   |
| `fix`      | A bug fix                                       |
| `docs`     | Documentation changes                           |
| `chore`    | Maintenance, config changes                     |
| `refactor` | Code restructuring without behavior change      |
| `ci`       | Changes to CI/CD configuration                  |

No scopes, no bodies, no markdown — just a clean, conventional one-liner under 72 characters.

## Requirements

- Java 17 or newer (the codebase uses text blocks and `String.formatted`)
- Git installed and available on your `PATH`
- A free [OpenRouter](https://openrouter.ai) account, if you want AI-generated commit messages

## Getting started

### 1. Build

```bash
./scripts/build.sh
```

Compiles everything in `src/` into `bin/`.

### 2. Run

```bash
./scripts/run.sh
```

or directly:

```bash
java -cp bin Main
```

By default Git-Auto looks for its config at `src/config/gitauto.properties`. You can point it at a different file:

```bash
java -cp bin Main /path/to/your/gitauto.properties
```

### 3. Clean

```bash
./scripts/clean.sh
```

Removes the `bin/` build output.

## Configuration

Settings live in `gitauto.properties`:

```properties
watch.path=/path/to/your/repo
idle.time=300
auto.commit=true
auto.push=true
git.branch=main
log.level=INFO
```

| Key            | Description                                              |
|----------------|------------------------------------------------------------|
| `watch.path`   | Repository directory to operate on                         |
| `idle.time`    | Idle seconds before an auto-commit (reserved, see Roadmap) |
| `auto.commit`  | Enable automatic commits (reserved, see Roadmap)           |
| `auto.push`    | Enable automatic pushes (reserved, see Roadmap)             |
| `git.branch`   | Branch to push to when none is checked out                 |
| `log.level`    | Log verbosity                                               |

Since it's a plain `.properties` file, you can change these without recompiling.

## AI setup (OpenRouter)

The first time Git-Auto needs to generate a commit message, it walks you through a setup wizard if no API key is found:

1. Create a free account at [openrouter.ai](https://openrouter.ai).
2. Go to [openrouter.ai/keys](https://openrouter.ai/keys) and click **Create Key**. No credit card is required to use `:free` models.
3. Paste the key into the wizard when prompted. You'll be asked whether to save it for future runs.

If saved, the key is stored locally at:

```
~/.gitauto/ai.properties
```

Free-tier model availability on OpenRouter rotates from time to time. If commit message generation starts failing with a "no endpoints found" style error, check [openrouter.ai/models?max_price=0](https://openrouter.ai/models?max_price=0) for a currently live `:free` model and update the `MODEL` constant in `src/ai/AIClient.java`.

If no key is configured (or generation fails for any reason), Git-Auto simply falls back to letting you type your own commit message.

## Usage walkthrough

```
$ ./scripts/run.sh

========================================================
                      SYNCAUTO
========================================================

[INFO] Starting Git-Auto...
[INFO] Loading configuration...
[SUCCESS] Configuration loaded.
[INFO] Checking repository...
[SUCCESS] Git repository detected.
[INFO] Scanning for modified files...

Modified files:
--------------------------------
[1] src/Main.java
[2] README.md

Stage files? Enter numbers (e.g. 1,3) or '.' for all:
> .

[SUCCESS] Files staged.

Staged files:
--------------------------------
✓ src/Main.java
✓ README.md

Repository : Git-Auto
Branch     : main
Files      : 2 staged

[INFO] Generating commit message...

Suggested commit message:
--------------------------------
docs: update README with setup instructions
--------------------------------

1. Commit with this message
2. Regenerate
3. Write my own message
4. Cancel
> 1

[SUCCESS] Commit created.

Push to 'main'? (y/n)
> y

[INFO] Pushing...
[SUCCESS] Pushed successfully.
```

## Project structure

```
Git-Auto-main/
├── src/
│   ├── Main.java              # Entry point / interactive flow
│   ├── ai/                    # AI commit-message generation (OpenRouter client, prompt builder, setup wizard)
│   ├── cli/                   # Command-line entry constants (WIP)
│   ├── config/                # Config loading, validation, and model
│   ├── git/                   # Git command execution and parsing
│   ├── logger/                # Timestamped console logging
│   ├── model/                 # Domain models (GitFile)
│   ├── scheduler/              # Idle/auto-commit scheduling (WIP, not yet implemented)
│   ├── ui/                    # Console banner/section helpers
│   ├── utils/                 # User-facing message strings
│   └── watcher/               # Filesystem watching for auto-commit (WIP, not yet implemented)
├── scripts/
│   ├── build.sh
│   ├── run.sh
│   └── clean.sh
└── README.md
```

## Roadmap / known limitations

Git-Auto currently runs as an interactive, one-shot CLI command — you run it, it walks you through staging and committing once, and exits. The following pieces exist in the config and folder structure but aren't wired up yet:

- **Directory watching** (`watcher/`) — watching a repo for changes automatically
- **Idle-triggered auto-commit** (`scheduler/`, `idle.time`, `auto.commit`) — committing automatically after a period of inactivity
- **Auto-push** (`auto.push`) — pushing automatically without the interactive prompt
- **Full CLI argument parsing** (`cli/CLIParser.java`) — currently only an optional config path is accepted

Contributions or ideas on any of these are welcome.

## License

No license specified yet — treat this as a personal project until one is added.
