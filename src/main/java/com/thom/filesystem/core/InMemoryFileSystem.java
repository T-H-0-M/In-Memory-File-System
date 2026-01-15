package com.thom.filesystem.core;

import com.thom.filesystem.api.FileSystem;
import com.thom.filesystem.models.DirectoryNode;

import java.util.List;
import java.util.Objects;

public final class InMemoryFileSystem implements FileSystem {
    private final FileTree tree;
    private DirectoryNode cwd;

    public InMemoryFileSystem() {
        this(new FileTree());
    }

    public InMemoryFileSystem(FileTree tree) {
        this.tree = Objects.requireNonNull(tree, "tree");
        this.cwd = tree.root();
    }

    @Override
    public void mkdir(String path) {
        tree.mkdir(path, cwd);
    }

    @Override
    public void cd(String path) {
        cwd = tree.cd(path, cwd);
    }

    @Override
    public List<String> ls() {
        return tree.ls(null, cwd);
    }

    @Override
    public List<String> ls(String path) {
        return tree.ls(path, cwd);
    }

    @Override
    public void touch(String filename) {
        tree.touch(filename, cwd);
    }
}
