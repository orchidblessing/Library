package com.orchidblessing.library.dao;

import com.orchidblessing.library.entity.User;

import com.orchidblessing.library.db.DataBaseUtils;
//夫子的Dao也就起了一个拼接字符串的作用,把内容传到DB里，去掉手动拼的过程，是解决Sql注入的关键
//用于存放sql语句，以及判断逻辑，避免在LibraryServiceImpl里出现sql语句，实现LS只管具体功能，不直接调用DB；避免在DataBaseUtils类里出现业务逻辑，DB只接收变量，执行sql语句，不要写复杂业务
public class UserDao {

    public User getUser(String username) {
        User user = DataBaseUtils.queryUser(username);//不能把sql语句提到这里，不然又要传sql语句，又要传要填的变量，夫子是在这里把两者拼好了再传的
        return user;
    }
}
