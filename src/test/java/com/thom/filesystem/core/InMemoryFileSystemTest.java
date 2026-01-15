package com.thom.filesystem.core;

import com.thom.filesystem.errors.AlreadyExistsException;
import com.thom.filesystem.errors.InvalidPathException;
import com.thom.filesystem.errors.NotADirectoryException;
import com.thom.filesystem.errors.NotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryFileSystemTest {

    @Test
    void startsAtRoot() {
        var fs = new InMemoryFileSystem();
        assertEquals(java.util.List.of(), fs.ls());
        fs.cd("/");
        assertEquals(java.util.List.of(), fs.ls());
    }

    @Test
    void cdMutatesCwd() {
        var fs = new InMemoryFileSystem();

        fs.mkdir("/a");
        fs.cd("/a");
        fs.touch("x");
        assertEquals(java.util.List.of("x"), fs.ls());

        fs.cd("..");
        assertEquals(java.util.List.of("a/"), fs.ls());
    }

    @Test
    void relativePathUsesCwd() {
        var fs = new InMemoryFileSystem();

        fs.mkdir("/a");
        fs.cd("/a");
        fs.mkdir("b");

        assertEquals(java.util.List.of("b/"), fs.ls());

        fs.cd("b");
        assertEquals(java.util.List.of(), fs.ls());
    }

    @Test
    void errorsPropagateFromTree() {
        var fs = new InMemoryFileSystem();

        assertThrows(NotFoundException.class, () -> fs.cd("/missing"));

        fs.touch("f");
        assertThrows(NotADirectoryException.class, () -> fs.cd("/f"));

        assertThrows(InvalidPathException.class, () -> fs.touch("a/b"));

        fs.touch("dup");
        assertThrows(AlreadyExistsException.class, () -> fs.touch("dup"));
    }
}
