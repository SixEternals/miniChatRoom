package org.chat.Threads;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{
    ServerSocket serverSocket;
    private Server(ServerSocket socket){
        serverSocket = socket;
    }

    @Override
    public void run(){
        while(true){
            Socket client = null;
            try{
                client = serverSocket.accept();

            }catch (IOException e){
                break;
                //直接关闭
            }
        }
    }
}
