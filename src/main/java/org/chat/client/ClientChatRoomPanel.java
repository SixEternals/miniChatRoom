package org.chat.client;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.chat.Threads.Receive;
import org.chat.Utils.CloseUtils;
import org.chat.model.User;

public class ClientChatRoomPanel {
    private JFrame frame;
    //头部参数
    private JTextField host_textfield;
    private JTextField port_textfield;
    private JTextField name_textfield;
    private JButton head_connect;
    private JButton head_exit;
    //底部参数
    private JTextField text_field;
    private JButton foot_send;
    private JButton foot_sysClear;
    private JButton foot_userClear;

    //右边参数
    private JLabel users_label;
    private JButton privateChat_button;
    private JButton shield_button;
    private JButton unshield_button;
    private JList<String> userlist;
    private DefaultListModel<String> users_model;
    private HashMap<String, Integer> users_map;

    //左边参数
    private JScrollPane sysTextScrollPane;
    private JTextPane sysMsgArea;
    private JScrollBar sysVertical;

    //中间参数
    private JScrollPane userTextScrollPane;
    private JTextPane userMsgArea;
    private JScrollBar userVertical;

    //发送和接受参数
    private DataOutputStream dos;
    private Receive receive;
    private Socket charClient;
    //时间格式化工具类
    static private SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置时间

    //当前用户的id
    private int id;

    //私聊窗口Map
    private HashMap<String, privateChatFrame> privateChatFrameMap;

    // 用户信息
    User user;

    public ClientChatRoomPanel(User user){
        this.user = user;
        init();
        listener();
    }

    public static void main(String[] args) {
        new ClientChatRoomPanel(null);
    }

