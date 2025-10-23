package org.lab3.dao;

import org.lab3.pool.JDBCConnectionException;
import org.lab3.model.Reader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DAOReader extends DAO<Reader> {

    private static final Logger logger = Logger.getLogger(DAOReader.class.getName());

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
            logger.info("Новый читатель успешно добавлен: " + reader.getName());
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при добавлении читателя в базу данных", e);
            throw new JDBCConnectionException("Не удалось добавить читателя в базу данных", e);
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Неизвестная ошибка при создании читателя", e);
            throw new JDBCConnectionException("Произошла неизвестная ошибка при добавлении читателя", e);
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
                Reader reader = new Reader(rs.getInt("id"), rs.getString("name"));
                logger.info("Читатель найден: " + reader.getName());
                return reader;
            } else {
                logger.warning("Читатель с ID " + id + " не найден");
                return null;
            }
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при чтении данных читателя", e);
            throw new JDBCConnectionException("Не удалось прочитать данные читателя", e);
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Неизвестная ошибка при чтении читателя", e);
            throw new JDBCConnectionException("Произошла неизвестная ошибка при чтении данных", e);
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
            ps.setString(1, reader.getName());
            ps.setInt(2, reader.getId());
            int rows = ps.executeUpdate();

            if (rows > 0) {
                logger.info("Данные читателя с ID " + reader.getId() + " успешно обновлены");
            } else {
                logger.warning("Не найден читатель для обновления с ID " + reader.getId());
            }
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при обновлении данных читателя", e);
            throw new JDBCConnectionException("Не удалось обновить данные читателя", e);
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Неизвестная ошибка при обновлении читателя", e);
            throw new JDBCConnectionException("Произошла неизвестная ошибка при обновлении данных", e);
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
            int rows = ps.executeUpdate();

            if (rows > 0) {
                logger.info("Читатель с ID " + id + " успешно удалён");
            } else {
                logger.warning("Читатель с ID " + id + " не найден для удаления");
            }
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при удалении читателя", e);
            throw new JDBCConnectionException("Не удалось удалить читателя", e);
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Неизвестная ошибка при удалении читателя", e);
            throw new JDBCConnectionException("Произошла неизвестная ошибка при удалении данных", e);
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
                readers.add(new Reader(rs.getInt("id"), rs.getString("name")));
            }

            logger.info("Извлечено " + readers.size() + " записей читателей из базы данных");
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при получении списка читателей", e);
            throw new JDBCConnectionException("Не удалось получить список читателей", e);
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Неизвестная ошибка при получении всех читателей", e);
            throw new JDBCConnectionException("Произошла неизвестная ошибка при получении списка", e);
        }
        finally {
            pool.releaseConnection(conn);
        }
        return readers;
    }
}
