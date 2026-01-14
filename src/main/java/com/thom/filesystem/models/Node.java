package com.thom.filesystem.models;

public sealed interface Node permits DirectoryNode, FileNode {
    String name();

    DirectoryNode parent();
}
