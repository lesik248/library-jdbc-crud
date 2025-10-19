package org.lab3.dao;

import org.lab3.db.JDBCConnectionException;
import org.lab3.model.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public void create(Log log) throws JDBCConnectionException {

        try (Connection conn = connector.getConnection();
             PreparedStatement ps = conn.prepareStatement(CREATE_LOG)) {

            ps.setInt(1, log.getBookId());
            ps.setInt(2, log.getReaderId());
            ps.setString(3, log.getIssueDate());
            ps.setString(4, log.getReturnDate());
            ps.setInt(5, log.getDebt());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new JDBCConnectionException("Failed to create Log", e);
        }
    }
    public Log read(int id) throws JDBCConnectionException {
        try (Connection conn = connector.getConnection();
             PreparedStatement ps = conn.prepareStatement(READ_LOG)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Log(
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getInt("reader_id"),
                        rs.getString("issue_date"),
                        rs.getString("return_date"),
                        rs.getInt("debt")
                );
            }
            return null;

        } catch (SQLException e) {
            throw new JDBCConnectionException("Failed to read Log", e);
        }
    }
    public void update(Log log) throws JDBCConnectionException {
        try (Connection conn = connector.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_LOG)) {

            ps.setString(1, String.valueOf(log.getBookId()));
            ps.setString(2, String.valueOf(log.getReaderId()));
            ps.setString(3, log.getIssueDate());
            ps.setString(4, log.getReturnDate());
            ps.setInt(5, log.getDebt());
            ps.executeUpdate();

        } catch (SQLException | JDBCConnectionException e) {
            throw new JDBCConnectionException("Failed to update Log", e);
        }
    }
    public void delete(int id) throws JDBCConnectionException {
        try (Connection conn = connector.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_LOG)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException | JDBCConnectionException e) {
            throw new JDBCConnectionException("Failed to delete Log", e);
        }
    }
    public List<Log> getAll() throws JDBCConnectionException {
        List<Log> logs = new ArrayList<>();

        try (Connection conn = connector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_LOG)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Log log = new Log(
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getInt("reader_id"),
                        rs.getString("issue_date"),
                        rs.getString("return_date"),
                        rs.getInt("debt")
                );
                logs.add(log);
            }
        } catch (SQLException | JDBCConnectionException e) {
            throw new JDBCConnectionException("Failed to delete Log", e);
        }
        return logs;
    }
}
