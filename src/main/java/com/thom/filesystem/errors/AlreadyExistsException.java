package com.thom.filesystem.errors;

public final class AlreadyExistsException extends FileSystemException {
    public AlreadyExistsException(String message) {
        super(message);
    }
}
