package com.orchidblessing.library.dao;

import com.orchidblessing.library.db.DataBaseUtils;
import com.orchidblessing.library.domain.PreparedParamDomain;
import com.orchidblessing.library.entity.Book;
import com.orchidblessing.library.entity.BorrowRecord;
import com.orchidblessing.library.entity.User;

import java.util.ArrayList;
import java.util.List;

public class BorrowRecordDao {
    public int add(String username, String bookName) {
        int rows = 0;
        rows = DataBaseUtils.insertBorrowRecord(username,bookName);
        return rows;
    }

    public int updateStatus(int userId, int bookId) {
        int rows = 0;
        rows = DataBaseUtils.updateBorrowRecord(userId, bookId);
        return rows;
    }

    public BorrowRecord queryRecord(String username, String bookName) {
        BorrowRecord borrowRecord = null;
        borrowRecord = DataBaseUtils.queryRecord(DataBaseUtils.queryUser(username).getId(), DataBaseUtils.queryBook(bookName).getId());
        return borrowRecord;
    }

    public List<Book> listUserBorrowBook(int userId, int status) {
        //通过用户Id和状态码，两表联查，查到书籍并返回
        String sql = "select lb.id,lb.book_name as bookName,lb.author,lb.press,lb.price,lb.stock,lb.borrow_count as borrowCount from lib_book lb join lib_borrow_record lbr on lb.id=lbr.book_id where user_id=? and `status`=?";
        //为了防止sql注入，用到了preparedParamDomain来传数据和表示其类型的字节码
        ArrayList arrayList = new ArrayList();
        arrayList.add(new PreparedParamDomain(userId,Integer.class));
        arrayList.add(new PreparedParamDomain(status,Integer.class));

        return DataBaseUtils.commonQuery(Book.class,sql,arrayList);
    }

    public List<User> listBookBorrowUser(int bookId) {
        String sql = "select lu.id,lu.username,lu.age,lu.email from lib_user lu join (select distinct user_id from lib_borrow_record where book_id=?) as lbr on lu.id=lbr.user_id";
        //为了防止sql注入，用到了preparedParamDomain来传数据和表示其类型的字节码
        ArrayList arrayList = new ArrayList();
        arrayList.add(new PreparedParamDomain(bookId,Integer.class));

        return DataBaseUtils.commonQuery(User.class,sql,arrayList);
    }

}
