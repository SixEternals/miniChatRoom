package org.chat.client;

import org.chat.model.User;
import org.chat.panel.ClientPanel;

public class ClientMain {
    private User user; // 本user 每一个ClientMain就会创建一个独特的user

    // 1. 界面
    ClientPanel clientPanel = null;
    public void start(){
       // 1. 画出界面
        clientPanel = new ClientPanel();

        // 2. 验证账号密码的正确与否

        // 3. 将个人信息取出来放在本user中

        // 4. 建立Socket连接

        // 5. 单独创建一个线程来完成接收信息的功能
    }
    public static void main(String[] args){
        ClientMain entry = new ClientMain();
        entry.start();
    }
}
