package com.hao.QQServer.service;

import com.hao.QQcommon.Message;
import com.hao.QQcommon.MessageType;
import com.hao.utils.Utility;
import com.sun.xml.internal.ws.api.config.management.policy.ManagedClientAssertion;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 推送消息到所有用户的线程
 */
public class SendNewsToAll implements  Runnable {
    private Scanner scanner=new Scanner(System.in);
    @Override
    public void run() {
        //为了多次推送，用while
        while(true) {
            System.out.println("请输入服务器要推送的新闻/消息[输入exit表示退出推送服务]");
            String news = Utility.readString(100);
            if("exit".equals(news)){
                break;
            }
            //构建一个消息，群发消息
            Message message = new Message();
            message.setSender("服务器");
            message.setContent(news);
            message.setSendTime(new Date().toString());
            message.setMesType(MessageType.MESSAGE_TOALL_MES);


            //遍历当前所有的通信线程，得到socket，并发送message对象

            HashMap<String, ServerConnectClientThread> hm = ManageServerThreads.getHm();
            Iterator<String> iterator = hm.keySet().iterator();
            while (iterator.hasNext()) {
                String onLineUserId = iterator.next().toString();
                ServerConnectClientThread serverConnectClientThread = hm.get(onLineUserId);
                try {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    objectOutputStream.writeObject(message);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("\n服务器推送消息给所有人:" + news);
        }

    }
}
