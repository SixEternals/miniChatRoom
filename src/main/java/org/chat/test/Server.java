package org.chat.test;

import org.chat.Threads.CloseUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static CopyOnWriteArrayList<Channel> all = new CopyOnWriteArrayList<>();

    // 一个Channel就代表一个客户端（将其封装成一个类）
    static class Channel implements Runnable{
        private DataOutputStream dos;
        private DataInputStream dis;
        private boolean flag;
        private String name;
        private Socket socket;

        // 构造器 用于数据的初始化
        public Channel(Socket socket){
            this.socket= socket;
            flag = true;

            try {
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());

                // 获取聊天昵称 因为这个聊天室规定第一次先输入昵称
                // 第二次及以后的发言才算是正式发言
                this.name = receive();

                // 当客户假如聊天室后 发送欢迎信息
                this.send("欢迎" + name + "进入本聊天室test");

                // 将消息发给其他成员
                this.sendOthers(name + "加入到聊天室", true);
            }catch (IOException e){
                this.flag = false;
                this.release();
//                e.printStackTrace();
            }
        }

        // 接收消息
        public String receive(){
            String msg = "";
            try{
                msg = dis.readUTF();
            }catch (IOException e){
                release();
            }
            return msg;
        }

        // 发送消息（发给自己）
        public void send(String msg){
            if(!msg.equals("")){
                try {
                    dos.writeUTF(msg);
                    dos.flush();
                }catch (IOException e){
                    release();
//                    e.printStackTrace();
                }
            }
        }


        /**
         * 1. 群聊：发给除了自己以外的人
         * 2. 私聊 使用艾特@私聊
         *
         */
        public void sendOthers(String msg,boolean isSys){
            boolean isPrivate = msg.startsWith("@");
            if(isPrivate){ // 私聊
                // 获取冒号下标
                int index = msg.indexOf(":");
                // 截取名字
                String targetName = msg.substring(1,index);

                // 截取要发的信息
                msg= msg.substring(index + 1);
                // 遍历容器找到要艾特的人
                for(Channel other: all){
                    if(other.name.equals(targetName)){
                        other.send(this.name + "悄悄对你说:" + msg);
                        break;
                    }
                }
            }else{ // 不是私聊
                // 发给除自己以外的其他人
                for(Channel other: all){
                    if(other != this){ // 不发给自己
                        if(!isSys){
                            other.send(this.name + "说" + msg);
                        }else{
                            other.send(msg); //是系统消息
                        }
                    }
                }
            }
        }

        // 释放资源
        public void release(){
            this.flag =false;

            CloseUtil.CloseAll(dos,dis,socket);
        }


        @Override
        public void run(){
            while(flag){
                // 接收消息
                String msg = receive();

                //群聊发信息 （类似于群发）
                if(!msg.equals("")){
                    sendOthers(msg, false);// 不是系统消息
                }
            }
        }

        // 测试
        public static void main(String[] args) throws  IOException{
            System.out.println("===服务器端===");

            // 获取ServerSocket管道
            ServerSocket sSocket = new ServerSocket(9898);
            while(true){
                // 获取客户端socket
                Socket client = sSocket.accept();
                System.out.println("一个客户端连接成功");
                Channel channel = new Channel(client);

                // 将每一个客户端添加到容器中 进行统一管理
                all.add(channel);

                // 启动线程
                new Thread(channel).start();
            }
        }

    }
}
