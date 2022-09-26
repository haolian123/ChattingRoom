package com.hao.QQcommon;

import java.io.Serializable;

/**
 * @author hao
 * @version  1.0
 * 表示一个用户信息
 */
public class User implements Serializable {
    //增强兼容性
    private static final long serialVersionUID=1L;
    private String userId;//用户ID
    private String passwd;//用户密码

    private boolean isnew;

    public boolean isnew() {
        return isnew;
    }

    public void setNew(boolean aNew) {
        isnew = aNew;
    }

//    public User(String userId, String passwd) {
//        this.userId = userId;
//        this.passwd = passwd;
//    }


    public User(String userId, String passwd, boolean isNew) {
        this.userId = userId;
        this.passwd = passwd;
        this.isnew = isNew;
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
