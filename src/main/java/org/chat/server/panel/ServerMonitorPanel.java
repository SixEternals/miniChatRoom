package org.chat.server.panel;

import org.chat.Threads.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.chat.Threads.Channel;

public class ServerMonitorPanel extends JFrame{
    protected CopyOnWriteArrayList<Channel> allUserChannel;
    /*以下为窗口参数*/
    private JFrame frame;
    // TOP顶部部参数
    private JTextField port_textfield;
    private JTextField name_textfield;
    private JButton head_connect;
    private JButton head_exit;
    private int port;
    //底部参数
    private JTextField text_field;
    private JTextField sysText_field;
    private JButton foot_send;
    private JButton foot_sysSend;
    private JButton foot_userClear;

    //右边参数
    protected JLabel users_label;
    private JButton privateChat_button;
    private JButton kick_button;
    private JList<String> userlist;
    protected DefaultListModel<String> users_model;

    //左边参数
    private JScrollPane sysTextScrollPane;
    private JTextPane sysMsgArea;
    private JScrollBar sysVertical;

    //中间参数
    private JScrollPane userTextScrollPane;
    private JTextPane userMsgArea;
    private JScrollBar userVertical;


    //时间格式化工具类
    static private SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置时间

    //用户自增ID
    protected int userId = 1000;
    //服务器管理员名字
    private String adminName;

    //服务器线程
    protected ServerSocket serverSocket;

    //服务器线程
    private Server server;

    // todo:管理员的私聊窗口队列和线程队列
//    private HashMap<String, privateChatFrame> adminPrivateQueue;
    private HashMap<String, Channel> adminPrivateThread;

    public ServerMonitorPanel(){
        init();
        listener();
    }


    public static void main(String[] args) {
        new ServerMonitorPanel();
    }


