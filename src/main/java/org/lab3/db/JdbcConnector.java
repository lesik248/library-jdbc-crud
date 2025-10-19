package org.lab3.db;

import org.lab3.config.ConfigurationManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcConnector {

    public Connection getConnection() throws JDBCConnectionException {
        ConfigurationManager cfg = ConfigurationManager.getInstance();
        try {
            Class.forName(cfg.getDriverName());
            return DriverManager.getConnection(
                    cfg.getURL(),
                    cfg.getUsername(),
                    cfg.getPassword()
            );
        } catch (ClassNotFoundException e) {
            throw new JDBCConnectionException("Can't load database driver.", e);
        } catch (SQLException e) {
            throw new JDBCConnectionException("Can't connect to database.", e);
        }
    }
}
