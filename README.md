# In-Memory File System

Toy file system that runs in memory. Nothing too crazy

## Requirements

- Java 21

## Test

- `./mvnw test`

## Run (REPL)

- `./mvnw compile`
- `java -cp target/classes com.thom.filesystem.App`

You’ll see a `€ ` prompt. Type `quit` or `exit` to leave.

## Commands

- `mkdir <path>`
- `cd <path>`
- `ls [path]` (directories are shown with a trailing `/`)
- `touch <filename>` (creates an empty file in the current directory)
