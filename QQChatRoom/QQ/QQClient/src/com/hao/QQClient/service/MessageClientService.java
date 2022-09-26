package com.hao.QQClient.service;

import com.hao.QQcommon.Message;
import com.hao.QQcommon.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * 该对象提供和消息相关的方法
 */
public class MessageClientService {
    /**
     *功能：私聊发送消息
     * @param content 内容
     * @param senderId 发送者id
     * @param getterId 接收者id
     */
    public void sendMessageToOne(String content,String senderId,String getterId){
        //构建Message
        Message message=new Message();
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setContent(content);
        message.setMesType(MessageType.MESSAGE_COMM_MES);
        //构建时间
        message.setSendTime(new Date().toString());
        System.out.println(senderId +" 对 "+getterId+" 说: "+content);
        //发送给服务端
        try {
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 功能：群发消息方法
     * @param content 内容
     * @param senderId 发送者
     */
    public void sendMessageToAll(String content,String senderId){
        //构建Message
        Message message=new Message();
        message.setSender(senderId);
        message.setContent(content);
        message.setMesType(MessageType.MESSAGE_TOALL_MES);
        //构建时间
        message.setSendTime(new Date().toString());
        System.out.println(senderId +" 对 "+"大家"+" 说: "+content);
        //发送给服务端
        try {
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
