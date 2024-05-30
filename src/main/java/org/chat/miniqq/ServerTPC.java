package org.chat.miniqq;


import java.io.*;

import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ServerTPC extends JFrame {
    private JTextField enterField;
    private JTextArea displayArea;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;
    private JButton but;
    private JPanel panel;
    public ServerTPC(int i) {
        super("管理员");
        panel = new JPanel(new FlowLayout());
        but = new JButton("发送");
        Container container = getContentPane();
        enterField = new JTextField(20);
        enterField.setEditable(false);
        enterField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendData(e.getActionCommand());
                enterField.setText("");
            }
        });
        but.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendData(e.getActionCommand());
                enterField.setText("");
            }
        });
        try {
            server = new ServerSocket(i, 100);
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(null, "端口被占用请 重新选择", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
        panel.add(enterField);
        panel.add(but);
        container.add(panel, BorderLayout.SOUTH);
        displayArea = new JTextArea();
        displayArea.setEditable(false);

        container.add(new JScrollPane(displayArea), BorderLayout.CENTER);
        setBounds(600, 200, 500, 250);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Thread a = new Thread(new Roll());
        a.start();
    }

    private void waitForConnection() throws IOException {
        displayMessage("等待用户接入");
        connection = server.accept();
    }

    private void getStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
    }

    private void processConnection() throws IOException {
        String message = "连接成功";
        sendData(message);
        setTextFieldEditable(true);
        try {
            do {
                message = (String) input.readObject();
                displayMessage("\n" + message);
            } while (true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "好友已下线", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void closeConnection() {
        displayMessage("对话结束\n");
        setTextFieldEditable(false);
        try {
            output.close();
            input.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendData(String message) {
        try {
            Date date = new Date();
            SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
            output.writeObject("管理员:" + sf.format(date) + "\n" + message);
            output.flush();
            displayMessage("\n管理员:" + sf.format(date) + "\n" + message);
        } catch (Exception e) {
            displayArea.append("\n发生错误");
        }
    }

    private void displayMessage(final String messageToDisplay) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                displayArea.append(messageToDisplay);
                displayArea.setCaretPosition(displayArea.getText().length());
            }
        });
    }

    private void setTextFieldEditable(final boolean editable) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                enterField.setEditable(editable);
            }
        });
    }

    class Roll implements Runnable {
        public void run() {
            try {
                while (true) {
                    try {
                        waitForConnection();
                        getStreams();
                        processConnection();
                    } catch (EOFException e) {
                        System.out.println("这里发生了错误请及时修复");
                    } finally {
                        closeConnection();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
