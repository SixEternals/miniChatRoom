package org.chat.miniqq;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class qqinpage extends JFrame { // 服务器端
    private JLabel ipLabel, userLabel, serverLabel, meanLabel;
    private JButton inButton, outButton;
    private JTextField userField, serverField;
    private JRadioButton meanButton, mean2Button;
    private JPanel ipPanel, userPanel, serverPanel, meanPanel, lastPanel;
    private JPasswordField ipField;

    public qqinpage() {
        userLabel = new JLabel("请 输 入 管 理 员 用 户 名：");
        ipLabel = new JLabel("请  输  入  管 理 员 密  码：");
        serverLabel = new JLabel("请输入你 要开放的 端 口：");
        meanLabel = new JLabel("聊    天    方    式：");

        inButton = new JButton("开启服务器");
        outButton = new JButton("退出");
        meanButton = new JRadioButton("双人");
        mean2Button = new JRadioButton("多人");
        ButtonGroup bg = new ButtonGroup();
        bg.add(meanButton);
        bg.add(mean2Button);
        meanButton.setSelected(true);
        ipField = new JPasswordField(20);
        userField = new JTextField(20);
        serverField = new JTextField(20);

        userPanel = new JPanel(new FlowLayout());
        serverPanel = new JPanel(new FlowLayout());
        meanPanel = new JPanel(new FlowLayout());
        lastPanel = new JPanel(new FlowLayout());
        ipPanel = new JPanel(new FlowLayout());

        userPanel.add(userLabel);
        userPanel.add(userField);
        serverPanel.add(serverLabel);
        serverPanel.add(serverField);
        meanPanel.add(meanLabel);
        meanPanel.add(meanButton);
        meanPanel.add(mean2Button);
        lastPanel.add(inButton);
        lastPanel.add(outButton);
        ipPanel.add(ipLabel);
        ipPanel.add(ipField);
        addListener();

        Container container = getContentPane();
        container.setLayout(new GridLayout(5, 1));
        container.add(userPanel);
        container.add(ipPanel);
        container.add(serverPanel);
        container.add(meanPanel);
        container.add(lastPanel);

        setBounds(600, 200, 500, 250);
        setTitle("管理员登录");
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void addListener() {
        outButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        inButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (userField.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "请输入用户名", "提示", JOptionPane.INFORMATION_MESSAGE);
                } else if (ipField.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "请输入用户名", "提示", JOptionPane.INFORMATION_MESSAGE);
                } else if (!meanButton.isSelected() && !mean2Button.isSelected()) {
                    JOptionPane.showMessageDialog(null, "请选择聊天方式", "提示", JOptionPane.INFORMATION_MESSAGE);
                } else if (userField.getText().equals("root") && ipField.getText().equals("123456")) {
                    if (mean2Button.isSelected()) {
                        dispose();
                        new Server(Integer.parseInt(serverField.getText()));
                    } else {
                        dispose();
                        JOptionPane.showMessageDialog(null, "服务器已打开", "提示", JOptionPane.INFORMATION_MESSAGE);
                        new ServerTPC(Integer.parseInt(serverField.getText()));
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "密码错误\n程序退出", "提示", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(1);
                }
            }

        });
    }

    public static void main(String[] args) {
        qqinpage a = new qqinpage();
        a.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
