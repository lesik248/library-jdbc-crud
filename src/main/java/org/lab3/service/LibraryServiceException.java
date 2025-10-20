package org.lab3.service;

public class LibraryServiceException extends RuntimeException {
    public LibraryServiceException(String message) {
        super(message);
    }
    public LibraryServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
