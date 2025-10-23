package org.lab3.service;
import org.lab3.pool.ConnectionPool;
import org.lab3.pool.JDBCConnectionException;
import org.lab3.dao.DAOBook;
import org.lab3.dao.DAOLog;
import org.lab3.dao.DAOReader;
import org.lab3.model.Book;
import org.lab3.model.Reader;
import org.lab3.model.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LibraryService {
    private final DAOBook daoBook;
    private final DAOLog daoLog;
    private final DAOReader daoReader;

    private static final Logger logger = Logger.getLogger(LibraryService.class.getName());

    public LibraryService() throws JDBCConnectionException {

            daoBook = new DAOBook();
            daoLog = new DAOLog();
            daoReader = new DAOReader();
    }
    private Book getBookByTitle(String bookTitle, String author) {
            List<Book> books = daoBook.getAll();
            Book targetBook = null;

            for (Book book : books) {
                author = author.trim().toLowerCase();
                if (book.getTitle().equalsIgnoreCase(bookTitle) && book.getAuthor().equalsIgnoreCase(author)) {
                    targetBook = book;
                }
            }
            if (targetBook == null) {
                throw new LibraryServiceException("Книга " + bookTitle + " не найдена");
            }
            return targetBook;
    }
    public int getFreeCopiesOfBook(String author, String bookTitle) {
            Book targetBook = getBookByTitle(bookTitle, author);
            int freeCopies = targetBook.getCopies();
            List<Log> logs = daoLog.getAll();
            for (Log log : logs) {
                if (log.getBookId() == targetBook.getId()) {
                    freeCopies--;
                }
            }
            return freeCopies;
    }
    public List<Reader> getReadersWithDebt() {
            List<Log> logs = daoLog.getAll();
            List<Reader> readersWithDebt = new ArrayList<>();
            for (Log log : logs) {
                if (log.getDebt() > 30) {
                    readersWithDebt.add(daoReader.read(log.getReaderId()));
                }
            }
            if (readersWithDebt.isEmpty()) {
                throw new LibraryServiceException("Нет читателей с задолженностью более 1 месяца");
            }
            return readersWithDebt;
    }
    public HashMap<Book, Integer> getBooksForAuthor(String author) {
            HashMap<Book, Integer> booksInfo = new HashMap<>();
            List<Book> books = daoBook.getAll();
            for (Book book : books) {
                String bookAuthor = book.getAuthor();
                if (bookAuthor.equalsIgnoreCase(author)) {
                    int copies = getFreeCopiesOfBook(bookAuthor, book.getTitle());
                    booksInfo.put(book, copies);
                }
            }
            if (booksInfo.isEmpty()) {
                throw new LibraryServiceException("Нет книг этого автора");
            }
            return booksInfo;
    }
    public Reader getReaderByName(String name) {
            List<Reader> readers = daoReader.getAll();

            Reader targetReader = null;
            for (Reader reader : readers) {
                if (reader.getName().equalsIgnoreCase(name)) {
                    targetReader = reader;
                }
            }
            return targetReader;
    }

    public void giveBook(String name, String author, String bookTitle) {
            Book targetBook = getBookByTitle(bookTitle, author);

            Reader targetReader = getReaderByName(name);

            if (targetReader == null) {
                targetReader = new Reader(1, name);
                daoReader.create(targetReader);
                targetReader = getReaderByName(name);
            }

            LocalDate issueDate = LocalDate.now();
            LocalDate returnDate = issueDate.plusWeeks(2);
            long debtDays = Math.max(ChronoUnit.DAYS.between(returnDate, LocalDate.now()), 0);

            daoLog.create(new Log(
                    1,
                    targetBook.getId(),
                    targetReader.getId(),
                    issueDate.toString(),
                    returnDate.toString(),
                    (int) debtDays
            ));
    }
    public void removeBook(String author, String title) {
        if (getFreeCopiesOfBook(author, title) > 0) {
            Book targetBook = getBookByTitle(title, author);
            targetBook.setCopies(targetBook.getCopies() - 1);
            daoBook.update(targetBook);
            if (targetBook.getCopies() == 0) {
                daoBook.delete(targetBook.getId());
            }
        }
    }
    public void close() throws LibraryServiceException {
        try {
            ConnectionPool.getInstance().closeAllConnections();
        }
        catch (JDBCConnectionException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new LibraryServiceException("Не удалось закрыть соединения");
        }
    }
}
