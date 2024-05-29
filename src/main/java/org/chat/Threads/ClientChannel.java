package org.chat.Threads;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientChannel {
    private static CopyOnWriteArrayList<Channel> all = new CopyOnWriteArrayList<>();

    // 一个channel代表一个用户
    static class Channel implements Runnable {
        private DataOutputStream dos;
        private DataInputStream dis;
        private boolean isRunning;
        private String name;
        private Socket socket; // 每个客户端一个套接字

        public Channel(Socket socket){
            // login成功后 从输入的账号中获取name

            this.socket = socket;
            isRunning = true; // 代表开始运行了

            try{ // 获取资源
                this.dis = new DataInputStream(socket.getInputStream());
                this.dos = new DataOutputStream(socket.getOutputStream());

            }catch (IOException e){

            }

        }

        public String receive() {

            return null;
        }

        public void send(String msg) {

        }

        public void sendOthers(String msg, boolean isSys) {

        }

        public void release() {

        }

        @Override
        public void run() {

        }

    }
}
