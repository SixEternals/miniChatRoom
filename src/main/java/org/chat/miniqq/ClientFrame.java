package org.chat.miniqq;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import javafx.scene.control.ScrollPane;

public class ClientFrame extends JFrame {
    private JPanel contentPane,panel;
    private JTextField tfMessage;
    private JButton btnSend;
    private JTextArea textArea;
    private String userName;
    private ChatRoomClient client;
    public ClientFrame(String userName,String ip,int i) {
        textArea=new JTextArea(10,10);
        textArea.setEditable(false);
        tfMessage =new JTextField(20);
        btnSend = new JButton("发送");
        try {
            client = new ChatRoomClient(ip, i);
            textArea.setText("连接成功\n");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        setTitle("客户端");
        tfMessage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Date date = new Date();
                SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
                client.sendMessage(userName + ":" + sf.format(date) + ":\n" + tfMessage.getText());
                tfMessage.setText("");
            }
        });
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Date date = new Date();
                SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
                client.sendMessage(userName + ":" + sf.format(date) + ":\n" + tfMessage.getText());
                tfMessage.setText("");
            }
        });

        this.userName = userName;
        ReadMessageThread messageThread = new ReadMessageThread();
        messageThread.start();

        Container c=getContentPane();
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        panel = new JPanel();
        panel.setLayout(new FlowLayout());

        contentPane.add(new JScrollPane(textArea),BorderLayout.CENTER);
        panel.add(tfMessage);
        panel.add(btnSend);
        contentPane.add(panel,BorderLayout.SOUTH);
        c.add(contentPane);

        setBounds(100, 100, 450, 300);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }
    private class ReadMessageThread extends Thread {
        public void run() {
            while (true) {
                String str = client.reciveMessage();
                textArea.append(str + "\n");
            }
        }
    }

}
