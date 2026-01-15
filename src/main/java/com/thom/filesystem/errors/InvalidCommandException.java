package com.thom.filesystem.errors;

public final class InvalidCommandException extends FileSystemException {
    public InvalidCommandException(String message) {
        super(message);
    }
}
