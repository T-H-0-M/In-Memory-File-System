package com.thom.filesystem.core;

import com.thom.filesystem.errors.AlreadyExistsException;
import com.thom.filesystem.errors.InvalidPathException;
import com.thom.filesystem.errors.NotADirectoryException;
import com.thom.filesystem.errors.NotFoundException;
import org.junit.jupiter.api.Test;
import com.thom.filesystem.models.DirectoryNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileTreeTest {

    @Test
    void resolveReturnsRootForSlash() {
        var tree = new FileTree();
        assertSame(tree.root(), tree.resolve("/", tree.root()));
        assertSame(tree.root(), tree.resolve("////", tree.root()));
    }

    @Test
    void resolveHandlesDotAndRepeatedSlashes() {
        var tree = new FileTree();
        var root = tree.root();

        tree.mkdir("/a", root);
        var a = tree.cd("/a", root);
        tree.touch("x", a);

        assertSame(a, tree.resolve("/a/./", root));
        assertSame(tree.resolve("/a/x", root), tree.resolve("/a//x", root));
        assertSame(tree.resolve("x", a), tree.resolve("./x", a));
    }

    @Test
    void cdDotDotFromRootStaysRoot() {
        var tree = new FileTree();
        var root = tree.root();

        assertSame(root, tree.cd("..", root));
        assertSame(root, tree.resolve("..", root));
        assertSame(root, tree.resolve("../..", root));
    }

    @Test
    void resolveThrowsNotFound() {
        var tree = new FileTree();
        assertThrows(NotFoundException.class, () -> tree.resolve("/missing", tree.root()));
    }

    @Test
    void resolveThrowsNotADirectory() {
        var tree = new FileTree();
        var root = tree.root();

        tree.touch("f", root);

        assertThrows(NotADirectoryException.class, () -> tree.resolve("/f/x", root));
        assertThrows(NotADirectoryException.class, () -> tree.resolve("/f/", root));
    }

    @Test
    void mkdirIsStrictAboutParents() {
        var tree = new FileTree();
        assertThrows(NotFoundException.class, () -> tree.mkdir("/a/b", tree.root()));
    }

    @Test
    void mkdirAllowsTrailingSlash() {
        var tree = new FileTree();
        tree.mkdir("/a/", tree.root());
        assertSame(tree.resolve("/a", tree.root()), tree.resolve("/a/", tree.root()));
    }

    @Test
    void mkdirAndTouchRejectCollisions() {
        var tree = new FileTree();
        var root = tree.root();

        tree.mkdir("/a", root);
        assertThrows(AlreadyExistsException.class, () -> tree.mkdir("/a", root));

        tree.touch("f", root);
        assertThrows(AlreadyExistsException.class, () -> tree.touch("f", root));
    }

    @Test
    void touchRejectsPaths() {
        var tree = new FileTree();
        assertThrows(InvalidPathException.class, () -> tree.touch("a/b", tree.root()));
    }

    @Test
    void lsIsDeterministicallySorted() {
        var tree = new FileTree();
        var root = tree.root();

        tree.mkdir("/b", root);
        tree.touch("z", root);
        tree.mkdir("/a", root);
        tree.touch("m", root);

        assertEquals(java.util.List.of("a", "b", "m", "z"), tree.ls("/", root));
    }

    @Test
    void mkdirSupportsRelativeDotDot() {
        var tree = new FileTree();
        var root = tree.root();

        tree.mkdir("/a", root);
        var a = tree.cd("/a", root);

        tree.mkdir("../b", a);
        assertSame(tree.resolve("/b", root), tree.resolve("b", root));
    }

    @Test
    void mkdirCreation() {
        FileTree tree = new FileTree();
        DirectoryNode root = tree.root();
        assertEquals(root.name(), "/");
    }
}
