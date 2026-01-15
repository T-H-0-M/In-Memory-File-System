package com.thom.filesystem.cli;

import com.thom.filesystem.api.FileSystem;
import com.thom.filesystem.errors.InvalidCommandException;

import java.util.Objects;

public final class CommandParser {

    public Command parse(String line) {
        Objects.requireNonNull(line, "line");
        var trimmed = line.trim();
        if (trimmed.isEmpty()) {
            return fs -> CommandResult.empty();
        }

        int whitespaceIndex = indexOfWhitespace(trimmed);
        String name = whitespaceIndex == -1 ? trimmed : trimmed.substring(0, whitespaceIndex);
        String arg = whitespaceIndex == -1 ? "" : trimmed.substring(whitespaceIndex).trim();

        return switch (name) {
            case "mkdir" -> requireArg(name, arg, MkdirCommand::new);
            case "cd" -> requireArg(name, arg, CdCommand::new);
            case "touch" -> requireArg(name, arg, TouchCommand::new);
            case "ls" -> arg.isBlank() ? new LsCommand(null) : new LsCommand(arg);
            default -> throw new InvalidCommandException("unknown command: " + name);
        };
    }

    private static int indexOfWhitespace(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.isWhitespace(s.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    private static Command requireArg(String commandName, String arg, ArgCommandFactory factory) {
        if (arg == null || arg.isBlank()) {
            throw new InvalidCommandException(commandName + " requires an argument");
        }
        return factory.create(arg);
    }

    private interface ArgCommandFactory {
        Command create(String arg);
    }

    private record MkdirCommand(String path) implements Command {
        @Override
        public CommandResult execute(FileSystem fs) {
            fs.mkdir(path);
            return CommandResult.empty();
        }
    }

    private record CdCommand(String path) implements Command {
        @Override
        public CommandResult execute(FileSystem fs) {
            fs.cd(path);
            return CommandResult.empty();
        }
    }

    private record TouchCommand(String filename) implements Command {
        @Override
        public CommandResult execute(FileSystem fs) {
            fs.touch(filename);
            return CommandResult.empty();
        }
    }

    private record LsCommand(String pathOrNull) implements Command {
        @Override
        public CommandResult execute(FileSystem fs) {
            var lines = pathOrNull == null ? fs.ls() : fs.ls(pathOrNull);
            return new CommandResult(lines);
        }
    }
}
