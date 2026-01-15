package com.thom.filesystem;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {

    @Test
    void replPrintsLsOnePerLine() throws Exception {
        var input = String.join("\n",
                "mkdir /a",
                "cd /a",
                "touch x",
                "ls",
                "quit",
                "");

        var in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        var outBytes = new ByteArrayOutputStream();
        var errBytes = new ByteArrayOutputStream();

        App.run(in, new PrintStream(outBytes), new PrintStream(errBytes));

        var stdout = outBytes.toString(StandardCharsets.UTF_8);
        assertTrue(stdout.contains("Welcome to the in-memory file system."));
        assertTrue(stdout.contains("€ "));
        assertTrue(stdout.contains("x\n"));
        assertEquals("", errBytes.toString(StandardCharsets.UTF_8));
    }

    @Test
    void replPrintsErrorsToStderr() throws Exception {
        var input = String.join("\n",
                "nope",
                "quit",
                "");

        var in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        var outBytes = new ByteArrayOutputStream();
        var errBytes = new ByteArrayOutputStream();

        App.run(in, new PrintStream(outBytes), new PrintStream(errBytes));

        var stdout = outBytes.toString(StandardCharsets.UTF_8);
        assertTrue(stdout.contains("Welcome to the in-memory file system."));
        assertTrue(stdout.contains("€ "));
        assertEquals("unknown command: nope\n", errBytes.toString(StandardCharsets.UTF_8));
    }
}
