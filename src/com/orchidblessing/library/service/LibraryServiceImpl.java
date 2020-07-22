package com.orchidblessing.library.service;

import com.orchidblessing.library.dao.BookDao;
import com.orchidblessing.library.dao.BorrowRecordDao;
import com.orchidblessing.library.dao.UserDao;
import com.orchidblessing.library.db.DataBaseUtils;
import com.orchidblessing.library.entity.Book;
import com.orchidblessing.library.entity.BorrowRecord;
import com.orchidblessing.library.entity.User;
import com.orchidblessing.library.util.ConnectionUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class LibraryServiceImpl implements LibraryService{
    public static final int BORROWING = 1;//借阅中状态码
    public static final int BORROWED = 0;//已归还状态码

    //示例化各个Dao工具，用于操作DataBaseUtils
    private UserDao userDao = new UserDao();
    private BookDao bookDao = new BookDao();
    private BorrowRecordDao borrowRecordDao = new BorrowRecordDao();

//    @Override
//    public void oldAddBook(String bookName, String author, String press, BigDecimal price, int stock) {
//        int rows = DataBaseUtils.addBook(bookName, author, press, price, stock);
//        if(1 == rows) {
//            System.out.println("图书添加成功");
//        }
//    }

    //新addBook调用BookDao，使用DataBaseUtils的通用commonUpdate方法修改，降低代码量
    @Override
    public void addBook(String bookName, String author, String press, BigDecimal price, int stock) {
        //每次获取connection的时候，都讲清楚要不要开启事务
        Connection connection = DataBaseUtils.getConnection(true);
        //在service里把connection放进connectionThreadLocal里
        ConnectionUtils.connectionThreadLocal.set(connection);

        Book book = new Book();
        int bookId = 0;
        book.setBookName(bookName);
        book.setAuthor(author);
        book.setPress(press);
        book.setPrice(price);
        book.setStock(stock);
        bookId = bookDao.addBook(book);
        book.setId(bookId);
        if(0 != bookId) {
            //统一在service里输出是否成功的消息
            System.out.println("添加成功");
            //这个book里面被赋予了bookDao返回的bookId
            System.out.println(book);
        }
    }

    //addUser就这样吧，跳过Dao层；（也可以参照addBook,封装成对象给Dao，让Dao传sql语句和参数键值对，使用DataBaseUtils的通用commonUpdate方法修改
    @Override
    public void addUser(String userName, Integer age, String email) {
        //每次获取connection的时候，都讲清楚要不要开启事务
        Connection connection = DataBaseUtils.getConnection(true);
        //在service里把connection放进connectionThreadLocal里
        ConnectionUtils.connectionThreadLocal.set(connection);

        //将输入信息继续下沉到db，用prepareStatement来避免SQL注入问题
        int rows = DataBaseUtils.addUser(userName, age, email);
        if(1 == rows) {
            System.out.println("用户添加成功");
        }
    }

    @Override
    public void borrowBook(String username, String bookName) {

        //每次获取connection的时候，都讲清楚要不要开启事务
        Connection connection = DataBaseUtils.getConnection(false);
        //在service里把connection放进connectionThreadLocal里
        ConnectionUtils.connectionThreadLocal.set(connection);

        //第一步，查询有无该用户和书籍
        User user = userDao.getUser(username);//都在DB写了单独的方法
        Book book = bookDao.getBook(bookName);//都在DB写了单独的方法
        if(null == user) {
            System.out.println("请核对用户是否存在");
            return;
        }
        if(null == book) {
            System.out.println("请核对书籍是否存在");
            return;
        }
        //第二步，查询库存
        if(book.getStock() <= 0) {
            System.out.println("库存不足");
            return;
        }
        //第三步，查询用户借过没有
        BorrowRecord borrowRecord = borrowRecordDao.queryRecord(username, bookName);
        if(null != borrowRecord) {
            System.out.println("用户已借阅");
            return;
        }

        try {
            //第四步，减少库存  这两条要同时成功与失败，用事务
            int reduceRows = bookDao.updateStock4Borrow(bookName);
            //添加借阅记录
            int insertBorrowRecordRows = borrowRecordDao.add(username, bookName);
            //如果都是一行，则借书成功
            if(reduceRows != 1 || insertBorrowRecordRows != 1) {
                System.out.println("借阅失败");
                return;
            }
            //提交事务，将开启事务到commit()之间的所有修改语句 置为生效
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                connection.rollback();
                System.out.println("回滚成功");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            //谁开启，谁关闭
            DataBaseUtils.getConnection(true);
            ConnectionUtils.connectionThreadLocal.remove();//防止内存泄漏
        }
    }

    @Override
    public void returnBook(String username, String bookName) {
        //每次获取connection的时候，都讲清楚要不要开启事务
        Connection connection = DataBaseUtils.getConnection(false);
        //在service里把connection放进connectionThreadLocal里
        ConnectionUtils.connectionThreadLocal.set(connection);

        User user = userDao.getUser(username);
        Book book = bookDao.getBook(bookName);

        //查询有无借书记录，遍历结果集
        //如果有状态为1的书，才能还
        BorrowRecord borrowRecord = borrowRecordDao.queryRecord(username, bookName);
        if(null == borrowRecord) {
            System.out.println("用户未借阅此书");
        } else if(1 != borrowRecord.getStatus()) {
            System.out.println("此书已归还");
            return;
        }


        try {
            //更新借书记录
            int updateStatusRows = borrowRecordDao.updateStatus(user.getId(), book.getId());
            //增加库存
            int increaseRows = bookDao.updateStock4Return(bookName);

            //如果都是一行，则还书成功
            if(updateStatusRows != 1 && increaseRows != 1) {
                System.out.println("还书失败");
            }
            connection.commit();
            System.out.println("还书成功");
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    @Override
    public void delBook(String bookName) {
        //每次获取connection的时候，都讲清楚要不要开启事务
        Connection connection = DataBaseUtils.getConnection(true);
        //在service里把connection放进connectionThreadLocal里
        ConnectionUtils.connectionThreadLocal.set(connection);

        if(null == bookName || "".equals(bookName)) {
            return;
        }
        System.out.println("影响行数" + bookDao.delBook(bookName));
    }

    @Override
    public void queryBook(String bookName) {
        //每次获取connection的时候，都讲清楚要不要开启事务
        Connection connection = DataBaseUtils.getConnection(true);
        //在service里把connection放进connectionThreadLocal里
        ConnectionUtils.connectionThreadLocal.set(connection);

        //校验用户输入
        if(null == bookName || "".equals(bookName)) {
            System.out.println("书名不能为空");
            return;
        }
        Book book = bookDao.getBookByName(bookName);
        System.out.println(book);
    }

    @Override
    public void queryBookByAuthor(String author) {
        //每次获取connection的时候，都讲清楚要不要开启事务
        Connection connection = DataBaseUtils.getConnection(true);
        //在service里把connection放进connectionThreadLocal里
        ConnectionUtils.connectionThreadLocal.set(connection);

        //校验用户输入
        if(null == author || "".equals(author)) {
            System.out.println("作者不能为空");
            return;
        }
        List<Book> books = bookDao.listBookByAuthor(author);
        books.forEach(System.out::println);
    }

    @Override
    public void queryMyBorrowBook(String username) {
        //每次获取connection的时候，都讲清楚要不要开启事务
        Connection connection = DataBaseUtils.getConnection(true);
        //在service里把connection放进connectionThreadLocal里
        ConnectionUtils.connectionThreadLocal.set(connection);

        //校验用户输入
        if(null == username || "".equals(username)) {
            System.out.println("用户名不能为空");
            return;
        }
        //根据用户名查询用户信息
        User user = userDao.getUser(username);
        //根据用户Id到借阅表查询状态为1的记录
        //这里可以用拼接的方法，因为userId和状态码都不是用户直接输入的
        List<Book> books = borrowRecordDao.listUserBorrowBook(user.getId(), BORROWING);
        books.forEach(System.out::println);

    }

    @Override
    public void queryBookBorrowUser(String bookName) {
        //每次获取connection的时候，都讲清楚要不要开启事务
        Connection connection = DataBaseUtils.getConnection(true);
        //在service里把connection放进connectionThreadLocal里
        ConnectionUtils.connectionThreadLocal.set(connection);

        //校验用户输入
        if(null == bookName || "".equals(bookName)) {
            System.out.println("用户名不能为空");
            return;
        }

        //根据书名，查到书本Id
        Book book = bookDao.getBook(bookName);
        if(null == book) {
            System.out.println("查无此书");
            return;
        }
        //根据书本Id查询哪些用户借阅过这本书
        List<User> users = borrowRecordDao.listBookBorrowUser(book.getId());
        users.forEach(System.out::println);
    }

    @Override
    public void queryBookBorrowCount() {
        //每次获取connection的时候，都讲清楚要不要开启事务
        Connection connection = DataBaseUtils.getConnection(true);
        //在service里把connection放进connectionThreadLocal里
        ConnectionUtils.connectionThreadLocal.set(connection);

        List<Book> books = bookDao.listBookByBorrowCount();
        books.forEach(System.out::println);
    }
}