    public void init() {
        allUserChannel = new CopyOnWriteArrayList<>();
        // todo
//        adminPrivateQueue = new HashMap<>();
        adminPrivateThread = new HashMap<>();
        // todo
//        setUIStyle();

        frame = new JFrame("Hcode聊天室服务器");
        JPanel panel = new JPanel();        /*主要的panel，上层放置连接区，下层放置消息区，
                                                  中间是消息面板，左边是room列表，右边是当前room的用户列表*/
        JPanel headpanel = new JPanel();    /*上层panel，用于放置连接区域相关的组件*/
        JPanel footpanel = new JPanel();    /*下层panel，用于放置发送信息区域的组件*/
        JPanel centerpanel = new JPanel();    /*中间panel，用于放置聊天信息*/
        JPanel leftpanel = new JPanel();    /*左边panel，用于放置房间列表和加入按钮*/
        JPanel rightpanel = new JPanel();   /*右边panel，用于放置房间内人的列表*/

        /*最上层的布局，分中间，东南西北五个部分*/
        BorderLayout layout = new BorderLayout();
        /*格子布局，主要用来设置西、东、南三个部分的布局*/
        GridBagLayout gridBagLayout = new GridBagLayout();
        /*主要设置北部的布局*/
        FlowLayout flowLayout = new FlowLayout();
        /*设置初始窗口的一些性质*/
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setContentPane(panel);
        frame.setLayout(layout);
        /*设置各个部分的panel的布局和大小*/
        headpanel.setLayout(flowLayout);
        footpanel.setLayout(gridBagLayout);
        leftpanel.setLayout(gridBagLayout);
        centerpanel.setLayout(gridBagLayout);
        rightpanel.setLayout(gridBagLayout);
        //设置面板大小
        leftpanel.setPreferredSize(new Dimension(350, 0));
        rightpanel.setPreferredSize(new Dimension(155, 0));
        footpanel.setPreferredSize(new Dimension(0, 40));

        //头部布局
        port_textfield = new JTextField("8888");
        name_textfield = new JTextField("匿名");
        port_textfield.setPreferredSize(new Dimension(70, 25));
        name_textfield.setPreferredSize(new Dimension(150, 25));

        JLabel port_label = new JLabel("端口号:");
        JLabel name_label = new JLabel("管理员:");

        head_connect = new JButton("启动");
        head_exit = new JButton("关闭");

        headpanel.add(port_label);
        headpanel.add(port_textfield);
        headpanel.add(name_label);
        headpanel.add(name_textfield);
        headpanel.add(head_connect);
        headpanel.add(head_exit);

        //底部布局
        foot_send = new JButton("发送聊天信息");
        foot_sysSend = new JButton("发送系统消息");
        foot_sysSend.setPreferredSize(new Dimension(110, 0));
        foot_userClear = new JButton("清空聊天消息");
        foot_userClear.setPreferredSize(new Dimension(148, 0));

        sysText_field = new JTextField();
        sysText_field.setPreferredSize(new Dimension(230, 0));
        text_field = new JTextField();
        footpanel.add(sysText_field, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        footpanel.add(foot_sysSend, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 3), 0, 0));
        footpanel.add(text_field, new GridBagConstraints(2, 0, 1, 1, 100, 100,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        footpanel.add(foot_send, new GridBagConstraints(3, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));
        footpanel.add(foot_userClear, new GridBagConstraints(4, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));


        //左边布局
        JLabel sysMsg_label = new JLabel("系统日志：");
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

        users_label = new JLabel("当前连接用户：0");
        privateChat_button = new JButton("私聊");
        kick_button = new JButton("踢出");
        users_model = new DefaultListModel<>();
        userlist = new JList<String>(users_model);
        JScrollPane userListPane = new JScrollPane(userlist);

        rightpanel.add(users_label, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        rightpanel.add(privateChat_button, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        rightpanel.add(kick_button, new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        rightpanel.add(userListPane, new GridBagConstraints(0, 3, 1, 1, 100, 100,
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

        //设置顶层布局
        panel.add(headpanel, "North");
        panel.add(footpanel, "South");
        panel.add(leftpanel, "West");
        panel.add(rightpanel, "East");
        panel.add(centerpanel, "Center");

        frame.setVisible(true);

        String name = JOptionPane.showInputDialog("请输入本聊天室管理员昵称：");
        if (name != null &&!name.equals("")) {
            name_textfield.setText(name);
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void listener(){
        // 关闭时间监听
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(frame, "确定关闭聊天室界面?", "提示",
                        JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    if (e.getWindow() == frame) {
                        frame.dispose();
                        System.exit(0);
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
        });
        //聊天信息输入框的监听回车按钮事件
        text_field.addKeyListener(new KeyAdapter() {

        });
        // 系统信息输入框的回车监控事件
        sysText_field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {

            }
        });

        // 顶部的启动按钮：启动连接
        head_connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String strPort = port_textfield.getText();
                // 先检查ip port
                if(!ipCheckPort(strPort)){
                    JOptionPane.showMessageDialog(frame, "请使用0-65535的整数作为端口号！", "失败", JOptionPane.ERROR_MESSAGE);
                }
                port = Integer.parseInt(strPort);

                if(!head_connect.getText().equals("已开启")) {
                    try {
                        server = new Server(new ServerSocket(port));

                        head_connect.setText("已开启");
                        head_exit.setText("关闭");

//                        (new Thread(server)).start(); todo:疑似这里有问题

                        adminName = name_textfield.getText();
                        name_textfield.setEditable(false);
                        port_textfield.setEditable(false);
                    } catch (IOException portException) {
                        // 端口被占用
                        System.out.println("Error from ServerMonitorPanel,端口号被占用");
                        JOptionPane.showMessageDialog(frame, "开启服务端失败！服务端端口被占用，请更换端口号！", "失败", JOptionPane.ERROR_MESSAGE);
                    }
                }else{
                    // todo: 切换开关
                    JOptionPane.showMessageDialog(frame, "服务已开启，想关闭请关闭窗口");
                }

            }
        });

        // 顶部的关闭按钮
        head_exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(server == null){
                    JOptionPane.showMessageDialog(frame, "不能关闭，因为未开启服务器！", "错误", JOptionPane.ERROR_MESSAGE);
                }else{
                    try{
                        serverSocket.close();
                    }catch (IOException exitE){
                        System.out.println("错误！服务器关闭失败！");
                    }
                    head_connect.setText("启动");
                    head_exit.setText("已关闭");
                    port_textfield.setEditable(true);
                    name_textfield.setEditable(true);
                    for(Channel channel : allUserChannel){
                        channel.release();
                    }
                    server = null;
                    users_model.removeAllElements();
                    System.out.println("系统已关闭");
                    JOptionPane.showMessageDialog(frame, "服务器已关闭！");
                }
            }
        });

        // 右侧：私聊

        // 右侧：踢出

        // 底部：发送系统消息

        // 底部：发送聊天信息

        // 底部：清空聊天消息

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

}
