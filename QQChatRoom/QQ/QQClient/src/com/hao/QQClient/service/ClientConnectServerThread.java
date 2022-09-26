package com.hao.QQClient.service;

import com.hao.QQcommon.Message;
import com.hao.QQcommon.MessageType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientConnectServerThread extends Thread{
    //该线程需要持有Socket
    private Socket socket;

    //构造器可以接收一个Socket对象
    public ClientConnectServerThread(Socket socket){
        this.socket=socket;
    }

    //为了更方便得到Socket


    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run(){
        //因为线程需要在后台和服务器通信，因此做成一个无限循环
        while(true){

            try {
                System.out.println("客户端线程 等待读取从服务器端发送的消息");
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                //如果服务器没有发送Message对象，线程会阻塞在这里
                Message mesage=(Message) objectInputStream.readObject();
                //后续需要使用Message
                //判断该message的类型，然后做相应的业务处理
                //如果读取到达是服务端返回的在线用户列表
                if(mesage.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)){
                    //取出在线列表信息，并显示
                    String[] onlineUsers = mesage.getContent().split(" ");
                    System.out.println("\n当前在线用户列表如下：");
                    for(int i=0;i<onlineUsers.length;i++){
                        System.out.println("用户:"+onlineUsers[i]);
                    }
                }else if(mesage.getMesType().equals(MessageType.MESSAGE_COMM_MES)){
                    //普通的聊天消息
                    //把从服务器端转发的消息显示到控制台
                    System.out.println("\n"+mesage.getSender()+" 对 "+mesage.getGetter()+" 说：" +
                            mesage.getContent()+"   "+mesage.getSendTime());
                }else if(mesage.getMesType().equals(MessageType.MESSAGE_TOALL_MES)){
                    //打印
                    System.out.println(mesage.getSender()+"对大家说"+mesage.getContent());

                }else if(mesage.getMesType().equals(MessageType.MESSAGE_FILE_MES)){
                    //判断是文件消息
                    System.out.println("\n"+mesage.getSender()+"给你发送了文件\""+mesage.getSrc()
                    +"\"到我的电脑目录"+mesage.getDest());

                    //取出message的文件字节数组，通过文件输出流写出到磁盘
                    FileOutputStream fileOutputStream = new FileOutputStream((mesage.getDest()));
                    fileOutputStream.write(mesage.getFileBytes());
                    fileOutputStream.close();
                    System.out.println("文件已保存到路径:"+mesage.getDest());
                }
                else{
                    System.out.println("是其它类型的message，暂时不处理...");
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }
}
