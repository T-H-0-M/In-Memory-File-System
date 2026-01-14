package com.thom.filesystem.models;

import com.thom.filesystem.errors.InvalidPathException;

import java.util.Objects;

public final class FileNode implements Node {
    private final String name;
    private final DirectoryNode parent;

    public FileNode(String name, DirectoryNode parent) {
        this.name = Objects.requireNonNull(name, "name");
        this.parent = Objects.requireNonNull(parent, "parent");

        if (name.isBlank()) {
            throw new InvalidPathException("file name cannot be blank");
        }
        if (name.contains("/")) {
            throw new InvalidPathException("file name cannot contain '/'");
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public DirectoryNode parent() {
        return parent;
    }
}