    /**
     * @MethodName init
     * @Params  * @param null
     * @Description 客户端GUI界面初始化，各种监听事件绑定
     * @Return
     * @Since 2020/6/6
     */
    public void init() {
        users_map = new HashMap<>();
        privateChatFrameMap = new HashMap<>();

        frame = new JFrame("聊天室用户端");
        JPanel panel = new JPanel();        /*主要的panel，上层放置连接区，下层放置消息区，中间是消息面板，左边是系统消息，右边是当前room的用户列表*/
        JPanel headpanel = new JPanel();    /*上层panel，用于放置连接区域相关的组件*/
        JPanel footpanel = new JPanel();    /*下层panel，用于放置发送信息区域的组件*/
        JPanel centerpanel = new JPanel();    /*中间panel，用于放置聊天信息*/
        JPanel leftpanel = new JPanel();    /*左边panel，用于放置房间列表和加入按钮*/
        JPanel rightpanel = new JPanel();   /*右边panel，用于放置房间内人的列表*/

        /*顶层的布局，分中间，东南西北五个部分*/
        BorderLayout layout = new BorderLayout();

        /*格子布局，主要用来设置西、东、南三个部分的布局*/
        GridBagLayout gridBagLayout = new GridBagLayout();

        /*主要设置北部的布局*/
        FlowLayout flowLayout = new FlowLayout();

        /*设置初始窗口的一些性质*/
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        frame.setContentPane(panel);
        frame.setLayout(layout);

        /*设置各个部分的panel的布局和大小*/
        headpanel.setLayout(flowLayout);
        footpanel.setLayout(gridBagLayout);
        leftpanel.setLayout(gridBagLayout);
        centerpanel.setLayout(gridBagLayout);
        rightpanel.setLayout(gridBagLayout);

        //设置面板大小
        leftpanel.setPreferredSize(new Dimension(200, 0));
        rightpanel.setPreferredSize(new Dimension(155, 0));
        footpanel.setPreferredSize(new Dimension(0, 40));

        //头部布局
        host_textfield = new JTextField("127.0.0.1");
        port_textfield = new JTextField("8888");
        name_textfield = new JTextField(user.getU_name());
        name_textfield.setEditable(false); // 需要在个人界面中进行修改
        host_textfield.setPreferredSize(new Dimension(100, 25));
        port_textfield.setPreferredSize(new Dimension(70, 25));
        name_textfield.setPreferredSize(new Dimension(150, 25));

        JLabel host_label = new JLabel("服务器IP:");
        JLabel port_label = new JLabel("端口:");
        JLabel name_label = new JLabel("昵称:");

        head_connect = new JButton("连接");
        head_exit = new JButton("退出");

        headpanel.add(host_label);
        headpanel.add(host_textfield);
        headpanel.add(port_label);
        headpanel.add(port_textfield);
        headpanel.add(name_label);
        headpanel.add(name_textfield);
        headpanel.add(head_connect);
        headpanel.add(head_exit);

        //底部布局
        foot_send = new JButton("发送");
        foot_sysClear = new JButton("清空系统消息");
        foot_sysClear.setPreferredSize(new Dimension(193, 0));
        foot_userClear = new JButton("清空聊天消息");
        foot_userClear.setPreferredSize(new Dimension(148, 0));

        text_field = new JTextField();
        footpanel.add(foot_sysClear, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        footpanel.add(text_field, new GridBagConstraints(1, 0, 1, 1, 100, 100,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        footpanel.add(foot_send, new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        footpanel.add(foot_userClear, new GridBagConstraints(3, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));


        //左边布局
        JLabel sysMsg_label = new JLabel("系统消息：");
        sysMsgArea = new JTextPane();
        sysMsgArea.setEditable(false);
        sysTextScrollPane = new JScrollPane();
        sysTextScrollPane.setViewportView(sysMsgArea);
        sysVertical = new JScrollBar(JScrollBar.VERTICAL);
        sysVertical.setAutoscrolls(true);
        sysTextScrollPane.setVerticalScrollBar(sysVertical);
        leftpanel.add(sysMsg_label, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        leftpanel.add(sysTextScrollPane, new GridBagConstraints(0, 1, 1, 1, 100, 100,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        //右边布局
        users_model = new DefaultListModel<>();
        userlist = new JList<>(users_model);
        JScrollPane userListPane = new JScrollPane(userlist);
        users_label = new JLabel("聊天室内人数：0");
        privateChat_button = new JButton("私聊");
        shield_button = new JButton("屏蔽对方");
        unshield_button = new JButton("取消屏蔽");
        rightpanel.add(users_label, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        rightpanel.add(privateChat_button, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        rightpanel.add(shield_button, new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        rightpanel.add(unshield_button, new GridBagConstraints(0, 3, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        rightpanel.add(userListPane, new GridBagConstraints(0, 4, 1, 1, 100, 100,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        //中间布局
        JLabel userMsg_label = new JLabel("世界聊天：");
        userMsgArea = new JTextPane();
        userMsgArea.setEditable(false);
        userTextScrollPane = new JScrollPane();
        userTextScrollPane.setViewportView(userMsgArea);
        userVertical = new JScrollBar(JScrollBar.VERTICAL);
        userVertical.setAutoscrolls(true);
        userTextScrollPane.setVerticalScrollBar(userVertical);

        centerpanel.add(userMsg_label, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        centerpanel.add(userTextScrollPane, new GridBagConstraints(0, 1, 1, 1, 100, 100,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        /*设置顶层布局*/
        panel.add(headpanel, "North");
        panel.add(footpanel, "South");
        panel.add(leftpanel, "West");
        panel.add(rightpanel, "East");
        panel.add(centerpanel, "Center");

        //窗口显示
        frame.setVisible(true);

    }

    private void listener(){
        //窗口关闭事件
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(frame, "确定关闭聊天室界面?", "提示",
                        JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    if (e.getWindow() == frame) {
                        if (receive != null) {
                            sendMsg("exit", ""); //如果已连接就告诉服务器本客户端已断开连接，退出聊天室
                        }
                        frame.dispose();
                        System.exit(0);
                    }
                }
            }
        });

        //聊天信息输入框的监听回车按钮事件
        text_field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {

                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    if (charClient == null || receive == null) {
                        JOptionPane.showMessageDialog(frame, "请先连接服务器进入聊天室！", "提示", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    String text = text_field.getText();
                    if (text != null && !"".equals(text)) {
                        sendMsg("msg", text);
                        text_field.setText("");
                    }
                }
            }
        });

        // 顶部：连接按钮
        head_connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // todo:连接服务器
                boolean isSucess =  connectServer(host_textfield.getText(), Integer.parseInt(port_textfield.getText()));
            }
        });

        // 底部：发送按钮
        foot_send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (charClient == null || receive == null) {
                    JOptionPane.showMessageDialog(frame, "请先连接服务器进入聊天室！", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String text = text_field.getText();
                if (text != null && !"".equals(text)) {
                    sendMsg("msg", text);
                    text_field.setText("");
                }
            }

        });
    }

    public static boolean ipCheckHost(String text) {
        if (text != null && !text.isEmpty()) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."+
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."+
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."+
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
            if (text.matches(regex)) {
                // 返回判断信息
                return true;
            } else {
                // 返回判断信息
                return false;
            }
        }
        return false;
    }


    public static boolean ipCheckPort(String text){
        return text.matches("([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-5]{2}[0-3][0-5])");
    }

    /**
     * @MethodName connectServer
     * @Params  * @param null
     * @Description 开启与服务器的连接，开启接受服务器的指令与信息的Receive线程类
     * @Return
     * @Since 2020/6/6
     */
    private boolean connectServer(String host, int port) {

        try {
            charClient = new Socket(host, port);
            dos = new DataOutputStream(charClient.getOutputStream());
            receive = new Receive(charClient, this);
            (new Thread(receive)).start();//接受服务器的消息的线程(系统消息和其他网友的信息）
            head_connect.setText("已连接");
            head_exit.setText("退出");
            port_textfield.setEditable(false);
            name_textfield.setEditable(false);
            host_textfield.setEditable(false);
            sendMsg("new", name_textfield.getText()); //后续写在登录窗口
            sendMsg("getList", "");
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "连接服务器失败！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * @MethodName sendMsg
     * @Params  * @param null
     * @Description 发送指令与信息给服务器端对应的线程服务
     * @Return
     * @Since 2020/6/6
     */
    private void sendMsg(String cmd, String msg) {
        try {
            dos.writeUTF(msg);
            dos.flush();
        } catch (IOException e) {
            System.out.println("Error from ClientChatRoomPanel,sendMsg Method,发送失败");
            CloseUtils.close(dos, charClient);
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * @MethodName updateTextArea
     * @Params  * @param null
     * @Description 更新系统文本域或聊天事件文本域
     * @Return
     * @Since 2020/6/6
     */
    public void updateTextArea(String content, String where) {
        if (content.length() > 0) {
            Matcher matcher = null;
            if ("user".equals(where)) {
                Pattern pattern = Pattern.compile("<userId>(.*)</userId><userMsg>(.*)</userMsg><time>(.*)</time>");
                matcher = pattern.matcher(content);
                if (matcher.find()) {
                    String userId = matcher.group(1);
                    String userMsg = matcher.group(2);
                    String time = matcher.group(3);
//                if(userMsg.startsWith("<emoji>")){
//                    String emojiCode = userMsg.substring(7, smsg.length()-8);
//                    insertMessage(userTextScrollPane, userMsgArea, emojiCode, fromName+"说：", null,userVertical);
//                    return ;
//                }
                    if (userId.equals("0")) {
                        insertMessage(userTextScrollPane, userMsgArea, null, getUserName(userId) + " "+time , " "+userMsg, userVertical, false);
                    } else {
                        String fromName = getUserName(userId);
                        if (fromName.equals("[用户" + id + "]" + name_textfield.getText())) //如果是自己说的话
                        {
                            fromName = "你";
                        }
                        insertMessage(userTextScrollPane, userMsgArea, null,  fromName + " "+time, " "+userMsg, userVertical, false);
                    }
                }
            } else {
                Pattern pattern = Pattern.compile("<time>(.*)</time><sysMsg>(.*)</sysMsg>");
                matcher = pattern.matcher(content);
                if (matcher.find()) {
                    String sysTime = matcher.group(1);
                    String sysMsg = matcher.group(2);
                    insertMessage(sysTextScrollPane, sysMsgArea, null, "[系统消息] " + sysTime, sysMsg, sysVertical, true);
                }
            }
        }
    }

    /**
     * @MethodName insertMessage
     * @Params  * @param null
     * @Description 更新文本域信息格式化工具
     * @Return
     * @Since 2020/6/6
     */
    private void insertMessage(JScrollPane scrollPane, JTextPane textPane, String icon_code,
                               String title, String content, JScrollBar vertical, boolean isSys) {
        StyledDocument document = textPane.getStyledDocument();     /*获取textpane中的文本*/
        /*设置标题的属性*/
        Color content_color;
        if (isSys) {
            content_color = Color.RED;
        } else {
            content_color = Color.GRAY;
        }
        SimpleAttributeSet title_attr = new SimpleAttributeSet();
        StyleConstants.setBold(title_attr, true);
        StyleConstants.setForeground(title_attr, Color.BLUE);
        /*设置正文的属性*/
        SimpleAttributeSet content_attr = new SimpleAttributeSet();
        StyleConstants.setBold(content_attr, false);
        StyleConstants.setForeground(content_attr, content_color);
        Style style = null;
        if (icon_code != null) {
            Icon icon = new ImageIcon("icon/" + icon_code + ".png");
            style = document.addStyle("icon", null);
            StyleConstants.setIcon(style, icon);
        }

        try {
            document.insertString(document.getLength(), title + "\n", title_attr);
            if (style != null) {
                document.insertString(document.getLength(), "\n", style);
            } else {
                document.insertString(document.getLength(), content + "\n", content_attr);
            }

        } catch (BadLocationException ex) {
            System.out.println("Bad location exception");
        }
        /*设置滑动条到最后*/
        textPane.setCaretPosition(textPane.getDocument().getLength());
//        vertical.setValue(vertical.getMaximum());
    }

    /**
     * @MethodName getUserName
     * @Params  * @param null
     * @Description 在users_map中根据value值用户ID获取key值的用户名字
     * @Return
     * @Since 2020/6/6
     */
    private String getUserName(String strId) {
        int uid = Integer.parseInt(strId);
        Set<String> set = users_map.keySet();
        Iterator<String> iterator = set.iterator();
        String cur;
        while (iterator.hasNext()) {
            cur = iterator.next();
            if (users_map.get(cur) == uid) {
                return cur;
            }
        }
        return "";
    }


    /**
     * @MethodName showEscDialog
     * @Params  * @param null
     * @Description 处理当前客户端用户断开与服务器连接的一切事务
     * @Return
     * @Since 2020/6/6
     */
    public void showEscDialog(String content) {

        //清除所有私聊
        if (privateChatFrameMap.size() != 0) {
            Set<Map.Entry<String, privateChatFrame>> entrySet = privateChatFrameMap.entrySet();
            for (Map.Entry<String, privateChatFrame> entry : entrySet) {
                entry.getValue().dispose(); //关闭对应窗口
                sendMsg("privateExit", entry.getKey()); //想对方说明私聊结束
            }
        }
        //关闭输出流
        CloseUtils.close(dos, charClient);
        receive.setRunning(false);
        //输入框可编辑
        port_textfield.setEditable(true);
        name_textfield.setEditable(true);
        host_textfield.setEditable(true);
        head_connect.setText("连接");
        head_exit.setText("已退出");
        insertMessage(sysTextScrollPane, sysMsgArea, null, "[系统消息] " + df.format(new Date()), content, sysVertical, true);
        JOptionPane.showMessageDialog(frame, content, "提示", JOptionPane.WARNING_MESSAGE);
        /*清除消息区内容，清除用户数据模型内容和用户map内容，更新房间内人数*/
        userMsgArea.setText("");
//        sysMsgArea.setText("");
        users_map.clear();
        users_model.removeAllElements();
        users_label.setText("聊天室内人数：0");

    }

    /**
     *
     * @Params  * @param null
     * @Description 当有新的用户加入聊天室，系统文本域的更新和用户列表的更新
     * @Return
     * @Since 2020/6/6
     */
    public void addUser(String content) {
        if (content.length() > 0) {
            Pattern pattern = Pattern.compile("<username>(.*)</username><id>(.*)</id>");
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                String name = matcher.group(1);
                String id = matcher.group(2);
                if (!users_map.containsKey(name)) {
                    users_map.put("[用户" + id + "]" + name, Integer.parseInt(id));
                    users_model.addElement("[用户" + id + "]" + name);
                } else {
                    users_map.remove("[用户" + id + "]" + name);
                    users_model.removeElement(name);
                    users_map.put("[用户" + id + "]" + name, Integer.parseInt(id));
                    users_model.addElement("[用户" + id + "]" + name);
                }
                insertMessage(sysTextScrollPane, sysMsgArea, null, "[系统消息] " + df.format(new Date()), "[用户" + id + "]" + name + " 加入了聊天室", sysVertical, true);
            }
        }
        users_label.setText("聊天室内人数：" + users_map.size()); //更新房间内的人数
    }


    /**
     * @MethodName delUser
     * @Params  content(为退出用户的ID)
     * @Description 当有用户退出时，系统文本域的通知和用户列表的更新
     * @Return
     * @Since 2020/6/6
     */
    public void delUser(String content) {
        if (content.length() > 0) {
            Set<String> set = users_map.keySet();
            String delName = getUserName(content);
            insertMessage(sysTextScrollPane, sysMsgArea, null, "[系统消息] " + df.format(new Date()), delName + " 退出了聊天室", sysVertical, true);
            users_map.remove(delName);
            users_model.removeElement(delName);
        }
        users_label.setText("聊天室内人数：" + users_map.size());//更新房间内的人数
    }


    /**
     * @MethodName updateUsername
     * @Params  content(为指定用户的ID)
     * @Description 修改指定ID用户的昵称（暂时用不到）
     * @Return
     * @Since 2020/6/6
     */
    public void updateUsername(String content) {
        if (content.length() > 0) {
            Pattern pattern = Pattern.compile("<id>(.*)</id><username>(.*)</username>");
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                String id = matcher.group(1);
                String name = matcher.group(2);
                if ("0".equals(id)) {
                    users_map.remove("[管理员]" + name);
                    users_model.removeElementAt(0);
                    users_map.put("[管理员]" + name, Integer.parseInt(id));
                    users_model.addElement("[管理员]" + name);
                } else if (users_map.get("[用户" + id + "]" + name) != Integer.parseInt(id)) {
                    users_map.put("[用户" + id + "]" + name, Integer.parseInt(id));
                    users_model.addElement("[用户" + id + "]" + name);
                } else {
                    users_map.remove("[用户" + id + "]" + name);
                    users_model.removeElement("[用户" + id + "]" + name);
                    users_map.put("[用户" + id + "]" + name, Integer.parseInt(id));
                    users_model.addElement("[用户" + id + "]" + name);
                }
            }
        }
    }


    /**
     * @MethodName getUserList
     * @Params  * @param null
     * @Description 从服务器获取全部用户信息的列表，解析信息格式，列出所有用户
     * @Return
     * @Since 2020/6/6
     */
    public void getUserList(String content) {
        String name;
        String id;
        Pattern numPattern;
        Matcher numMatcher = null;
        Pattern userListPattern = null;

        if (content.length() > 0) {
            numPattern = Pattern.compile("<user>(.*?)</user>");
            numMatcher = numPattern.matcher(content);
            //遍历字符串，进行正则匹配，获取所有用户信息
            while (numMatcher.find()) {
                String detail = numMatcher.group(1);
                userListPattern = Pattern.compile("<id>(.*)</id><username>(.*)</username>");
                Matcher userListmatcher = userListPattern.matcher(detail);
                if (userListmatcher.find()) {
                    name = userListmatcher.group(2);
                    id = userListmatcher.group(1);
                    if ("0".equals(id)) {
                        name = "[管理员]" + name;
                        users_map.put(name, Integer.parseInt(id));
                    } else {
                        name = "[用户" + id + "]" + name;
                        users_map.put(name, Integer.parseInt(id));
                    }
                    users_model.addElement(name);
                }
            }
            users_model.removeElementAt(0);
        }
        users_label.setText("聊天室内人数：" + users_map.size());
    }


    /**
     * @MethodName askBuildPrivateChat
     * @Params  * @param null
     * @Description 处理某用户对当前客户端的用户的私聊请求
     * @Return
     * @Since 2020/6/6
     */
    public void askBuildPrivateChat(String msg) {
        Pattern pattern = Pattern.compile("<from>(.*)</from><id>(.*)</id>");
        Matcher matcher = pattern.matcher(msg);
        if (matcher.find()) {
            String toPrivateChatName = matcher.group(1);
            String toPrivateChatId = matcher.group(2);
            int option = JOptionPane.showConfirmDialog(frame, "[" + toPrivateChatName + "]想与你私聊，是否同意？", "提示",
                    JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                sendMsg("agreePrivateChar", toPrivateChatId);
                privateChatFrame chatFrame = new privateChatFrame("与[" + toPrivateChatName + "]的私聊窗口", toPrivateChatName, toPrivateChatId);
                privateChatFrameMap.put(toPrivateChatId, chatFrame);
            } else {
                sendMsg("refusePrivateChar", toPrivateChatId);
            }
        }
    }


    /**
     * @MethodName startOrStopHisPrivateChat
     * @Params  * @param null
     * @Description 获取请求指定用户的私聊请求的结果，同意就开启私聊窗口，拒绝就提示。
     * @Return
     * @Since 2020/6/6
     */
    public void startOrStopHisPrivateChat(String msg) {
        Pattern pattern = Pattern.compile("<result>(.*)</result><from>(.*)</from><id>(.*)</id>");
        Matcher matcher = pattern.matcher(msg);
        if (matcher.find()) {
            String result = matcher.group(1);
            String toPrivateChatName = matcher.group(2);
            String toPrivateChatId = matcher.group(3);
            if ("1".equals(result)) {  //对方同意的话
                if ("0".equals(toPrivateChatId)) {
                    toPrivateChatName = "[管理员]" + toPrivateChatName;
                }
                privateChatFrame chatFrame = new privateChatFrame("与[" + toPrivateChatName + "]的私聊窗口", toPrivateChatName, toPrivateChatId);
                privateChatFrameMap.put(toPrivateChatId, chatFrame);
            } else if ("0".equals(result)) {
                JOptionPane.showMessageDialog(frame, "[" + toPrivateChatName + "]拒绝了你的私聊请求", "失败", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    /**
     * @MethodName giveMsgToPrivateChat
     * @Params  * @param null
     * @Description 根据服务器端发来的用户ID和内容，搜寻当前客户端的用户中对应传来的用户ID的私聊窗口，将内容写进私聊窗口的文本域
     * @Return
     * @Since 2020/6/6
     */
    public void giveMsgToPrivateChat(String msg) {
        Pattern privatePattern = Pattern.compile("<msg>(.*)</msg><id>(.*)</id>");
        Matcher privateMatcher = privatePattern.matcher(msg);
        if (privateMatcher.find()) {
            String toPrivateMsg = privateMatcher.group(1);
            String toPrivateId = privateMatcher.group(2);
            privateChatFrame chatFrame = privateChatFrameMap.get(toPrivateId);
            insertMessage(chatFrame.textScrollPane, chatFrame.msgArea, null, df.format(new Date()) + " 对方说：", " "+toPrivateMsg, chatFrame.vertical, false);
        }
    }


    /**
     * @MethodName endPrivateChat
     * @Params  * @param null
     * @Description 结束指定id用户的私聊窗口
     * @Return
     * @Since 2020/6/6
     */
    public void endPrivateChat(String msg) {
        Pattern privatePattern = Pattern.compile("<id>(.*)</id>");
        Matcher privateMatcher = privatePattern.matcher(msg);
        if (privateMatcher.find()) {
            String endPrivateId = privateMatcher.group(1);
            privateChatFrame chatFrame = privateChatFrameMap.get(endPrivateId);
            JOptionPane.showMessageDialog(frame, "由于对方结束了私聊，该私聊窗口即将关闭！", "提示", JOptionPane.WARNING_MESSAGE);
            chatFrame.dispose();
            insertMessage(sysTextScrollPane, sysMsgArea, null, "[系统消息] " + df.format(new Date()), "由于[" + chatFrame.otherName + "]关闭了私聊窗口，私聊结束！", sysVertical, true);
            privateChatFrameMap.remove(endPrivateId);
        }
    }


    /**
     * @ClassName privateChatFrame
     * @Params  * @param null
     * @Description 私聊窗口GUI的内部类
     * @Return
     * @Since 2020/6/6
     */
    private class privateChatFrame extends JFrame {
        private String otherName;
        private String otherId;
        private JButton sendButton;
        private JTextField msgTestField;
        private JTextPane msgArea;
        private JScrollPane textScrollPane;
        private JScrollBar vertical;

        public privateChatFrame(String title, String otherName, String otherId) throws HeadlessException {
            super(title);
            this.otherName = otherName;
            this.otherId = otherId;
            //全局面板容器
            JPanel panel = new JPanel();
            //全局布局
            BorderLayout layout = new BorderLayout();

            JPanel headpanel = new JPanel();    //上层panel，
            JPanel footpanel = new JPanel();    //下层panel
            JPanel centerpanel = new JPanel(); //中间panel

            //头部布局
            FlowLayout flowLayout = new FlowLayout();
            //底部布局
            GridBagLayout gridBagLayout = new GridBagLayout();

            setSize(600, 500);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            setContentPane(panel);
            setLayout(layout);

            headpanel.setLayout(flowLayout);
            footpanel.setLayout(gridBagLayout);
            footpanel.setPreferredSize(new Dimension(0, 40));
            centerpanel.setLayout(gridBagLayout);

            //添加头部部件
            JLabel Name = new JLabel(otherName);
            headpanel.add(Name);

            //设置底部布局
            sendButton = new JButton("发送");
            sendButton.setPreferredSize(new Dimension(40, 0));
            msgTestField = new JTextField();
            footpanel.add(msgTestField, new GridBagConstraints(0, 0, 1, 1, 100, 100,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
            footpanel.add(sendButton, new GridBagConstraints(1, 0, 1, 1, 10, 10,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));

            //中间布局
            msgArea = new JTextPane();
            msgArea.setEditable(false);
            textScrollPane = new JScrollPane();
            textScrollPane.setViewportView(msgArea);
            vertical = new JScrollBar(JScrollBar.VERTICAL);
            vertical.setAutoscrolls(true);
            textScrollPane.setVerticalScrollBar(vertical);
            centerpanel.add(textScrollPane, new GridBagConstraints(0, 0, 1, 1, 100, 100,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

            //设置顶层布局
            panel.add(headpanel, "North");
            panel.add(footpanel, "South");
            panel.add(centerpanel, "Center");

            // 监听
            listener2();

            //窗口显示
            setVisible(true);
        }

        private void listener2(){
            //窗口关闭事件
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    int option = JOptionPane.showConfirmDialog(e.getOppositeWindow(), "确定结束私聊？", "提示",
                            JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        if (receive != null) {
                            sendMsg("privateExit", otherId); //关闭当前私聊连接
                        }
                        insertMessage(sysTextScrollPane, sysMsgArea, null, "[系统消息] " + df.format(new Date()), "您与[" + otherName + "]的私聊结束", sysVertical, true);
                        dispose();
                        privateChatFrameMap.remove(otherId);
                    }
                }
            });

            //聊天信息输入框的监听回车按钮事件
            msgTestField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                        if (receive == null) {
                            JOptionPane.showMessageDialog(frame, "请先连接聊天室的服务器！", "提示", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        String text = msgTestField.getText();
                        if (text != null && !"".equals(text)) {
                            sendMsg("privateMsg", "<msg>" + text + "</msg><id>" + otherId + "</id>");
                            msgTestField.setText("");
                            insertMessage(textScrollPane, msgArea, null, df.format(new Date()) + " 你说：", " "+text, vertical, false);
                        }
                    }
                }
            });
            sendButton.addActionListener(e -> {
                String cmd = e.getActionCommand();
                if ("发送".equals(cmd)) {
                    if (receive == null) {
                        JOptionPane.showMessageDialog(frame, "请先连接聊天室的服务器！", "提示", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    String text = msgTestField.getText();
                    if (text != null && !"".equals(text)) {
                        sendMsg("privateMsg", "<msg>" + text + "</msg><id>" + otherId + "</id>");
                        msgTestField.setText("");
                        insertMessage(textScrollPane, msgArea, null, df.format(new Date()) + " 你说：", " "+text, vertical, false);
                    }
                }
            });
        }
    }
}
