package org.chat.miniqq;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class qqinpage2 extends JFrame {
    private JLabel ipLabel, userLabel, serverLabel, meanLabel;
    private JButton inButton, outButton;
    private JTextField ipField, userField, serverField;
    private JRadioButton meanButton, mean2Button;
    private JPanel ipPanel, userPanel, serverPanel, meanPanel, lastPanel;

    public qqinpage2() {
        ipLabel = new JLabel("请输入你要连入的IP地址：");
        userLabel = new JLabel("请  输  入  你  的  用 户 名：");
        serverLabel = new JLabel("请输入你 要接入的 端 口：");
        meanLabel = new JLabel("请选择你需要的聊天方式：");

        inButton = new JButton("连接");
        outButton = new JButton("退出");
        meanButton = new JRadioButton("双人");
        mean2Button = new JRadioButton("多人");
        ButtonGroup bg = new ButtonGroup();
        bg.add(meanButton);
        bg.add(mean2Button);
        meanButton.setSelected(true);

        ipField = new JTextField(20);
        ipField.setText("127.0.0.1");
        userField = new JTextField(20);
        serverField = new JTextField(20);

        ipPanel = new JPanel(new FlowLayout());
        userPanel = new JPanel(new FlowLayout());
        serverPanel = new JPanel(new FlowLayout());
        meanPanel = new JPanel(new FlowLayout());
        lastPanel = new JPanel(new FlowLayout());

        ipPanel.add(ipLabel);
        ipPanel.add(ipField);
        userPanel.add(userLabel);
        userPanel.add(userField);
        serverPanel.add(serverLabel);
        serverPanel.add(serverField);
        meanPanel.add(meanLabel);
        meanPanel.add(meanButton);
        meanPanel.add(mean2Button);
        lastPanel.add(inButton);
        lastPanel.add(outButton);
        addListener();

        Container container = getContentPane();
        container.setLayout(new GridLayout(5, 1));
        container.add(ipPanel);
        container.add(userPanel);
        container.add(serverPanel);
        container.add(meanPanel);
        container.add(lastPanel);
        setBounds(600, 200, 500, 250);
        setTitle("客户端");
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
                if (ipField.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "请输入ip", "提示", JOptionPane.INFORMATION_MESSAGE);
                } else if (userField.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "请输入用户名", "提示", JOptionPane.INFORMATION_MESSAGE);
                } else if (!meanButton.isSelected() && !mean2Button.isSelected()) {
                    JOptionPane.showMessageDialog(null, "请选择聊天方式", "提示", JOptionPane.INFORMATION_MESSAGE);
                } else if (serverField.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "请输入接口", "提示", JOptionPane.INFORMATION_MESSAGE);
                } else if (mean2Button.isSelected()) {
                    dispose();
                    new ClientFrame(userField.getText(), ipField.getText(), Integer.parseInt(serverField.getText()));
                } else if (meanButton.isSelected()) {
                    dispose();
                    new ClientTPC(userField.getText(), ipField.getText(), Integer.parseInt(serverField.getText()));
                }
            }
        });
    }

    public static void main(String[] args) {
        qqinpage2 a = new qqinpage2();
        a.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
