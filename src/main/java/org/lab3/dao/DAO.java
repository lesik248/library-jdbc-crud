package org.lab3.dao;

import org.lab3.db.JDBCConnectionException;
import org.lab3.db.JdbcConnector;

import java.sql.SQLException;
import java.util.List;

public abstract class DAO<T> {
    protected final JdbcConnector connector = new JdbcConnector();

    public abstract void create(T item) throws SQLException, JDBCConnectionException;
    public abstract T read(int id) throws SQLException, JDBCConnectionException;
    public abstract void update(T entity) throws SQLException, JDBCConnectionException;
    public abstract void delete(int id) throws SQLException, JDBCConnectionException;
    public abstract List<T> getAll() throws SQLException, JDBCConnectionException;
}
