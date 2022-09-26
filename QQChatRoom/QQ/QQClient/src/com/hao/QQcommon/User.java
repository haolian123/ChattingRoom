package com.hao.QQcommon;

import java.io.Serializable;

/**
 * @author hao
 * @version  1.0
 * 表示一个用户信息
 */
public class User implements Serializable {
    public boolean isIsnew() {
        return isnew;
    }

    public void setIsnew(boolean isnew) {
        this.isnew = isnew;
    }

    //增强兼容性
    private static final long serialVersionUID=1L;
    private String userId;//用户ID
    private String passwd;//用户密码

    private boolean   isnew;//是否注册



//    public User(String userId, String passwd) {
//        this.userId = userId;
//        this.passwd = passwd;
//    }

    public User(String userId, String passwd, boolean isnew) {
        this.userId = userId;
        this.passwd = passwd;
        this.isnew = isnew;
    }

    public User() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
