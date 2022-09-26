 package com.hao.utils;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JDBCDruidUtility {


    private static DataSource ds;

    //静态代码块完成初始化
    static {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("QQ/QQServer/src/druid.properties"));
//            properties.load(new FileInputStream(System.getProperty("user.dir") + "/src/druid.properties"));
            ds= DruidDataSourceFactory.createDataSource(properties);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    //编写getConnection方法
    public static Connection getConnection()throws  Exception{
        return ds.getConnection();
    }

    //关闭连接,只是把连接放回连接池
    public  static  void close(ResultSet resultSet, Statement statement,Connection connection){
        try {
            if(resultSet!=null)
                resultSet.close();
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
