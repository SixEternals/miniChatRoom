package org.chat.server.panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerLoginPanel extends JFrame {
    private JFrame mainFrame = new JFrame("miniQQ服务端");
    private Container c = mainFrame.getContentPane();
    private JLabel label1 = new JLabel("管理员");
    private JTextField username = new JTextField();
    private JLabel label2 = new JLabel("密   码");
    private JPasswordField password = new JPasswordField();
    private JButton okButton = new JButton("确定");
    private JButton cancelButton = new JButton("取消");
    private ServerMonitorPanel monitorPanel;

    public ServerLoginPanel() {
        mainFrame.setBounds(600, 200, 300, 220);
        c.setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        init();
        listener(); // 初始化时添加监听器
        mainFrame.setVisible(true);
    }
    private void init() {
        // 标题部分
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout());
        titlePanel.add(new JLabel("miniQQ服务端"));
        c.add(titlePanel, BorderLayout.NORTH);

        // 输入部分
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(null);
        label1.setBounds(50, 20, 50, 20);
        label2.setBounds(50, 60, 50, 20);
        fieldPanel.add(label1);
        fieldPanel.add(label2);
        username.setBounds(110, 20, 120, 20);
        password.setBounds(110, 60, 120, 20);
        fieldPanel.add(username);
        fieldPanel.add(password);
        c.add(fieldPanel, BorderLayout.CENTER);

        // 按钮部分
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        c.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void listener(){
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = username.getText();
                String pwd = String.valueOf(password.getPassword());
                if(name.equals("root") && pwd.equals("123456")){
                    JOptionPane.showMessageDialog(null, "管理员登陆成功!");
                    // 进入聊天室监控窗口
                    mainFrame.dispose();// 关闭
                    createNewMonitorWindow();
                }else{
                    JOptionPane.showMessageDialog(null, "账号或密码失败!请重新输入");
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username.setText("");
                password.setText("");
            }
        });
    }
    private void createNewMonitorWindow(){
        monitorPanel = new ServerMonitorPanel();
        monitorPanel.setVisible(true);
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                new ServerLoginPanel().setVisible(true);
//            }
//        });
    }
    private void buildServerConnection(){

    }

    // 测试
    public static void main(String[] args){
        new ServerLoginPanel();
    }
}
