package org.lab3.service;

import org.lab3.pool.JDBCConnectionException;

public class LibraryServiceException extends RuntimeException {
    public LibraryServiceException(String message) {
        super(message);
    }
    public LibraryServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public LibraryServiceException(Throwable e) {
        super(e);
    }
}
