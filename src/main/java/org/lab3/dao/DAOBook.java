package org.lab3.dao;

import org.lab3.pool.JDBCConnectionException;
import org.lab3.model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DAOBook extends DAO<Book> {

    private static final String CREATE_BOOK =
            "INSERT INTO book (title, author, copies) VALUES (?, ?, ?)";
    private static final String READ_BOOK =
            "SELECT * FROM book WHERE id = ?";
    private static final String UPDATE_BOOK =
            "UPDATE book SET title = ?, author = ?, copies = ? WHERE id = ?";
    private static final String DELETE_BOOK =
            "DELETE FROM book WHERE id = ?";
    private static final String SELECT_ALL_BOOK =
            "SELECT * FROM book";

    private static final Logger logger = Logger.getLogger(DAOBook.class.getName());

    public DAOBook() throws JDBCConnectionException {
        super();
    }

    public void create(Book book) throws JDBCConnectionException {
        Connection conn = null;
        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(CREATE_BOOK);
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setInt(3, book.getCopies());

            ps.executeUpdate();
            logger.info("Книга успешно добавлена: " + book.getTitle());

        } catch (JDBCConnectionException e) {
            logger.log(Level.SEVERE, "Ошибка подключения при добавлении книги", e);
            throw e;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка SQL при добавлении книги", e);
            throw new JDBCConnectionException("Не удалось добавить книгу в базу данных", e);
        } finally {
            pool.releaseConnection(conn);
        }
    }

    public Book read(int id) throws JDBCConnectionException {
        Connection conn = null;
        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(READ_BOOK);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("copies")
                );
                logger.info("Книга успешно найдена: ID=" + id);
                return book;
            } else {
                logger.warning("Книга с ID=" + id + " не найдена.");
                return null;
            }

        } catch (JDBCConnectionException e) {
            logger.log(Level.SEVERE, "Ошибка подключения при чтении книги", e);
            throw e;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка SQL при чтении книги", e);
            throw new JDBCConnectionException("Не удалось прочитать книгу из базы данных", e);
        } finally {
            pool.releaseConnection(conn);
        }
    }

    public void update(Book book) throws JDBCConnectionException {
        Connection conn = null;
        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(UPDATE_BOOK);
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setInt(3, book.getCopies());
            ps.setInt(4, book.getId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                logger.info("Книга успешно обновлена: ID=" + book.getId());
            } else {
                logger.warning("Не удалось обновить книгу: ID=" + book.getId());
            }

        } catch (JDBCConnectionException e) {
            logger.log(Level.SEVERE, "Ошибка подключения при обновлении книги", e);
            throw e;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка SQL при обновлении книги", e);
            throw new JDBCConnectionException("Не удалось обновить книгу в базе данных", e);
        } finally {
            pool.releaseConnection(conn);
        }
    }

    public void delete(int id) throws JDBCConnectionException {
        Connection conn = null;
        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(DELETE_BOOK);
            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                logger.info("Книга успешно удалена: ID=" + id);
            } else {
                logger.warning("Не удалось удалить книгу: ID=" + id);
            }

        } catch (JDBCConnectionException e) {
            logger.log(Level.SEVERE, "Ошибка подключения при удалении книги", e);
            throw e;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка SQL при удалении книги", e);
            throw new JDBCConnectionException("Не удалось удалить книгу из базы данных", e);
        } finally {
            pool.releaseConnection(conn);
        }
    }

    public List<Book> getAll() throws JDBCConnectionException {
        Connection conn = null;
        List<Book> books = new ArrayList<>();

        try {
            conn = pool.getConnection();
            PreparedStatement ps = conn.prepareStatement(SELECT_ALL_BOOK);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("copies")
                ));
            }

            logger.info("Все книги успешно получены. Количество: " + books.size());

        } catch (JDBCConnectionException e) {
            logger.log(Level.SEVERE, "Ошибка подключения при получении списка книг", e);
            throw e;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка SQL при получении списка книг", e);
            throw new JDBCConnectionException("Не удалось получить список книг из базы данных", e);
        } finally {
            pool.releaseConnection(conn);
        }

        return books;
    }
}
