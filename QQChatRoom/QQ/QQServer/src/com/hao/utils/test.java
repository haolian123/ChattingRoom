package com.hao.utils;

import java.sql.Connection;

public class test {
    public static void main(String[] args) throws  Exception{
        int i=Utility.readInt();
        System.out.println(i);
        Connection connection = JDBCDruidUtility.getConnection();
        System.out.println(connection);
    }

}
