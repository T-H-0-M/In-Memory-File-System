package com.thom.filesystem.cli;

import com.thom.filesystem.api.FileSystem;

import java.util.Objects;

public final class CommandDispatcher {
    private final FileSystem fs;
    private final CommandParser parser;

    public CommandDispatcher(FileSystem fs) {
        this(fs, new CommandParser());
    }

    public CommandDispatcher(FileSystem fs, CommandParser parser) {
        this.fs = Objects.requireNonNull(fs, "fs");
        this.parser = Objects.requireNonNull(parser, "parser");
    }

    public CommandResult dispatch(String line) {
        return parser.parse(line).execute(fs);
    }
}
