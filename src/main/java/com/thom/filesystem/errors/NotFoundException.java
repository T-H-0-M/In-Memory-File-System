package com.thom.filesystem.errors;

public final class NotFoundException extends FileSystemException {
    public NotFoundException(String message) {
        super(message);
    }
}
