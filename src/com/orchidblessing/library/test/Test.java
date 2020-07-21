package com.orchidblessing.library.test;

import com.orchidblessing.library.db.DataBaseUtils;
import com.orchidblessing.library.entity.Book;
import com.orchidblessing.library.entity.User;
import com.orchidblessing.library.service.LibraryService;
import com.orchidblessing.library.service.LibraryServiceImpl;
import java.math.BigDecimal;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        //'Java从入门到精通','道格李','人民教育出版社',30.95,2
        LibraryService libraryService = new LibraryServiceImpl();

//        libraryService.addBook("Java从入门到精通(通用版6)", "道格李", "人民教育出版社", new BigDecimal(30.95), 2);//1.添加图书

//        libraryService.addUser("李四",14,"lisi@qq.com");//2.添加用户

//        libraryService.borrowBook("李四","Go入门");//3.借书
//        libraryService.returnBook("张三", "Go高级");//4.还书

        //统一查询测试：查所有用户
//        List<User> users = DataBaseUtils.commonQuery(User.class, "select id, username, age, email from lib_user");
//        users.forEach(System.out::println);
        //查所有书
//        List<Book> books = DataBaseUtils.commonQuery(Book.class, "select id, book_name as bookName, author, press, price, stock,borrow_count as borrowCount from lib_book");
//        books.forEach(book -> System.out.println(book));

//        libraryService.delBook("Go入门(2020)");//删除书

//        libraryService.queryBook("");//5.根据书名查书
//        libraryService.queryBook(null);//5.根据书名查书
//        libraryService.queryBookByAuthor("");//6.根据作者搜书
//        libraryService.queryBookByAuthor(null);//6.根据作者搜书
//        libraryService.queryMyBorrowBook("张三");//7.根据用户名查询正在借阅的图书
//        libraryService.queryBookBorrowUser("Go入门");//8.根据书名查询正在借阅的用户
//        libraryService.queryBookBorrowCount();//9.统计每本书借阅次数，并按倒叙排序。


    }
}