package com.thom.filesystem.core;

import com.thom.filesystem.errors.AlreadyExistsException;
import com.thom.filesystem.errors.InvalidPathException;
import com.thom.filesystem.errors.NotADirectoryException;
import com.thom.filesystem.errors.NotFoundException;
import com.thom.filesystem.models.DirectoryNode;
import com.thom.filesystem.models.FileNode;
import com.thom.filesystem.models.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class FileTree {
    private final DirectoryNode root;

    public FileTree() {
        this.root = new DirectoryNode("/", null);
    }

    public DirectoryNode root() {
        return root;
    }

    public Node resolve(String path, DirectoryNode cwd) {
        Objects.requireNonNull(cwd, "cwd");
        var normalized = normalizePath(path);

        DirectoryNode current = normalized.isAbsolute ? root : cwd;
        if (normalized.segments.isEmpty()) {
            return current;
        }

        Node result = current;
        for (int i = 0; i < normalized.segments.size(); i++) {
            var segment = normalized.segments.get(i);
            var isLast = i == normalized.segments.size() - 1;

            if ("..".equals(segment)) {
                var parent = current.parent();
                current = parent == null ? current : parent;
                result = current;
                continue;
            }

            var child = resolveChildOrThrow(current, segment);
            result = child;

            if (!isLast) {
                if (child instanceof DirectoryNode childDir) {
                    current = childDir;
                } else {
                    throw new NotADirectoryException("not a directory: " + child.name());
                }
                continue;
            }

            if (normalized.hasTrailingSlash && !(child instanceof DirectoryNode)) {
                throw new NotADirectoryException("not a directory: " + child.name());
            }
        }

        return result;
    }

    public DirectoryNode resolveDir(String path, DirectoryNode cwd) {
        var node = resolve(path, cwd);
        if (node instanceof DirectoryNode dir) {
            return dir;
        }
        throw new NotADirectoryException("not a directory: " + node.name());
    }

    public ResolvedParent resolveParent(String path, DirectoryNode cwd) {
        Objects.requireNonNull(cwd, "cwd");
        var normalized = normalizePath(path);
        if (normalized.hasTrailingSlash) {
            throw new InvalidPathException("path cannot have trailing '/'");
        }
        if (normalized.segments.isEmpty()) {
            throw new InvalidPathException("path must include a leaf name");
        }

        var leafName = normalized.segments.getLast();
        if ("..".equals(leafName)) {
            throw new InvalidPathException("leaf name cannot be '..'");
        }

        DirectoryNode current = normalized.isAbsolute ? root : cwd;
        for (int i = 0; i < normalized.segments.size() - 1; i++) {
            var segment = normalized.segments.get(i);

            if ("..".equals(segment)) {
                var parent = current.parent();
                current = parent == null ? current : parent;
                continue;
            }

            var child = resolveChildOrThrow(current, segment);
            if (child instanceof DirectoryNode childDir) {
                current = childDir;
            } else {
                throw new NotADirectoryException("not a directory: " + child.name());
            }
        }

        return new ResolvedParent(current, leafName);
    }

    public DirectoryNode mkdir(String path, DirectoryNode cwd) {
        Objects.requireNonNull(cwd, "cwd");
        var normalized = removeTrailingSlashes(path);

        var resolvedParent = resolveParent(normalized, cwd);
        var parent = resolvedParent.parent();
        var leafName = resolvedParent.leafName();

        var newDir = new DirectoryNode(leafName, parent);
        parent.addChild(newDir);
        return newDir;
    }

    public FileNode touch(String fileName, DirectoryNode cwd) {
        Objects.requireNonNull(cwd, "cwd");
        if (fileName == null || fileName.isBlank()) {
            throw new InvalidPathException("file name cannot be blank");
        }
        if (fileName.contains("/")) {
            throw new InvalidPathException("touch only accepts a filename in cwd");
        }
        if (".".equals(fileName) || "..".equals(fileName)) {
            throw new InvalidPathException("invalid file name: " + fileName);
        }

        if (cwd.getChild(fileName) != null) {
            throw new AlreadyExistsException("name already exists: " + fileName);
        }

        var file = new FileNode(fileName, cwd);
        cwd.addChild(file);
        return file;
    }

    public List<String> ls(String path, DirectoryNode cwd) {
        Objects.requireNonNull(cwd, "cwd");
        if (path == null || path.isBlank()) {
            return cwd.childNames();
        }
        return resolveDir(path, cwd).childNames();
    }

    public DirectoryNode cd(String path, DirectoryNode cwd) {
        return resolveDir(path, cwd);
    }

    public record ResolvedParent(DirectoryNode parent, String leafName) {
        public ResolvedParent {
            Objects.requireNonNull(parent, "parent");
            Objects.requireNonNull(leafName, "leafName");
            if (leafName.isBlank()) {
                throw new InvalidPathException("leaf name cannot be blank");
            }
            if (leafName.contains("/")) {
                throw new InvalidPathException("leaf name cannot contain '/'");
            }
        }
    }

    private static Node resolveChildOrThrow(DirectoryNode dir, String segment) {
        var child = dir.getChild(segment);
        if (child == null) {
            throw new NotFoundException("not found: " + segment);
        }
        return child;
    }

    private static NormalizedPath normalizePath(String path) {
        if (path == null || path.isBlank()) {
            throw new InvalidPathException("path cannot be blank");
        }

        var isAbsolute = path.startsWith("/");
        var hasTrailingSlash = path.length() > 1 && path.endsWith("/");

        var segments = new ArrayList<String>();
        for (String part : path.split("/")) {
            if (part.isEmpty() || ".".equals(part)) {
                continue;
            }
            segments.add(part);
        }

        return new NormalizedPath(isAbsolute, hasTrailingSlash, List.copyOf(segments));
    }

    private static String removeTrailingSlashes(String path) {
        if (path == null) {
            throw new InvalidPathException("path cannot be blank");
        }
        var trimmed = path;
        while (trimmed.length() > 1 && trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        if (trimmed.isBlank()) {
            throw new InvalidPathException("path cannot be blank");
        }
        return trimmed;
    }

    private record NormalizedPath(boolean isAbsolute, boolean hasTrailingSlash, List<String> segments) {
    }
}
