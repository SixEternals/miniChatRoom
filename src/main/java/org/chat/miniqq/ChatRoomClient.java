package org.chat.miniqq;


import java.io.*;
import java.net.*;

public class ChatRoomClient {
    private Socket socket;
    private BufferedReader bufferReader;
    private PrintWriter pWriter;

    public ChatRoomClient(String host, int port) throws UnknownHostException, IOException {
        socket = new Socket(host, port);
        bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        pWriter = new PrintWriter(socket.getOutputStream());
    }

    public void sendMessage(String str) {
        pWriter.println(str);
        pWriter.flush();
    }

    public String reciveMessage() {
        try {
            return bufferReader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
