package org.lab3.dao;

import org.lab3.pool.ConnectionPool;
import org.lab3.pool.JDBCConnectionException;

import java.sql.SQLException;
import java.util.List;

public abstract class DAO<T> {

    protected final ConnectionPool pool;

    public DAO() throws JDBCConnectionException {
        this.pool = ConnectionPool.getInstance();
    }
    public abstract void create(T item) throws SQLException, JDBCConnectionException;
    public abstract T read(int id) throws SQLException, JDBCConnectionException;
    public abstract void update(T entity) throws SQLException, JDBCConnectionException;
    public abstract void delete(int id) throws SQLException, JDBCConnectionException;
    public abstract List<T> getAll() throws SQLException, JDBCConnectionException;

}
