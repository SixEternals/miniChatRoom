package org.chat.panel;

import org.chat.Utils.CloseUtils;
import org.chat.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Channel;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerMonitorPanel extends JFrame{
    private CopyOnWriteArrayList<Channel> allUserChannel;
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
    private JLabel users_label;
    private JButton privateChat_button;
    private JButton kick_button;
    private JList<String> userlist;
    private DefaultListModel<String> users_model;

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
    private int userId = 1000;
    //服务器管理员名字
    private String adminName;

    //服务器线程
    private ServerSocket serverSocket;

    //服务器线程
    // todo
//    private Server server;

    //管理员的私聊窗口队列和线程队列
    // todo
//    private HashMap<String, privateChatFrame> adminPrivateQueue;
    private HashMap<String, Channel> adminPrivateThread;

    public ServerMonitorPanel(){
        init();
        listener();
    }


    public static void main(String[] args) {
        new ServerMonitorPanel();
    }

    /**
     * @MethodName init   GUI初始化，初始化各种监听事件
     * @Params  * @param null
     * @Return null
     * @Since 2020/6/6
     */
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
        //聊天信息输入框的监听回车按钮事件
        text_field.addKeyListener(new KeyAdapter() {

        });
        // 系统信息输入框的回车监控事件
        sysText_field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {

            }
        });

        head_connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    // Server部分
    private class Server implements Runnable{
        private Server(ServerSocket socket){
            serverSocket = socket;
        }

        @Override
        public void run(){
            while(true){
                Socket client = null;
                try{
                    client = serverSocket.accept();
                    userId++;
                    User user = new User();
                    // todo:user初始化

                }catch (IOException e){
                    //关闭不处理
                    System.out.println("1名用户退出");
                    break;
                }
            }
        }
    }

    protected class Channel implements Runnable{
        private boolean isRunning;
        private CopyOnWriteArrayList<Channel> shieldList; // 用户列表
        private User user;
        private DataInputStream dis;
        private DataOutputStream dos;
        private Socket socket;
        public Channel(User user){

        }

        public User getUser(){
            return user;
        }

        public CopyOnWriteArrayList<Channel> getShieldList(){
            return shieldList;
        }

        private String receive(){
            String msg = "";
            try{
                msg = this.dis.readUTF();
            }catch (IOException e){
                this.release();
            }
            return msg;
        }


        public void release(){
            this.isRunning = false;
            CloseUtils.close(dis, dos, socket);

            // 列表中移除该用户
            users_model.removeElement(user.getU_ID() + "#@" + user.getU_name());
            if(allUserChannel.contains(this)){
                allUserChannel.remove(this);
            }
            users_label.setText("当前连接用户:" + allUserChannel.size());
        }

        @Override
        public void run(){
            while(isRunning){
                String msg = receive();
                if(!msg.equals("")){
                    // todo:parseMsg(msg)
                    // todo:将信息广播给其他人
                    if(msg.length() > 0){

                        // 遍历所有用户
                        for(Channel channel : allUserChannel){
                            User targetUser = channel.getUser();
                            // todo:实际上这里可以处理广播，私聊，或其他聊天功能 不过先放着了

                            // 先做的广播
                            if(targetUser != null){
                                try {
                                    channel.dos.writeUTF(msg); // 发送消息
                                    channel.dos.flush(); // 刷新输出流保证成功发送消息
                                }catch (IOException e){
                                    // todo:记录日志或处理错误
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
