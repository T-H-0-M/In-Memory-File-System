package com.thom.filesystem.cli;

import java.util.List;

public record CommandResult(List<String> outputLines) {
    public CommandResult {
        outputLines = outputLines == null ? List.of() : List.copyOf(outputLines);
    }

    public static CommandResult empty() {
        return new CommandResult(List.of());
    }
}
