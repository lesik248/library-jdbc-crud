package org.lab3.service;

import org.lab3.connector.JDBCConnectionException;
import org.lab3.dao.DAOBook;
import org.lab3.dao.DAOLog;
import org.lab3.dao.DAOReader;
import org.lab3.model.Book;
import org.lab3.model.Reader;
import org.lab3.model.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LibraryService {
    private final DAOBook daoBook;
    private final DAOLog daoLog;
    private final DAOReader daoReader;

    public LibraryService() {
        daoBook = new DAOBook();
        daoLog = new DAOLog();
        daoReader = new DAOReader();
    }
    private Book getBookByTitle(String bookTitle, String author) throws LibraryServiceException {
        try {
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
        catch (JDBCConnectionException e) {
            throw new LibraryServiceException("Ошибка доступа к БД", e);
        }
    }

    public int getFreeCopiesOfBook(String author, String bookTitle) throws LibraryServiceException {
        try {
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
        catch (JDBCConnectionException e) {
            throw new LibraryServiceException("Ошибка доступа к БД", e);
        }
    }

    public List<Reader> getReadersWithDebt() throws LibraryServiceException {
        try {
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
        catch (JDBCConnectionException e) {
                throw new LibraryServiceException("Ошибка доступа к БД", e);
        }
    }

    public HashMap<Book, Integer> getBooksForAuthor(String author) throws LibraryServiceException {
        try {
            HashMap<Book, Integer> booksInfo = new HashMap<>();

            List<Log> logs = daoLog.getAll();
            for (Log log : logs) {
                Book book = daoBook.read(log.getBookId());
                if (book.getAuthor().equalsIgnoreCase(author)) {
                    int copies = getFreeCopiesOfBook(book.getAuthor(), book.getTitle());
                    booksInfo.put(book, copies);
                }
            }
            return booksInfo;
        }
        catch (JDBCConnectionException e) {
            throw new LibraryServiceException("Ошибка доступа к БД", e);
        }
    }

    public void giveBook(String name, String author, String bookTitle) throws LibraryServiceException {
        try {
            Book targetBook = getBookByTitle(bookTitle, author);

            List<Reader> readers = daoReader.getAll();
            Reader targetReader = null;
            for (Reader reader : readers) {
                if (reader.getName().equalsIgnoreCase(name)) {
                    targetReader = reader;
                }
            }
            if (targetReader == null) {
                targetReader = new Reader(1, name);
                daoReader.create(targetReader);
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
        catch (JDBCConnectionException e) {
            throw new LibraryServiceException("Ошибка доступа к БД", e);
        }
    }
    public void removeBook(String author, String title) throws LibraryServiceException {
        try {
            if (getFreeCopiesOfBook(author, title) > 0) {
                Book targetBook = getBookByTitle(title, author);
                targetBook.setCopies(targetBook.getCopies() - 1);
                daoBook.update(targetBook);
                if (targetBook.getCopies() == 0) {
                    daoBook.delete(targetBook.getId());
                }
            }
        }
        catch (JDBCConnectionException e) {
            throw new LibraryServiceException("Ошибка доступа к БД", e);
        }
    }
}
