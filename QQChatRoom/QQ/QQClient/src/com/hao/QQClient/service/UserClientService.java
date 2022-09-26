package com.hao.QQClient.service;

import com.hao.QQcommon.Message;
import com.hao.QQcommon.MessageType;
import com.hao.QQcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * 该类完成用户登录验证和用户注册等功能
 */
public class UserClientService {
    //因为我们可能在其它地方需要使用user信息，以便其它地方使用get,set
    private User u = new User();
    //因为Socket在其它地方也可能使用，因此要做成属性
    private Socket socket;

    //根据userId和pwd到服务器验证该用户是否合法
    public boolean checkUser(String userId, String pwd) {
        boolean bool = false;
        //创建User对象
        u.setUserId(userId);
        u.setPasswd(pwd);
        u.setIsnew(false);
        //连接到服务端，发送u对象
        try {
            socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
            //得到ObjectOutputStream对象
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(u);//发送User对象


            //读取从服务端回送的Message对象

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message ms = (Message) ois.readObject();
            if (ms.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)) {//登录成功


                //创建一个和服务器端保持通信的线程->创建一个类ClientConnectServerThread
                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);

                //启动客户端的线程
                clientConnectServerThread.start();
                //为了后面客户端的扩展，将线程放入集合管理
                ManageClientConnectServerThread.addClientConnectServerThread(userId, clientConnectServerThread);
                bool = true;


            } else {
                //登录失败,就不能启动和服务器通信的线程，需要关闭Socket
                socket.close();

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return bool;
    }

    //向服务器端请求在线用户列表
    public void onlineFriendList() {

        //发送一个Message,类型就是MESSAGE_GET_ONLINE_FRIEND
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(u.getUserId());
        //发送给服务器
        //应该得到当前线程的Socket对应的ObjectOutputStream对象
        try {
            //从管理线程的集合中通过userId得到该线程，再得到该线程的socket的输出流OutPutStream
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(u.getUserId()).getSocket().getOutputStream());
            //通过ObjectOutputStream发送message到服务端，向服务端要在线用户列表
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    //方法:退出客户端，并给服务端发送一个退出系统的message对象
    public  void logout(){
        Message message=new Message();
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getUserId());//一定要指定我是哪个客户端Id

        //发送message
        try {
            ObjectOutputStream objectOutputStream   = new ObjectOutputStream(ManageClientConnectServerThread.
                    getClientConnectServerThread(u.getUserId()).getSocket().getOutputStream());
            objectOutputStream.writeObject(message);
            System.out.println(u.getUserId()+"退出了系统");
            System.exit(0);//结束进程
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    //确认数据库中是否已经存在Id,是则返回false
    public boolean newUserIdIsExist(String userId,String pwd){
        boolean bool = false;
        //创建User对象
        u.setUserId(userId);
        u.setPasswd(pwd);
        u.setIsnew(true);
        //连接到服务端，发送u对象
        try {
            socket = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
            //得到ObjectOutputStream对象
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(u);//发送User对象


            //读取从服务端回送的Message对象

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message ms = (Message) ois.readObject();
            if (ms.getMesType().equals(MessageType.MESSAGE_REGIST_FAIL)) {//注册失败

                bool = true;


            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return bool;
    }

}
