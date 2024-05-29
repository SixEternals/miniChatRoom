package org.chat.panel;

import org.chat.model.User;
import org.chat.server.ServerRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ClientPanel extends JFrame {
    private JFrame mainFrame = new JFrame("QQ");
    private Container c = mainFrame.getContentPane();
    private JLabel label1 = new JLabel("用户名");
    private JTextField username = new JTextField();
    private JLabel label2 = new JLabel("密   码");
    private JPasswordField password = new JPasswordField();
    private JButton okButton = new JButton("确定");
    private JButton cancelButton = new JButton("取消");
    private JButton registerButton = new JButton("注册"); // 创建注册按钮

    // 聊天室界面组件
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
//    private ChatClient chatClient; 未实现

    public ClientPanel() {
        mainFrame.setBounds(600, 200, 300, 220);
        c.setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        init();
        listener(); // 初始化时添加监听器
        getServerConnection(); // 与服务器端数据库建立联系
        mainFrame.setVisible(true);
    }
    private void getServerConnection(){
        ServerRepository s = new ServerRepository();
        s.getConnection();
    }
    private void init() {
        // 标题部分
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout());
        titlePanel.add(new JLabel("QQ登陆系统"));
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
        // 添加注册按钮
        buttonPanel.add(registerButton);
        c.add(buttonPanel, BorderLayout.SOUTH);
    }
    private void createRegisterWindow() {
        // 创建注册窗口
        JFrame registerFrame = new JFrame("注册新账号");
        registerFrame.setSize(300, 350); // 设置窗口大小
        registerFrame.setLayout(null); // 使用自由布局
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registerFrame.setLocationRelativeTo(mainFrame); // 相对于主窗口居中

        // 添加组件到注册窗口
        JLabel labelUsername = new JLabel("用户名:");
        JTextField usernameField = new JTextField();
        JLabel labelPassword = new JLabel("密   码:");
        JPasswordField passwordField = new JPasswordField();
        JLabel labelConfirmPassword = new JLabel("确认密码:");
        JPasswordField confirmPasswordField = new JPasswordField();
        JButton registerConfirmButton = new JButton("确认注册");
        JButton registerCancelButton = new JButton("取消");

        // 设置组件的位置和大小
        labelUsername.setBounds(50, 50, 80, 20);
        usernameField.setBounds(140, 50, 150, 20);
        labelPassword.setBounds(50, 100, 80, 20);
        passwordField.setBounds(140, 100, 150, 20);
        labelConfirmPassword.setBounds(50, 150, 80, 20);
        confirmPasswordField.setBounds(140, 150, 150, 20);
        registerConfirmButton.setBounds(50, 200, 80, 30);
        registerCancelButton.setBounds(170, 200, 80, 30);

        // 添加组件到注册窗口
        registerFrame.add(labelUsername);
        registerFrame.add(usernameField);
        registerFrame.add(labelPassword);
        registerFrame.add(passwordField);
        registerFrame.add(labelConfirmPassword);
        registerFrame.add(confirmPasswordField);
        registerFrame.add(registerConfirmButton);
        registerFrame.add(registerCancelButton);

        // 添加按钮监听器
        registerConfirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 处理注册逻辑
                /**
                 * 一开始注册会指定默认名字，所以一开始输入的是u_ID 而且规定只能输入数字
                 */
                String username = usernameField.getText();
                String password = String.valueOf(passwordField.getPassword());
                String confirmPassword = String.valueOf(confirmPasswordField.getPassword());
                if(isUsernameValidWhileRegister(username)) { // 验证是不是纯数字

                    if ((password.equals(confirmPassword))) {
                        // 注册成功逻辑
                        // TODO:将数据写入数据库 （√）
                        User user = new User();
                        // 获取注册时间
                        Timestamp eventTime = new Timestamp(System.currentTimeMillis());
                        user.setDefault(username, password, eventTime); // 账号 密码 注册时间
                        // TODO:检测账号不能有重复的 (√)
                        try {
                            if(ServerRepository.isExistedAccount(username)){
                                JOptionPane.showMessageDialog(registerFrame, "账号已存在，请重新输入");
                            }else{
                                ServerRepository.addUser(user);

                                JOptionPane.showMessageDialog(registerFrame, "注册成功！");
                                registerFrame.dispose(); // 关闭注册窗口
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }

                    } else {
                        // 密码不匹配逻辑
                        JOptionPane.showMessageDialog(registerFrame, "密码不匹配，请重新输入！");
                    }

                }else{
                    JOptionPane.showMessageDialog(registerFrame, "账号只能为纯数字");
                }
            }
        });

        registerCancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerFrame.dispose(); // 关闭注册窗口
            }
        });

        // 显示注册窗口
        registerFrame.setVisible(true);
    }

    /**
     * 用于验证【注册】的账号是否纯数字
     * @param username
     * @return T/F
     */
    private boolean isUsernameValidWhileRegister(String username){
        String regex = "^[0-9]+$";
        return username.matches(regex);
    }
    // 获取
    private void listener() {
        // 确认键
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String uname = username.getText();
                String pwd = String.valueOf(password.getPassword());
//                System.out.println("登录用户名: " + uname + " 密码: " + pwd);

                // TODO:检查登录的u_id和u_password正确与否
                try {
                    if(ServerRepository.checkLoginAccount_pwd(uname, pwd)){
                        // 检查得到账号密码合法
                        JOptionPane.showMessageDialog(mainFrame, "登陆成功");
                        mainFrame.dispose();
                        // TODO:打开聊天窗口
                        createChatWindow();
                    }else{
                        JOptionPane.showMessageDialog(mainFrame, "登录失败，请重新输入账号密码");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

            }
        });

        // 取消键
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username.setText("");
                password.setText("");
            }
        });

        // 注册按钮监听
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createRegisterWindow();
            }
        });
    }

    private void createChatWindow() {
    }

    // 测试
    public static void main(String[] args) {
        ClientPanel obj = new ClientPanel();
    }
}
