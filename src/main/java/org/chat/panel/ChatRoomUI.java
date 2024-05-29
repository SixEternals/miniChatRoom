package org.chat.panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRoomUI extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JTextField groupNumberField; // 输入群号的文本框
    private JButton createGroupButton;    // 新建群聊按钮
    private JButton joinGroupButton;      // 加入已有群聊按钮
    private JList<String> groupList;     // 群聊列表
    private Map<String, List<String>> groups; // 存储群聊和成员的映射

    public ChatRoomUI() {
        super("Chat Room");
        initializeUI();
        this.setSize(1000, 1000); // 设置窗口大小为 1000x1000 像素
//        this.setResizable(false); // 设置窗口大小不可调整
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示

        // 画出界面
        draw();

        // 初始化群聊映射
        groups = new HashMap<>();

        // 监听
        listen();
    }

    private void draw(){
        // 添加输入群号的文本框
        groupNumberField = new JTextField(10);
        groupNumberField.setBounds(10, 350, 180, 30);
        add(groupNumberField);

        // 新建群聊按钮
        createGroupButton = new JButton("新建群聊");
        createGroupButton.setBounds(200, 350, 100, 30);
        createGroupButton.addActionListener(e -> createGroup());
        add(createGroupButton);

        // 加入已有群聊按钮
        joinGroupButton = new JButton("加入已有群聊");
        joinGroupButton.setBounds(310, 350, 120, 30);
        joinGroupButton.addActionListener(e -> joinGroup());
        add(joinGroupButton);

        // 群聊列表
        groupList = new JList<>();
        groupList.setBounds(10, 390, 380, 100);
        add(new JScrollPane(groupList));
    }
    private void createGroup() {
        String groupNumber = groupNumberField.getText();
        // 检查群号是否唯一等逻辑
        groups.put(groupNumber, new ArrayList<>()); // 创建新群聊
        groupList.setListData(groups.keySet().toArray(new String[0])); // 更新群聊列表
    }

    private void joinGroup() {
        String groupNumber = groupNumberField.getText();
        if (groups.containsKey(groupNumber)) {
            // 加入群聊逻辑
            groups.get(groupNumber).add("新成员"); // 假设新成员是固定的字符串
            groupList.setListData(groups.keySet().toArray(new String[0])); // 更新群聊列表
        } else {
            JOptionPane.showMessageDialog(this, "群聊不存在", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void listen(){
        // 群聊列表选择事件
        groupList.addListSelectionListener(e -> {
            String selectedGroup = (String) groupList.getSelectedValue();
            // 根据选择的群聊更新聊天区域
            // 这里需要您根据实际逻辑来更新聊天内容
        });
    }
    private void initializeUI() {
        // 设置自由布局
        setLayout(null);

        // 创建聊天显示区域
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBounds(10, 10, 380, 290); // x, y, width, height
        add(scrollPane);

        // 创建消息输入区域
        messageField = new JTextField();
        messageField.setBounds(10, 310, 280, 30); // 可以根据需要调整大小和位置
        add(messageField);

        // 创建发送按钮
        sendButton = new JButton("发送");
        sendButton.setBounds(300, 310, 90, 30); // 可以根据需要调整大小和位置
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                // 这里可以添加发送消息的逻辑
                messageField.setText("");
                addMessageToChat(message);
            }
        });
        add(sendButton);
    }

    public void addMessageToChat(String message) {
        chatArea.append(message + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength()); // 自动滚动到底部
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChatRoomUI().setVisible(true);
            }
        });
    }
}
