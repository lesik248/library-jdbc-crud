package org.lab3.dao;

import org.lab3.pool.JDBCConnectionException;
import org.lab3.model.Reader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DAOReader extends DAO<Reader> {

    private static final String CREATE_READER =
            "INSERT INTO reader (name) VALUES (?)";
    private static final String READ_READER =
            "SELECT * FROM reader WHERE id = ?";
    private static final String UPDATE_READER =
            "UPDATE reader SET name = ? WHERE id = ?";
    private static final String DELETE_READER =
            "DELETE FROM reader WHERE id = ?";
    private static final String SELECT_ALL_READER =
            "SELECT * FROM reader";

    public DAOReader() throws JDBCConnectionException {
    }

    public void create(Reader reader) throws JDBCConnectionException {
        Connection conn = null;

        try {
            conn = pool.getConnection();
             PreparedStatement ps = conn.prepareStatement(CREATE_READER);

            ps.setString(1, reader.getName());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new JDBCConnectionException("Failed to create Reader", e);
        }
        finally {
            pool.releaseConnection(conn);
        }
    }
    public Reader read(int id) throws JDBCConnectionException {
        Connection conn = null;

        try {
            conn = pool.getConnection();
             PreparedStatement ps = conn.prepareStatement(READ_READER);

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Reader(
                        rs.getInt("id"),
                        rs.getString("name")
                );
            }
            return null;

        } catch (SQLException e) {
            throw new JDBCConnectionException("Failed to read Reader", e);
        }
        finally {
            pool.releaseConnection(conn);
        }
    }
    public void update(Reader reader) throws JDBCConnectionException {
        Connection conn = null;

        try {
            conn = pool.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_READER);

            ps.setString(1, String.valueOf(reader.getName()));
            ps.executeUpdate();

        } catch (SQLException | JDBCConnectionException e) {
            throw new JDBCConnectionException("Failed to update Reader", e);
        }
        finally {
            pool.releaseConnection(conn);
        }
    }
    public void delete(int id) throws JDBCConnectionException {
        Connection conn = null;

        try {
            conn = pool.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_READER);

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException | JDBCConnectionException e) {
            throw new JDBCConnectionException("Failed to delete Reader", e);
        }
        finally {
            pool.releaseConnection(conn);
        }
    }
    public List<Reader> getAll() throws JDBCConnectionException {
        Connection conn = null;
        List<Reader> readers = new ArrayList<>();

        try {
            conn = pool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_READER);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Reader reader = new Reader(
                        rs.getInt("id"),
                        rs.getString("name")
                );
                readers.add(reader);
            }
        } catch (SQLException | JDBCConnectionException e) {
            throw new JDBCConnectionException("Failed to delete Reader", e);
        }
        finally {
            pool.releaseConnection(conn);
        }
        return readers;
    }
}
