package com.orchidblessing.library.test;

import com.orchidblessing.library.db.DataBaseUtils;
import com.orchidblessing.library.entity.Book;
import com.orchidblessing.library.entity.User;
import com.orchidblessing.library.service.LibraryService;
import com.orchidblessing.library.service.LibraryServiceImpl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        //'Java从入门到精通','道格李','人民教育出版社',30.95,2
        LibraryService libraryService = new LibraryServiceImpl();

//        libraryService.addBook("Java1", "道格李", "人民教育出版社", new BigDecimal(30.95), 2);//1.添加图书ok
//
//        libraryService.addUser("李四1",14,"lisi@qq.com");//2.添加用户ok
//
//        libraryService.borrowBook("李四","Go高级");//3.借书ok
//        libraryService.returnBook("李四", "Go高级");//4.还书ok

        //统一查询测试：查所有用户
//        List<User> users = DataBaseUtils.commonQuery(User.class, "select id, username, age, email from lib_user");
//        users.forEach(System.out::println);
        //查所有书
//        List<Book> books = DataBaseUtils.commonQuery(Book.class, "select id, book_name as bookName, author, press, price, stock,borrow_count as borrowCount from lib_book");
//        books.forEach(book -> System.out.println(book));

//        libraryService.delBook("Java从入门到精通(通用版5)");//删除书

//        libraryService.queryBook("Go高级");//5.根据书名查书ok preparedStatement的问号占位符不能用引号引着
//        libraryService.queryBook(null);//5.根据书名查书
//        libraryService.queryBookByAuthor("道格李");//6.根据作者搜书
//        libraryService.queryBookByAuthor(null);//6.根据作者搜书
//        libraryService.queryMyBorrowBook("李四");//7.根据用户名查询正在借阅的图书
//        libraryService.queryBookBorrowUser("Go入门");//8.根据书名查询正在借阅的用户
//        libraryService.queryBookBorrowCount();//9.统计每本书借阅次数，并按倒叙排序。

//        try {
//            System.out.println(URLEncoder.encode("汉字转url码","utf-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//


    }
}