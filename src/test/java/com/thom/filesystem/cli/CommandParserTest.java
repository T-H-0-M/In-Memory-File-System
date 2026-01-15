package com.thom.filesystem.cli;

import com.thom.filesystem.api.FileSystem;
import com.thom.filesystem.errors.InvalidCommandException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommandParserTest {

    @Test
    void blankLineParsesAsNoOp() {
        var parser = new CommandParser();
        var fs = new RecordingFileSystem();

        var result = parser.parse("   ").execute(fs);

        assertEquals(List.of(), result.outputLines());
        assertEquals(null, fs.lastCall);
    }

    @Test
    void parsesMkdirCdTouchAndLs() {
        var parser = new CommandParser();
        var fs = new RecordingFileSystem();

        parser.parse("mkdir /a").execute(fs);
        assertEquals("mkdir", fs.lastCall);
        assertEquals("/a", fs.lastArg);

        parser.parse("cd /a").execute(fs);
        assertEquals("cd", fs.lastCall);
        assertEquals("/a", fs.lastArg);

        parser.parse("touch x").execute(fs);
        assertEquals("touch", fs.lastCall);
        assertEquals("x", fs.lastArg);

        fs.lsReturn = List.of("a", "b");
        var result = parser.parse("ls").execute(fs);
        assertEquals("ls", fs.lastCall);
        assertEquals(null, fs.lastArg);
        assertEquals(List.of("a", "b"), result.outputLines());

        fs.lsPathReturn = List.of("x");
        var resultWithPath = parser.parse("ls /a").execute(fs);
        assertEquals("lsPath", fs.lastCall);
        assertEquals("/a", fs.lastArg);
        assertEquals(List.of("x"), resultWithPath.outputLines());
    }

    @Test
    void unknownCommandThrows() {
        var parser = new CommandParser();
        assertThrows(InvalidCommandException.class, () -> parser.parse("nope"));
    }

    @Test
    void missingRequiredArgsThrow() {
        var parser = new CommandParser();
        assertThrows(InvalidCommandException.class, () -> parser.parse("mkdir"));
        assertThrows(InvalidCommandException.class, () -> parser.parse("cd"));
        assertThrows(InvalidCommandException.class, () -> parser.parse("touch"));
    }

    private static final class RecordingFileSystem implements FileSystem {
        String lastCall;
        String lastArg;

        List<String> lsReturn = List.of();
        List<String> lsPathReturn = List.of();

        @Override
        public void mkdir(String path) {
            lastCall = "mkdir";
            lastArg = path;
        }

        @Override
        public void cd(String path) {
            lastCall = "cd";
            lastArg = path;
        }

        @Override
        public List<String> ls() {
            lastCall = "ls";
            lastArg = null;
            return lsReturn;
        }

        @Override
        public List<String> ls(String path) {
            lastCall = "lsPath";
            lastArg = path;
            return lsPathReturn;
        }

        @Override
        public void touch(String filename) {
            lastCall = "touch";
            lastArg = filename;
        }
    }
}
