package org.chat.Threads;

import org.chat.Utils.CloseUtils;
import org.chat.model.User;
import org.chat.server.panel.ServerMonitorPanel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Channel extends ServerMonitorPanel implements Runnable {
    private boolean isRunning;
    private CopyOnWriteArrayList<Channel> shieldList; // 用户列表
    private HashMap<String, Channel> privateQueue;// 私聊列表
    private User user;
    protected DataInputStream dis;
    protected DataOutputStream dos;
    private Socket socket;
    public Channel(User user){
        try{
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());
            this.shieldList = new CopyOnWriteArrayList<>();
            this.privateQueue = new HashMap<>();
            this.isRunning = true;
            this.user = user;
        }catch (IOException ex3){
            System.out.println("聊天室服务器初始化失败");
            this.release();
        }
    }

    public User getUser(){
        return user;
    }

    public CopyOnWriteArrayList<Channel> getShieldList(){
        return shieldList;
    }

    private String receive(){
        String msg = "";
        try{
            msg = this.dis.readUTF();
        }catch (IOException e){
            this.release();
        }
        return msg;
    }


    public void release(){
        this.isRunning = false;
        CloseUtils.close(dis, dos, socket);

        // 列表中移除该用户
        users_model.removeElement(user.getU_ID() + "#@" + user.getU_name());
        if(allUserChannel.contains(this)){
            allUserChannel.remove(this);
        }
        users_label.setText("当前连接用户:" + allUserChannel.size());
    }

    @Override
    public void run(){
        while(isRunning){
            String msg = receive();
            if(!msg.equals("")){
                // todo:parseMsg(msg)
                // todo:将信息广播给其他人
                if(msg.length() > 0){

                    // 遍历所有用户
                    for(Channel channel : allUserChannel){
                        User targetUser = channel.getUser();
                        // todo:实际上这里可以处理广播，私聊，或其他聊天功能 不过先放着了

                        // 先做的广播
                        if(targetUser != null){
                            try {
                                channel.dos.writeUTF(msg); // 发送消息
                                channel.dos.flush(); // 刷新输出流保证成功发送消息
                            }catch (IOException e){
                                // todo:记录日志或处理错误
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
}
