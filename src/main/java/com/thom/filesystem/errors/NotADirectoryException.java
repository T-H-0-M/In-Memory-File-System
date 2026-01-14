package com.thom.filesystem.errors;

public final class NotADirectoryException extends FileSystemException {
    public NotADirectoryException(String message) {
        super(message);
    }
}
