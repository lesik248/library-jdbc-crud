package org.lab3.pool;

public class JDBCConnectionException extends Exception {
    public JDBCConnectionException(String message) {
        super(message);
    }
    public JDBCConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
