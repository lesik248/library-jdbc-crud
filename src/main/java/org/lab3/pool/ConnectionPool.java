package org.lab3.pool;

import org.lab3.config.ConfigurationManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;

public class ConnectionPool {
    private static ConnectionPool instance;
    private final ArrayBlockingQueue<Connection> pool;

    private ConnectionPool() throws JDBCConnectionException {
        ConfigurationManager cfg = ConfigurationManager.getInstance();
        pool = new ArrayBlockingQueue<>(cfg.getPoolSize());

        try {
            Class.forName(cfg.getDriverName());
            for (int i = 0; i < cfg.getPoolSize(); i++) {
                Connection conn = DriverManager.getConnection(
                        cfg.getURL(),
                        cfg.getUsername(),
                        cfg.getPassword()
                );
                pool.offer(conn);
            }
            System.out.println("Connection pool initialized with " + cfg.getPoolSize() + " connections.");
        } catch (ClassNotFoundException e) {
            throw new JDBCConnectionException("Can't load driver.", e);
        } catch (SQLException e) {
            throw new JDBCConnectionException("Can't create connection.", e);
        }
    }
    public static synchronized ConnectionPool getInstance() throws JDBCConnectionException {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }
    public Connection getConnection() throws JDBCConnectionException {
        try {
            return pool.take();
        }
        catch (InterruptedException e) {
            throw new JDBCConnectionException("Can't get connection.", e);
        }
    }
    public void releaseConnection(Connection connection) {
        if (connection != null) {
            pool.offer(connection);
        }
    }
    public void closeAllConnections() throws SQLException {
        for (Connection conn : pool) {
            conn.close();
        }
    }
}
