package com.hao.QQServer.service;

import com.hao.QQcommon.Message;
import com.hao.QQcommon.MessageType;
import com.hao.QQcommon.User;
import com.hao.dao.UserDAO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

/**
 * 这是服务端，在监听9999，等待客户端的连接，并保持通信
 */
public class QQServer {



    private ServerSocket serverSocket=null;
    //添加UserDAO对象
    private static UserDAO userDAO=new UserDAO();


//----------------此处已用数据库优化----------------------------
    //创建一个集合，存放多个用户，如果这些用户登录，就认为是合法的
    //后续用数据库优化
//    private static HashMap<String,User>validUsers=new HashMap<>();

//    static{
//        List<User> users = userDAO.queryMulti("select userId,passwd  from UserInfo", User.class);
//        for (User user:
//             users) {
//            validUsers.put(user.getUserId(),new User(user.getUserId(), user.getPasswd()));
//        }
//    }

 //---------------------------------

    //验证用户是否有效的方法
    private boolean checkUser(String userId,String passwd){
//        User user = validUsers.get(userId);
        //改为去数据库中查询有无该User
        User user=userDAO.querySingle("select * from UserInfo where userId=? and passwd=?",User.class,userId,passwd);
        if(user==null){
            //说明userId没有存在
            return false;
        }
        if(!user.getPasswd().equals(passwd)){
            //说明密码错误
            return false;
        }
        return true;
    }
    public QQServer(){
        //端口可以写在配置文件中

        try {
            System.out.println("服务器在9999端口监听");
            new Thread(new SendNewsToAll()).start();
            serverSocket =new ServerSocket(9999);

            while(true){//监听是循环的，当和某个客户端建立连接后，会继续监听
                Socket socket=serverSocket.accept();
                //得到socket关联的对象输入流
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                //得到socket关联的对象输出流
                ObjectOutputStream objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
                 User u = (User) objectInputStream.readObject();
                /**
                 * 以下后续会完善
                 */
            //创建一个Message对象，准备回复客户端
                Message message=new Message();
            //验证

                
                //登录
                if(!u.isnew()) {
                    if(checkUser(u.getUserId(),u.getPasswd())){
                    //登录通过
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    //将message对象回复给客户端
                    objectOutputStream.writeObject(message);
                    //创建一个线程，和客户端保持通信，该线程需要持有socket对象
                    ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, u.getUserId());
                    //启动
                    serverConnectClientThread.start();
                    //把该线程对象放入一个集合中,进行管理
                    ManageServerThreads.addServerThread(u.getUserId(),serverConnectClientThread);


                }else {//登录失败
                    System.out.println("用户"+u.getUserId()+"登录失败");
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    objectOutputStream.writeObject(message);
                    //关闭socket
                    socket.close();
                }}else{
                    //注册功能
                    User tmpuser=userDAO.querySingle("select * from UserInfo where userId=?",User.class,u.getUserId());
                    if(tmpuser==null){
                        //说明该用户还未存在
                        userDAO.update("insert into UserInfo values(?,?)",u.getUserId(),u.getPasswd());//存入数据库
                        message.setMesType(MessageType.MESSAGE_REGIST_SUCCEED);
                        objectOutputStream.writeObject(message);
                        socket.close();
                    }else{
                        System.out.println("用户"+u.getUserId()+"已存在");
                        message.setMesType(MessageType.MESSAGE_REGIST_FAIL);
                        objectOutputStream.writeObject(message);
                        socket.close();
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
//            如果服务端退出while，说明服务器端不再监听，因此关闭ServerSocket
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }
}
