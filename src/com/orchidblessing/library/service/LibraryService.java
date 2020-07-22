package com.orchidblessing.library.service;

import java.math.BigDecimal;

public interface LibraryService {
    void addBook(String bookName, String author, String press, BigDecimal price, int stock);
    void addUser(String userName, Integer age, String email);
    void borrowBook(String username, String bookName);
    void returnBook(String username, String bookName);
    void delBook(String bookName);
    void queryBook(String bookName);
    void queryBookByAuthor(String author);
    void queryMyBorrowBook(String username);
    void queryBookBorrowUser(String bookName);
    void queryBookBorrowCount();
}
