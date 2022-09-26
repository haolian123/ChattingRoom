package com.hao.dao;

import com.hao.utils.JDBCDruidUtility;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.util.List;

public class BasicDAO<T> {//泛型指定具体类型
    private QueryRunner qr=new QueryRunner();

    //开发通用的DML方法，针对任意的表

    public int update(String sql,Object...param){
        int update=0;
        Connection connection=null;
        try {
            connection = JDBCDruidUtility.getConnection();
            update= qr.update(connection, sql, param);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            JDBCDruidUtility.close(null,null,connection);
        }
        return update;

    }

    /**
     *
     * @param sql  sql语句，可以有 ?
     * @param clazz  传入一个类的Class对象，比如Actor.class
     * @param parameters  传入?的具体的值,可以是多个
     * @return 根据User.class 返回对应的ArrayList集合
     */
    //返回多个对象,针对任意表
    public List<T> queryMulti(String sql, Class<T>clazz, Object...parameters){
        Connection connection=null;
        try {
            connection = JDBCDruidUtility.getConnection();
            return qr.query(connection,sql,new BeanListHandler<T>(clazz),parameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            JDBCDruidUtility.close(null,null,connection);
        }

    }

    //查询单行结果的通用方法
    public  T querySingle(String sql,Class<T>clazz,Object ...parameters){
        Connection connection=null;
        try {
            connection = JDBCDruidUtility.getConnection();
            return qr.query(connection,sql,new BeanHandler<T>(clazz),parameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            JDBCDruidUtility.close(null,null,connection);
        }

    }

    //查询单行单列的方法，即返回单值
    public Object queryScalar(String sql,Object...parameters){
        Connection connection=null;
        try {
            connection = JDBCDruidUtility.getConnection();
            return qr.query(connection,sql,new ScalarHandler(),parameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            JDBCDruidUtility.close(null,null,connection);
        }
    }

}