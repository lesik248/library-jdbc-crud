package org.lab3.pool;

import org.lab3.config.ConfigurationManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionPool {
    private static ConnectionPool instance;
    private final ArrayBlockingQueue<Connection> pool;
    private static final Logger logger = Logger.getLogger(ConnectionPool.class.getName());

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
            logger.log(Level.INFO, "Создан пул с "+ cfg.getPoolSize() + "подключениями");

        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Драйвер не загружен");
            throw new JDBCConnectionException("Драйвер не загружен", e);
        } catch (SQLException e) {
            logger.log(Level.INFO, "Не получилось создать соединение", e);
            throw new JDBCConnectionException("Не получилось создать соединение", e);
        }
    }
    public static synchronized ConnectionPool getInstance() throws JDBCConnectionException {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        logger.fine("Создан новый экземпляр ConnectionPool");
        return instance;
    }
    public Connection getConnection() throws JDBCConnectionException {
        try {
            Connection conn = pool.take();
            logger.fine("Выдано соединение из пула. Осталось: " + pool.size());
            return conn;
        }
        catch (InterruptedException e) {
            logger.log(Level.WARNING, "Поток был прерван при ожидании соединения", e);
            throw new JDBCConnectionException("Ошибка при получении соединения из пула.", e);
        }
    }
    public void releaseConnection(Connection connection) {
        if (connection != null) {
            pool.offer(connection);
            logger.log(Level.INFO,"Соединение возвращено в пул. Доступно: " + pool.size());
        }
    }
    public void closeAllConnections() throws JDBCConnectionException {
        try {
            for (Connection conn : pool) {
                conn.close();
            }
            logger.info("Все соединения пула успешно закрыты");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при закрытии соединений", e);
            throw new JDBCConnectionException("Ошибка при закрытии соединений", e);
        }
    }
}
