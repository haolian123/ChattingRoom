package com.hao.QQServer.service;

import com.hao.QQcommon.Message;
import com.hao.QQcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 该类的一个对象和某个客户端保持通信
 */
public class ServerConnectClientThread extends  Thread{
    private Socket socket;
    private String userId;//连接到服务端的用户Id

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run(){//这里线程处于run状态，可以发送/接收消息

       while(true){
           try {
               System.out.println("服务端和客户端"+userId+"保持通信，读取数据...");
               ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
               Message message=(Message) objectInputStream.readObject();
               //根据message类型做相应的业务处理
               if(message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)){
                   //客户端要在线用户列表
                   System.out.println(message.getSender()+" 请求在线用户列表");
                   String onlineUser = ManageServerThreads.getOnlineUser();
                   //返回message
                   //构建一个Message对象，返回给客户端
                   Message message2 = new Message();
                   //设置消息类型
                   message2.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                   message2.setContent(onlineUser);
                   message2.setGetter(message.getSender());
                   //返回给客户端
                   ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                   objectOutputStream.writeObject(message2);

               }else  if(message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)){
                   System.out.println(message.getSender()+" 要退出系统");
                   //将该客户端对应的线程从集合中删除
                    ManageServerThreads.removeServerConnectClientThread(message.getSender());
                   //关闭连接
                    socket.close();
                    //退出while循环(线程)
                   break;

               }else if(message.getMesType().equals(MessageType.MESSAGE_COMM_MES)){
                   //是普通的聊天消息
                   //根据message获取getterid,然后得到对应线程
                   ServerConnectClientThread serverConnectClientThread = ManageServerThreads
                           .getServerConnectClientThread(message.getGetter());
                   //得到对应的socket对象输出流，将message对象转发给指定的客户端
                   ObjectOutputStream objectOutputStream = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    objectOutputStream.writeObject(message);//将A客户的发的消息发给客户B
                   //后续会优化，可以离线留言
               } else if(message.getMesType().equals(MessageType.MESSAGE_TOALL_MES)){
                   //需要遍历管理线程的集合，把所有的线程的socket得到，然后把message转发给所有人
                   HashMap<String,ServerConnectClientThread> hm=ManageServerThreads.getHm();
                   Iterator<String> iterator=hm.keySet().iterator();
                   while(iterator.hasNext()){

                       //取出在线用户的Id
                       String onlineUserId = iterator.next().toString();
                       if(!onlineUserId.equals(message.getSender())){
                           //排除群发给自己
                           //进行转发message
                           ObjectOutputStream objectOutputStream = new ObjectOutputStream(hm.get(onlineUserId).getSocket().getOutputStream());
                           objectOutputStream.writeObject(message);



                       }

                   }


               }else if(message.getMesType().equals(MessageType.MESSAGE_FILE_MES)){
                   //根据getterid 获取对应的线程，将message对象转发
                   ObjectOutputStream objectOutputStream = new ObjectOutputStream(ManageServerThreads.getServerConnectClientThread(message.getGetter()).getSocket().getOutputStream());
                   //转发
                   objectOutputStream.writeObject(message);


               }
               else{
                   System.out.println("其它类型的message,暂时不处理");
               }
           } catch (Exception e) {
               throw new RuntimeException(e);
           }

       }
    }
}
