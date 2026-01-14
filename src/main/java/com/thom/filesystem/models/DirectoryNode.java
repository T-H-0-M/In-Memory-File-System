package com.thom.filesystem.models;

import com.thom.filesystem.errors.AlreadyExistsException;
import com.thom.filesystem.errors.InvalidPathException;

import java.util.Collection;
import java.util.List;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

public final class DirectoryNode implements Node {
    private final String name;
    private final DirectoryNode parent;

    private final NavigableMap<String, Node> children = new TreeMap<>();

    public DirectoryNode(String name, DirectoryNode parent) {
        this.name = Objects.requireNonNull(name, "name");
        this.parent = parent;

        if (isRoot()) {
            if (!"/".equals(name)) {
                throw new InvalidPathException("root directory name must be '/'");
            }
        } else {
            validateNonRootName(name, "directory");
        }
    }

    private boolean isRoot() {
        return parent == null;
    }

    private static void validateNonRootName(String name, String kind) {
        if (name.isBlank()) {
            throw new InvalidPathException(kind + " name cannot be blank");
        }
        if (name.contains("/")) {
            throw new InvalidPathException(kind + " name cannot contain '/'");
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

    public Node getChild(String childName) {
        Objects.requireNonNull(childName, "childName");
        return children.get(childName);
    }

    public Node addChild(Node node) {
        Objects.requireNonNull(node, "node");
        if (node.parent() != this) {
            throw new IllegalArgumentException("child node parent must be this directory");
        }
        var existing = children.get(node.name());
        if (existing != null) {
            throw new AlreadyExistsException("name already exists: " + node.name());
        }
        children.put(node.name(), node);
        return node;
    }

    public Collection<Node> children() {
        return children.values();
    }

    public List<String> childNames() {
        return List.copyOf(children.navigableKeySet());
    }
}
