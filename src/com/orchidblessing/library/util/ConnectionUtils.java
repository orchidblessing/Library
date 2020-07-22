package com.orchidblessing.library.util;

import java.sql.Connection;

public class ConnectionUtils {
    //ThreadLocal集合，用于当前线程对象的透传
    public static final ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal();
}
