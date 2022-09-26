package com.hao.QQClient.view;

import com.hao.QQClient.service.FileClientService;
import com.hao.QQClient.service.MessageClientService;
import com.hao.QQClient.service.UserClientService;
import com.hao.QQClient.utils.Utility;

import javax.rmi.CORBA.Util;

/**
 * @author hao
 * @version 1.0
 * 客户端的菜单界面
 */
public class QQView {


    private boolean loop=true;//控制是否显示菜单
    private String key="";//用来接收用户的键盘输入

    private UserClientService userClientService=new UserClientService();//对象是用于登录服务/注册用户
    private MessageClientService messageClientService=new MessageClientService();//对象用户用于私聊
    private FileClientService fileClientService=new FileClientService();//该对象用于传输文件
    public static void main(String[] args) {
        new QQView().mainMenuFirst();
        System.out.println("退出系统");
    }

    //显示主菜单

    /**
     * 一级菜单
     */
    private void mainMenuFirst(){
        while(loop){
            System.out.println("========欢迎登录网络通信系统========");
            System.out.println("\t\t 1登录系统");
            System.out.println("\t\t 2注册账号");
            System.out.println("\t\t 9退出系统");
            System.out.print("请输入你的选择:");
            key= Utility.readString(1);
            //根据用户的输入，来处理不同的逻辑
            switch(key){
                case "1":
                    System.out.print("请输入用户号:");
                    String userId=Utility.readString(50);
                    System.out.print("请输入密 码:");
                    String passWord=Utility.readString(50);

                    //此处有很多代码，编写一个类UserClientSetvice[用户登录\注册]
                    /**-------------------------------------
                     *
                     * 后续需要到服务端去验证该用户是否存在。。。
                     *
                     * --------------------------------------
                     */


                    if(userClientService.checkUser(userId,passWord)){//还没有写完
                        System.out.println("========欢迎(用户"+userId+"登录)========");

                        //进入二级菜单
                        mainMenuSecond(userId);
                    }else{
                        System.out.println("用户名或密码错误!");

                    }

                    break;


                //注册账号页面
                case "2":
                    System.out.println("注册账号");
                    System.out.print("请输入用户名:");
                    String newUserId=Utility.readString(10);
                    System.out.println("请设置密码");
                    String newPasswd=Utility.readString(10);
                    while(true) {
                        System.out.println("请再次输入密码");
                        String newPasswd2 = Utility.readString(10);
                        if(!newPasswd2.equals(newPasswd)){
                            System.out.println("密码不一致");
                        }else{
                            break;
                        }
                    }
                    boolean isRegisterFail = userClientService.newUserIdIsExist(newUserId, newPasswd);
                    if(isRegisterFail){
                        System.out.println("该用户名已存在！");
                    }else{
                        System.out.println("注册成功！");
                    }
                    break;

                case "9":

                    loop=false;
                    break;
            }
        }



    }

    /**
     * 二级菜单
     * @param userId//用户名
     */
    private void mainMenuSecond(String userId){
        while(loop){
            System.out.println("========网络通信系统二级菜单(用户 " + userId + ")========");
            System.out.println("\t\t1.显示在线用户列表");
            System.out.println("\t\t2.群发消息");
            System.out.println("\t\t3.私聊消息");
            System.out.println("\t\t4.发送文件");
            System.out.println("\t\t9.退出系统");
            System.out.print("请输入你的选择:");
            key= Utility.readString(1);
            switch(key){
                case "1":
                    System.out.println("\n显示在线用户列表");
                    //获取在线用户列表方法
                    userClientService.onlineFriendList();
                    break;
                case "2":
                    System.out.println("请输入想对大家说的话");
                    String s=Utility.readString(100);
                    messageClientService.sendMessageToAll(s,userId);

                    break;
                case "3":
                    System.out.println("私聊消息");
                    System.out.print("请输入私聊对象(已在线)：");
                    String getterId = Utility.readString(50);
                    System.out.print("输入想说的话:");
                    String content = Utility.readString(100);
                    messageClientService.sendMessageToOne(content, userId, getterId);
                    break;
                case "4":
                    System.out.print("请输入接收文件的用户(在线):");
                    getterId = Utility.readString(50);
                    System.out.println("请输入文件的路径(形式 d:\\xx.jpg)");
                    String src = Utility.readString(100);
                    //目标路径后来优化后可以由接收者修改
                    System.out.println("请输入文件保存到对方的哪个路径(形式 d:\\xx.jpg)");
                    String dest=Utility.readString(100);
                    fileClientService.sendFileToOne(src,dest,userId,getterId);

                    break;
                case "9":
                    //调用方法，给服务器发送一个退出系统的message
                    userClientService.logout();
                    loop=false;
                    break;
            }
        }
    }


}
