package org.lab3.dao;

import org.lab3.pool.JDBCConnectionException;
import org.lab3.model.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DAOLog extends DAO<Log> {

    private static final String CREATE_LOG =
            "INSERT INTO logbook (book_id, reader_id, issue_date, return_date, debt) VALUES (?, ?, ?, ?, ?)";
    private static final String READ_LOG =
            "SELECT * FROM logbook WHERE id = ?";
    private static final String UPDATE_LOG =
            "UPDATE logbook SET book_id = ?, reader_id = ?, issue_date = ?, return_date = ?, debt = ? WHERE id = ?";
    private static final String DELETE_LOG =
            "DELETE FROM logbook WHERE id = ?";
    private static final String SELECT_ALL_LOG =
            "SELECT * FROM logbook";

    private static final Logger logger = Logger.getLogger(DAOLog.class.getName());

    public DAOLog() throws JDBCConnectionException {
        super();
    }

    public void create(Log log) {
        Connection conn = null;
        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(CREATE_LOG);

            ps.setInt(1, log.getBookId());
            ps.setInt(2, log.getReaderId());
            ps.setString(3, log.getIssueDate());
            ps.setString(4, log.getReturnDate());
            ps.setInt(5, log.getDebt());
            ps.executeUpdate();

            logger.info("Новая запись в журнал успешно создана.");

        } catch (JDBCConnectionException e) {
            logger.log(Level.SEVERE, "Ошибка подключения при создании записи в журнале", e);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка SQL при создании записи в журнале", e);
        } finally {
            pool.releaseConnection(conn);
        }
    }

    public Log read(int id) {
        Connection conn = null;
        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(READ_LOG);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Log log = new Log(
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getInt("reader_id"),
                        rs.getString("issue_date"),
                        rs.getString("return_date"),
                        rs.getInt("debt")
                );
                logger.info("Запись в журнале успешно найдена: ID=" + id);
                return log;
            } else {
                logger.warning("Запись в журнале с ID=" + id + " не найдена.");
            }

        } catch (JDBCConnectionException e) {
            logger.log(Level.SEVERE, "Ошибка подключения при чтении записи из журнала", e);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка SQL при чтении записи из журнала", e);
        } finally {
            pool.releaseConnection(conn);
        }
        return null;
    }

    public void update(Log log) {
        Connection conn = null;
        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(UPDATE_LOG);

            ps.setInt(1, log.getBookId());
            ps.setInt(2, log.getReaderId());
            ps.setString(3, log.getIssueDate());
            ps.setString(4, log.getReturnDate());
            ps.setInt(5, log.getDebt());
            ps.setInt(6, log.getId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                logger.info("Запись в журнале успешно обновлена: ID=" + log.getId());
            } else {
                logger.warning("Не удалось обновить запись: ID=" + log.getId());
            }

        } catch (JDBCConnectionException e) {
            logger.log(Level.SEVERE, "Ошибка подключения при обновлении записи журнала", e);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка SQL при обновлении записи журнала", e);
        } finally {
            pool.releaseConnection(conn);
        }
    }

    public void delete(int id) {
        Connection conn = null;
        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(DELETE_LOG);
            ps.setInt(1, id);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                logger.info("Запись успешно удалена: ID=" + id);
            } else {
                logger.warning("Не удалось удалить запись: ID=" + id);
            }

        } catch (JDBCConnectionException e) {
            logger.log(Level.SEVERE, "Ошибка подключения при удалении записи из журнала", e);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка SQL при удалении записи из журнала", e);
        } finally {
            pool.releaseConnection(conn);
        }
    }

    public List<Log> getAll() {
        Connection conn = null;
        List<Log> logs = new ArrayList<>();

        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(SELECT_ALL_LOG);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                logs.add(new Log(
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getInt("reader_id"),
                        rs.getString("issue_date"),
                        rs.getString("return_date"),
                        rs.getInt("debt")
                ));
            }

            logger.info("Список всех записей успешно получен. Количество записей: " + logs.size());

        } catch (JDBCConnectionException e) {
            logger.log(Level.SEVERE, "Ошибка подключения при получении списка записей", e);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка SQL при получении списка записей", e);
        } finally {
            pool.releaseConnection(conn);
        }
        return logs;
    }
}
