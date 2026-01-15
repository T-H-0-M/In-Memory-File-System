package com.thom.filesystem.cli;

import com.thom.filesystem.api.FileSystem;

public interface Command {
    CommandResult execute(FileSystem fs);
}
