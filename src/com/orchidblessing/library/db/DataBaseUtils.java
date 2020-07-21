package com.orchidblessing.library.db;

import com.orchidblessing.library.domain.PreparedParamDomain;
import com.orchidblessing.library.entity.Book;
import com.orchidblessing.library.entity.BorrowRecord;
import com.orchidblessing.library.entity.User;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseUtils {
    //1.设置四个基本信息
    private static final String url = "jdbc:mysql://localhost:3306/library";
    private static final String user = "root";
    private static final String password = "";
    private static final String driverClass = "com.mysql.jdbc.Driver";
    public static Connection connection;

    static {
        try {
            //加载驱动
            Class.forName(driverClass);
            //2.获取连接：java程序到数据库服务器之间的TCP/IP连接通道
            connection = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: 通用更新语句
     * @Author: Edmond Wang
     * @Date: 2020/7/11 11:21
     */
    public static int addBook(String bookName, String author, String press, BigDecimal price, int stock) {
        int rows = 0;
        PreparedStatement ps = null;
        String sql = "insert into lib_book(book_name,author,press,price,stock) value(?,?,?,?,?);";
        try {
            //3.预编译sql语句
            ps = connection.prepareStatement(sql);
            //4.填充占位符
            ps.setObject(1,bookName);
            ps.setObject(2,author);
            ps.setObject(3,press);
            ps.setObject(4,price);
            ps.setObject(5,stock);
            //5.执行
            rows = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("影响行数：" + rows);

        return rows;
    }

    public static int addUser(String userName, Integer age, String email) {
        int rows = 0;
        PreparedStatement ps = null;
        String sql = "insert into lib_user(username,age,email) value(?,?,?);";
        try {
            //3.预编译sql语句
            ps = connection.prepareStatement(sql);
            //4.填充占位符
            ps.setObject(1,userName);
            ps.setObject(2,age);
            ps.setObject(3,email);

            //5.执行
            rows = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("影响行数：" + rows);

        return rows;
    }

    //传进来sql和键值对集合
    public static int commonUpdateSql(String sql, List<PreparedParamDomain> params) {
        int rows = 0;
        boolean insertFlag = false;
        try {
            //预编译sql语句,并获取下一个id
            PreparedStatement preparedStatement = null;
            if("insert".equalsIgnoreCase(sql.trim().substring(0,6))) {
                //获取自增主键的方式
                preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                insertFlag = true;
            } else {
                preparedStatement = connection.prepareStatement(sql);
            }

            for(int index = 0; index < params.size(); index++) {
                //获取params里面的每一个param
                PreparedParamDomain param = params.get(index);
                if(param.getClazz() == String.class) {
                    preparedStatement.setString(index + 1, (String) param.getValue());
                }
                if(param.getClazz() == Integer.class) {
                    preparedStatement.setInt(index + 1, (Integer) param.getValue());
                }
                if(param.getClazz() == BigDecimal.class) {
                    preparedStatement.setBigDecimal(index + 1, (BigDecimal) param.getValue());
                }
            }
            rows = preparedStatement.executeUpdate();
            if(insertFlag) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if(generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public static int reduceStock(String bookName) {
        int reduceBookRows = 0;
        PreparedStatement reduceStockPs = null;
        String reduceStockSql = "update lib_book set stock=stock-1,borrow_count=borrow_count+1 where book_name=?";
        try {
            //3.预编译sql语句
            reduceStockPs = connection.prepareStatement(reduceStockSql);
            //4.填充占位符
            reduceStockPs.setObject(1,bookName);
            //5.执行
            reduceBookRows = reduceStockPs.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("更新图书状态：" + reduceBookRows + "条");
        return reduceBookRows;
    }

    public static int insertBorrowRecord(String username, String bookName) {
        //初始化影响行数和preparedStatement
        int insertBorrowRecordRows = 0;
        PreparedStatement insertBorrowRecordPs = null;
        String insertBorrowRecordSql = "insert into lib_borrow_record(user_id,book_id,`status`) values(?,?,1)";
        try {
            //3.预编译sql语句
            insertBorrowRecordPs = connection.prepareStatement(insertBorrowRecordSql);
            //4.填充占位符
            //要根据username和bookName找到userId和bookId
            insertBorrowRecordPs.setObject(1,queryUser(username).getId());//填userId,为了保持统一，就还是从新获取，而不是直接传入
            insertBorrowRecordPs.setObject(2,queryBook(bookName).getId());//同上
            //5.执行
            insertBorrowRecordRows = insertBorrowRecordPs.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("更新借阅记录：" + insertBorrowRecordRows + "条");
        return insertBorrowRecordRows;
    }

    public static int updateBorrowRecord(int userId, int bookId) {
        //初始化影响行数和preparedStatement
        int updateBorrowRecordRows = 0;
        PreparedStatement updateBorrowRecordPs = null;
        String sql = "update lib_borrow_record set `status` = 0 where user_id = ? and book_id = ? and status = 1;";
        try {
            //3.预编译sql语句
            updateBorrowRecordPs = connection.prepareStatement(sql);
            //4.填充占位符
            updateBorrowRecordPs.setObject(1, userId);
            updateBorrowRecordPs.setObject(2, bookId);
            //5.执行
            updateBorrowRecordRows = updateBorrowRecordPs.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return updateBorrowRecordRows;
    }

    //还书的时候用
    public static int increaseStock(String bookName) {
        //初始化影响行数和preparedStatement
        int rows = 0;
        PreparedStatement increaseStockPs = null;
        //更新书库存的sql语句
        String sql = "update lib_book set stock=stock+1 where book_name=?";

        try {
            //预编译sql语句
            increaseStockPs = connection.prepareStatement(sql);
            //填充占位符
            increaseStockPs.setObject(1, bookName);
            //执行
            rows = increaseStockPs.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }


    //通过username查询User对象
    public static User queryUser(String username) {
        User user = null;
        String sql = "select id,username,age,email from lib_user where username = ?";

        try {
            //预编译sql语句
            PreparedStatement ps = connection.prepareStatement(sql);
            //填充sql语句
            ps.setObject(1,username);
            //执行sql语句并返回结果集
            ResultSet resultSet = ps.executeQuery();
            //用while处理结果集
            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                username = resultSet.getString("username");
                int age = resultSet.getInt("age");
                String email = resultSet.getString("email");
                user = new User();
                user.setId(id);
                user.setUsername(username);
                user.setAge(age);
                user.setEmail(email);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    //通过bookName查询book对象，用手动的方法，给对象的每一个成员变量赋值，因为事先已经知道在查book
    public static Book queryBook(String bookName) {
        Book book = null;
        String sql = "select id,book_name as bookName,author,press,price,stock,borrow_count as borrowCount from lib_book where book_name = ?";

        try {
            //预编译sql语句
            PreparedStatement ps = connection.prepareStatement(sql);
            //填充sql语句
            ps.setObject(1,bookName);
            //执行sql语句并返回结果集
            ResultSet resultSet = ps.executeQuery();
            //用while处理结果集
            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                bookName = resultSet.getString("bookName");
                String author = resultSet.getString("author");
                String press = resultSet.getString("press");
                BigDecimal price = resultSet.getBigDecimal("price");
                int stock = resultSet.getInt("stock");
                int borrowCount = resultSet.getInt("borrowCount");

                book = new Book();
                book.setId(id);
                book.setBookName(bookName);
                book.setAuthor(author);
                book.setPress(press);
                book.setPrice(price);
                book.setStock(stock);
                book.setBorrowCount(borrowCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return book;
    }

    //通过userId和bookId查询借阅记录是不是1，用count也可以，我用的方法是返回查询，如果返回的结果集有东西，说明有记录
    public static BorrowRecord queryRecord(int userId, int bookId) {
        //初始化影响行数和preparedStatement
        BorrowRecord borrowRecord = null;
        PreparedStatement queryRecordPs = null;
        String sql = "select id,user_id as userId,book_id as bookId,status from lib_borrow_record where user_id = ? and book_id = ? and status = 1";
        try {
            //预编译sql语句
            queryRecordPs = connection.prepareStatement(sql);
            //填充sql语句
            queryRecordPs.setObject(1,userId);
            queryRecordPs.setObject(2,bookId);
            //执行
            ResultSet resultSet = queryRecordPs.executeQuery();
            //用while处理结果集
            while(resultSet.next()) {
                int id = resultSet.getInt("id");
                userId = resultSet.getInt("userId");
                bookId = resultSet.getInt("bookId");
                int status = resultSet.getInt("status");

                borrowRecord = new BorrowRecord();
                borrowRecord.setId(id);
                borrowRecord.setUserId(userId);
                borrowRecord.setBookId(bookId);
                borrowRecord.setStatus(status);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return borrowRecord;
    }

    //通用的查询方法，用泛型解决，传过来什么类型，返回什么对象
    /**
     * @Description: 统一查询,不安全版本
     * @Param: [clazz, sql]
     * @Return: T
     * @Author: Edmond Wang
     * @Date: 2020/7/16 15:26
     */
    //通用的东西会用到反射,用反射的方法，给对象的每一个成员变量赋值
    public static <T> List<T> commonQuery(Class<T> clazz, String sql) {
        List<T> tList = new ArrayList();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            //获取类的全部成员变量名字，根据变量的名字，方便从结果集字段里获取数据
            Field[] fields = clazz.getDeclaredFields();

            //循环结果集的每一行
            while (resultSet.next()) {
                //一行数据就是一个对象
                T t = clazz.newInstance();
                //可以先把t加进集合再慢慢的赋值，因为操作的地址都是堆里的同一个对象
                tList.add(t);
                //循环每一列
                for(Field field : fields) {
                    Object colValue = null;
//                    if(field.getType() == Integer.class) {
//                        colValue = resultSet.getInt(field.getName());
//                    }
//                    if(field.getType() == String.class) {
//                        colValue = resultSet.getString(field.getName());
//                    }
                    //根据列名称，获取这一行中，指定列名称的数据（但是不知道数据类型是什么）
                    //这个colValue是具体的数据
                    colValue = resultSet.getObject(field.getName());

                    //查找当前字段set方法，为对象的成员变量赋值
                    String numberName = field.getName();//先找到成员变量名，去拼方法名
                    String methodName = "set" + numberName.substring(0, 1).toUpperCase() + numberName.substring(1);
                    //这里获取类的方法，需要知道方法名和入参类型
                    Method method = clazz.getMethod(methodName, field.getType());//方法名拼出来的，入参类型就是列的字段类型
                    //获得方法之后，执行方法，告诉方法操作哪个对象，以及入参的值
                    method.invoke(t, colValue);
                }
            }
            return tList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
//        String s = commonQuery(String.class, "");
//        Book book = commonQuery(Book.class, "");
//        Class<User> userClazz = User.class;
//        //获取字节码文件的报名加类名
//        String name = userClazz.getName();
//        System.out.println(name);//com.orchidblessing.library.entity.User
//        //获取字节码文件
//        Field[] declaredFields = userClazz.getDeclaredFields();
//        for(Field field : declaredFields) {
//            System.out.println(field.getName());
//        }
//        System.out.println("---------");
//        //获取字节码文件类名
//        Method[] declaredMethods = userClazz.getDeclaredMethods();
//        for(Method method : declaredMethods) {
//            System.out.println(method.getName());
//        }
//        System.out.println("----------");
//        //获取字节码文件包名
//        Package aPackage = userClazz.getPackage();
//        System.out.println(aPackage.getName());
//        System.out.println("----------");
//
//        //通过反射，任意可以执行任意对象的方法
//        //通过字节码创建对象
//        User user = userClazz.newInstance();
//        System.out.println("username=" + user.getUsername());
//        //通过字节码文件获取方法并执行,方法名和入参类型，因为这两者就是一个方法的签名
//        Method setUsernameMethod = userClazz.getMethod("setUsername", String.class);
//        System.out.println("-----------");
//        //调用从字节码文件得到的方法,但是方法是对象的方法，需要告诉方法操纵哪个对象，并告诉方法入参内容
//        setUsernameMethod.invoke(user, "jack");
//        System.out.println("username=" + user.getUsername());
//
//        User u2 = userClazz.newInstance();
//        User u3 = userClazz.newInstance();
//
//        System.out.println(u2 == u3);//false
//        //substring可以只输入开头
//        String name = "abc123";
//        System.out.println(name.substring(1));

    }
}
