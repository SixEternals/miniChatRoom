package org.chat.Threads;

import org.chat.model.User;
import org.chat.server.panel.ServerMonitorPanel;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends ServerMonitorPanel implements  Runnable{
    // Server部分

    public Server(ServerSocket socket){
            serverSocket = socket;
    }
    @Override
    public void run() {

        while (true) {
            Socket client = null;
            try {
                client = serverSocket.accept();
                userId++;
                User user = new User();
                // todo:user初始化


            } catch (IOException e) {
                //关闭不处理
                System.out.println("1名用户退出");
                break;
            }
        }
    }

    private User createUser(Socket client) throws IOException {
        DataInputStream dis = new DataInputStream(client.getInputStream());
        return null;
    }

}
