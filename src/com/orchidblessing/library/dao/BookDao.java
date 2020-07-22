package com.orchidblessing.library.dao;

import com.orchidblessing.library.db.DataBaseUtils;
import com.orchidblessing.library.domain.PreparedParamDomain;
import com.orchidblessing.library.entity.Book;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDao {
    //addBook是运用了prepareStatement，避免了sql注入
    public int addBook(Book book) {
        String sql = "insert into lib_book(book_name,author,press,price,stock) value(?,?,?,?,?);";
        ArrayList arrayList = new ArrayList();
        arrayList.add(new PreparedParamDomain(book.getBookName(), String.class));
        arrayList.add(new PreparedParamDomain(book.getAuthor(), String.class));
        arrayList.add(new PreparedParamDomain(book.getPress(), String.class));
        arrayList.add(new PreparedParamDomain(book.getPrice(), BigDecimal.class));
        arrayList.add(new PreparedParamDomain(book.getStock(), Integer.class));
        int bookId = DataBaseUtils.commonUpdate(sql,arrayList);
        book.setId(bookId);
        return bookId;
    }

    public Book getBook(String bookName) {
        Book book = DataBaseUtils.queryBook(bookName);
        return book;
    }

    public Book getBookByName(String bookName) {
        String sql = "select id, book_name as bookName, author, press, price, stock, borrow_count as borrowCount from lib_book where book_name=?;";
        //为了防止sql注入，用到了preparedParamDomain来传数据和表示其类型的字节码
        ArrayList arrayList = new ArrayList();
        arrayList.add(new PreparedParamDomain(bookName,String.class));

        List<Book> books = DataBaseUtils.commonQuery(Book.class,sql,arrayList);
        return books.isEmpty() ? null : books.get(0);
    }

    public List<Book> listBookByAuthor(String author) {
        String sql = "select id, book_name as bookName, author, press, price, stock, borrow_count as borrowCount from lib_book where author=?;";
        //为了防止sql注入，用到了preparedParamDomain来传数据和表示其类型的字节码
        ArrayList arrayList = new ArrayList();
        arrayList.add(new PreparedParamDomain(author,String.class));
        return DataBaseUtils.commonQuery(Book.class, sql, arrayList);
    }

    public int updateStock4Borrow(String bookName) {
        int rows = 0;
        rows = DataBaseUtils.reduceStock(bookName);
        return rows;
    }

    public int updateStock4Return(String bookName) {
        int rows = 0;
        rows = DataBaseUtils.increaseStock(bookName);
        return rows;
    }

//    //Dao层直接实现了，而且写死了，不好
//    public int oldDelBook(String bookName) {
//        String sql = "delete from lib_book where book_name = ?";
//        try {
//            //预编译sql语句
//            PreparedStatement preparedStatement = DataBaseUtils.connection.prepareStatement(sql);
//            //填充占位符
//            preparedStatement.setString(1, bookName);
//            //执行并返回
//            return preparedStatement.executeUpdate();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }

    //通用更新语句，删除方法
    public int delBook(String bookName) {
        String sql = "delete from lib_book where book_name = ?";
        ArrayList arrayList = new ArrayList();
        arrayList.add(new PreparedParamDomain(bookName, String.class));
        return DataBaseUtils.commonUpdate(sql, arrayList);
    }

    public List<Book> listBookByBorrowCount() {
        String sql ="select id,book_name as bookName,author,press,price,stock,borrow_count as borrowCount from lib_book order by borrow_count desc;";
        return DataBaseUtils.commonQuery(Book.class,sql,null);
    }
}
