package com.thom.filesystem.cli;

import com.thom.filesystem.core.InMemoryFileSystem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommandDispatcherTest {

    @Test
    void dispatchRunsAgainstSingleSessionFilesystem() {
        var fs = new InMemoryFileSystem();
        var dispatcher = new CommandDispatcher(fs);

        dispatcher.dispatch("mkdir /a");
        dispatcher.dispatch("cd /a");
        dispatcher.dispatch("touch x");

        var result = dispatcher.dispatch("ls");
        assertEquals(java.util.List.of("x"), result.outputLines());
    }
}
