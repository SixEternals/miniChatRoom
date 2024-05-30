package org.chat.server;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class ServerService {
    private int port;
    private String IP;
    private ServerSocket serverSocket;
    public ServerService() {
        init();
        initUsers();
    }

    /**
     * init()方法：初始化Server的ip port
     */
    private void init(){
        Properties properties = new Properties();
        try(FileInputStream in = new FileInputStream("src\\main\\resources\\serverConfig.properties")){
            properties.load(in);
            try{ // 关闭资源

            }finally {
                in.close();
            }
        }catch (IOException e){
            System.out.println("error from ServerService");
            e.printStackTrace();
        }
        port = Integer.parseInt(properties.getProperty("serverPort"));
        IP = properties.getProperty("serverIP");
    }
    private void initUsers(){


    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);
        
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket.getRemoteSocketAddress());
            // Handle the client connection
        }
    }


}
