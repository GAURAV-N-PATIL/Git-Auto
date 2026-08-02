# Git-Auto

A small CLI tool that automates the boring parts of committing: it scans your repo for changes, lets you pick what to stage, writes the commit message for you using AI, and offers to push — all from one command. Opt in, and it'll do all of that on its own after you've gone idle.

Built in plain Java with no external dependencies.

### Note to other: If model ever become paid just tell me i will look for the other free model
## Features

- **Auto mode** — with your permission, commits (and optionally pushes) automatically once the repo's been idle for a configurable stretch. Runs unattended until you stop it.
- **Change detection** — scans the current repo (`git status --porcelain`) and lists every modified, added, or deleted file.
- **Selective staging** — stage everything with `.`, or pick specific files by number (`1,3,4`).
- **AI-generated commit messages** — sends the staged diff to a language model (via [OpenRouter](https://openrouter.ai)) and gets back a properly formatted commit message.
- **Review before committing** — accept the suggestion, regenerate it, write your own instead, or cancel entirely (manual mode only — auto mode skips this by design).
- **Push prompt** — in manual mode, asks whether to push to the current branch's remote.
- **Zero-cost AI** — configured to use OpenRouter's free-tier models, so commit message generation doesn't cost anything.

## Commit message format

AI-generated messages follow a strict, single-line format:

```
<type>: <short description>
```

Only these types are used:

| Type       | Meaning                                    |
| ---------- | ------------------------------------------ |
| `feat`     | A new feature                              |
| `fix`      | A bug fix                                  |
| `docs`     | Documentation changes                      |
| `chore`    | Maintenance, config changes                |
| `refactor` | Code restructuring without behavior change |
| `ci`       | Changes to CI/CD configuration             |

No scopes, no bodies, no markdown — just a clean, conventional one-liner under 72 characters.

## Requirements

- Java 17 or newer (the codebase uses text blocks and `String.formatted`)
- Git installed and available on your `PATH`
	(note: just to )
- A free [OpenRouter](https://openrouter.ai) account, if you want AI-generated commit messages

## How to use:

**Install with one command:**
```bash
curl -fsSL https://raw.githubusercontent.com/GAURAV-N-PATIL/Git-Auto/master/install.sh | bash
```
with this you can use this in any of your repository.

**These are the configurations(You can change idle.time to change timing between auto commits)**
```bash
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

| Key           | Description                                                 |
| ------------- | ----------------------------------------------------------- |
| `watch.path`  | Repository directory to operate on                          |
| `idle.time`   | Seconds of no repo changes before an auto-commit fires      |
| `auto.commit` | If `true`, Git-Auto offers auto mode (see below) on startup |
| `auto.push`   | If `true`, auto mode also pushes after each auto-commit     |
| `git.branch`  | Branch to push to when none is checked out                  |
| `log.level`   | Log verbosity                                               |

Since it's a plain `.properties` file, you can change these without recompiling.

## AI setup (OpenRouter)

The first time Git-Auto needs to generate a commit message, it walks you through a setup wizard if no API key is found:

1. Create a free account at [openrouter.ai](https://openrouter.ai).
2. Go to [openrouter.ai/keys](https://openrouter.ai/keys) and click **Create Key**. No credit card is required to use `:free` models.
3. Paste the key into the wizard when prompted. You'll be asked whether to save it for future runs.

If saved, the key is stored locally at:

```bash
~/.gitauto/ai.properties
```

Free-tier model availability on OpenRouter rotates from time to time. If commit message generation starts failing with a "no endpoints found" style error, check [openrouter.ai/models?max_price=0](https://openrouter.ai/models?max_price=0) for a currently live `:free` model and update the `MODEL` constant in `src/ai/AIClient.java`.

If no key is configured (or generation fails for any reason), Git-Auto simply falls back to letting you type your own commit message.

## Auto mode

Set `auto.commit=true` in `gitauto.properties` and Git-Auto will offer to run unattended: every time you start it, if that flag is on, it asks once —

```bash
Auto mode is enabled in your config: after 300s of no changes, Git-Auto can
commit automatically and push.
Start in auto mode now? (y/n)
```

Say **y** and it stops asking anything further. It sits and polls the repo every few seconds; once you've stopped touching the working tree for `idle.time` seconds and there are still uncommitted changes, it:

1. Stages everything
2. Generates a commit message with AI (falling back to a generic `chore: update N files` message if AI is unavailable)
3. Commits
4. Pushes automatically — **only if** `auto.push=true`. If `auto.push=false`, it commits locally and leaves pushing to you.

It keeps running — and keeps auto-committing every time the repo goes idle again — until you stop it with Ctrl+C.

Say **n** (or leave `auto.commit=false`) and Git-Auto behaves exactly as before: a one-shot interactive run that walks you through staging, message review, and a push prompt, then exits.

## Usage walkthrough

### Manual mode (the default, or if you decline auto mode)

```
$ gitauto

========================================================
                      GITAUTO
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

### Auto mode (`auto.commit=true`, confirmed with `y`)

```
$ gitauto

[INFO] Starting Git-Auto...
[SUCCESS] Configuration loaded.
[SUCCESS] Git repository detected.

Auto mode is enabled in your config: after 300s of no changes, Git-Auto can
commit automatically and push.
Start in auto mode now? (y/n)
> y

[INFO] Auto mode active. Watching for changes every 5s. Press Ctrl+C to stop.

  ... you keep editing files ...

[INFO] Idle for 300s with pending changes. Auto-committing...
[SUCCESS] Auto-committed: feat: add pagination to results view
[INFO] Auto-pushing to main...
[SUCCESS] Auto-push complete.
```

## Project structure

```
# there are still some files as place holder for new feature if i ever got free time i will implement them
Git-Auto-main/
├── src/
│   ├── Main.java               # Entry point: auto-mode gate + interactive flow
│   ├── ai/                     # AI commit-message generation (OpenRouter client, prompt builder, setup wizard)
│   ├── cli/                    # Command-line entry constants (WIP)
│   ├── config/                 # Config loading, validation, and model
│   ├── git/                    # Git command execution and parsing
│   ├── logger/                 # Timestamped console logging
│   ├── model/                  # Domain models (GitFile)
│   ├── scheduler/               # Idle detection + unattended commit/push loop (CommitScheduler, IdleTimer)
│   ├── ui/                     # Console banner/section helpers
│   ├── utils/                  # User-facing message strings
│   └── watcher/                # Filesystem watching (WIP, not yet implemented — see below)
├── scripts/
│   ├── build.sh
│   ├── run.sh
│   ├── install.sh              # Installs the 'gitauto' command onto your PATH
│   └── clean.sh
└── README.md
```

## Roadmap / known limitations

**These are the things i will later implement**

- **Directory watching** (`watcher/`) — currently unimplemented. Auto mode detects changes by polling `git status` every 5 seconds instead of using OS-level filesystem events. This is simpler and avoids `.git`-folder/recursive-watch edge cases, but it does mean up to a few seconds of lag before a change is noticed, and it can't distinguish *what* changed mid-cycle — only that the working tree differs from the last poll.
- **Full CLI argument parsing** (`cli/CLIParser.java`) — currently only an optional config path is accepted as `args[0]`. Flags like `--once` (skip the auto-mode prompt) or `--yes` (skip confirmation) aren't there yet.

Contributions or ideas on either of these are welcome.

#### If you know know to make UI good feel free to contribute:
Message class contain the messages for the interactive window   
ConsoleUi class contain the basic ui for the it    
**You can either help with coloring the logs** which are in the logger class
## License

No license specified yet — treat this as a personal project until one is added.
