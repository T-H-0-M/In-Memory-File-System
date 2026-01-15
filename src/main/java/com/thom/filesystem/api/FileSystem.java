package com.thom.filesystem.api;

import java.util.List;

public interface FileSystem {
    void mkdir(String path);

    void cd(String path);

    List<String> ls();

    List<String> ls(String path);

    void touch(String filename);
}
