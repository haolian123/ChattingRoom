package com.hao.QQClient.service;

import com.hao.QQcommon.Message;
import com.hao.QQcommon.MessageType;

import java.io.*;

/**
 * @author hao
 * @version 1.0
 * 该类完成文件的传输
 */
public class FileClientService {
    /**
     * 发送文件给对方
     * @param src 源文件
     * @param dest 把文件传输到对方的哪个目录
     * @param senderId 发送方
     * @param getterId 接收方
     */
    public void sendFileToOne(String src,String dest,String senderId,String getterId){
        //读取src文件->封装到message中
        Message message=new Message();
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setSrc(src);
        message.setDest(dest);


        //读文件
        FileInputStream fileInputStream=null;
        byte[] fileBytes=new byte[(int) new File(src).length()];
        try {
            fileInputStream=new FileInputStream(src);
            fileInputStream.read(fileBytes);//将src文件读入到fileBytes中
            //将文件对应的字节数组设置到message
            message.setFileBytes(fileBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            //关闭
             if(fileInputStream!=null){
                 try {
                     fileInputStream.close();
                 } catch (IOException e) {
                     throw new RuntimeException(e);
                 }
             }
        }


        //发送
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //提示信息
        System.out.println("\n"+senderId+"给"+getterId+"发送文件:\""+
                src+"\""+"到电脑目录\""+dest+"\"" );


    }
}
