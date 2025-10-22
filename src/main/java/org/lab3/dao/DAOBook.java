package org.lab3.dao;

import org.lab3.pool.JDBCConnectionException;
import org.lab3.model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public DAOBook() throws JDBCConnectionException {
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

        } catch (SQLException e) {
            throw new JDBCConnectionException("Failed to create Book", e);
        }
        finally {
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
                return new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("copies")
                );
            }
            return null;

        } catch (SQLException e) {
            throw new JDBCConnectionException("Failed to read Book", e);
        }
        finally {
            pool.releaseConnection(conn);
        }
    }
    public void update(Book book) throws JDBCConnectionException {
        Connection conn = null;

        try {
             conn = pool.getConnection();

             PreparedStatement ps = conn.prepareStatement(UPDATE_BOOK);

            ps.setString(1, String.valueOf(book.getTitle()));
            ps.setString(2, String.valueOf(book.getAuthor()));
            ps.setInt(3, book.getCopies());
            ps.setInt(4, book.getId());

            ps.executeUpdate();

        } catch (SQLException | JDBCConnectionException e) {
            throw new JDBCConnectionException("Failed to update Book", e);
        }
        finally {
            pool.releaseConnection(conn);
        }
    }
    public void delete(int id) throws JDBCConnectionException {
        Connection conn = null;
        try {
             conn = pool.getConnection();

             PreparedStatement ps = conn.prepareStatement(DELETE_BOOK);

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException | JDBCConnectionException e) {
            throw new JDBCConnectionException("Failed to delete Book", e);
        }
        finally {
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
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("copies")
                );
                books.add(book);
            }
        } catch (SQLException e) {
            throw new JDBCConnectionException("Failed to get all Books", e);
        }
        finally {
            pool.releaseConnection(conn);
        }

        return books;

    }
}
