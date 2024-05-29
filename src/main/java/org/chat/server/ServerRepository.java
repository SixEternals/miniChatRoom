package org.chat.server;

import org.chat.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 功能：访问数据库
 * 主体：Server服务器端
 */
public class ServerRepository {
    private static String mysqlname = null;
    private static String password = null;
    private static String URL = null;

    private static Connection con;
    public ServerRepository() {
    }

    /**
     * 初始化url password name
     */
    public Connection getConnection(){
        try{// 加载数据库驱动类
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("数据库驱动加载成功");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        // 读取数据文件
        Properties properties = new Properties();
        try(FileInputStream in = new FileInputStream("src\\main\\resources\\mysqlConfig.properties")){
            properties.load(in);
            try{ // 关闭in流
            }finally {
                in.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        String url = properties.getProperty("usersURL");
        String name = properties.getProperty("name");
        String password = properties.getProperty("password");

        // 访问数据库
        try{
             con = DriverManager.getConnection("jdbc:mysql://localhost:13306/users?useUnicode=true&characterEncoding=UTF8", name,password );
//            con = DriverManager.getConnection(url,name,password);
            System.out.println("数据库连接成功");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return con;
    }

    /**
     * 读写用户信息操作
     * add user
     */
    public static void addUser(User user){
        // SQL insert
        String sql = "INSERT INTO user (u_ID, u_name, u_password, u_gender, u_Bio, u_registrationDate, u_lastLogin, u_status) " +
             "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement preparedStatement = con.prepareStatement(sql)){
            preparedStatement.setString(1, user.getU_ID());
            preparedStatement.setString(2, user.getU_name());
            preparedStatement.setString(3, user.getU_password());
            preparedStatement.setString(4, user.getU_gender());
            preparedStatement.setString(5, user.getU_Bio());
            preparedStatement.setTimestamp(6, user.getU_registrationDate());
            preparedStatement.setTimestamp(7, user.getU_lastLogin());
            preparedStatement.setString(8, user.getU_status());

        // 执行插入操作
        int result = preparedStatement.executeUpdate();
        if (result > 0) {
            System.out.println("用户添加成功");
        } else {
            System.out.println("用户添加失败");
        }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    /**
     * 获取所有用户信息
     * 返回数字列表
     */
    public static ResultSet getAllU_ID() throws SQLException {
        Statement s = con.createStatement();
        // 获取结果集
        String sql = "SELECT u_ID FROM user";
        ResultSet rs = s.executeQuery(sql);
        return rs;
    }

    /**
     * 验证账号密码
     * @param
     */
    public static boolean isLoginBy_Id_Pwd(String input_ID, String input_pwd) throws SQLException {
        Statement s = con.createStatement();
        String sql = "SELECT u_ID, u_password FROM user";
        ResultSet rs = s.executeQuery(sql);

        while (rs.next()) {
//            System.out.print("u_ID:" + rs.getInt("u_ID"));
//            System.out.println(" u_password:" + rs.getString("u_password"));

            if(rs.getString("u_ID").equals(input_ID)){
                if(rs.getString("u_password").equals(input_pwd)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 建号时检测账号适合存在
     * @param acc 输入注册时写入的账号
     * 返回这个账号是否在数据库中存在
     * @throws SQLException
     */
    public static boolean isExistedAccount(String acc) throws SQLException {
        ResultSet rs;
        Statement s = con.createStatement();
        String sql = "SELECT u_ID FROM user";
        rs = s.executeQuery(sql);

        while(rs.next()){
            if(rs.getString("u_ID").equals(acc)){
                return true;
            }
        }
        return false;
    }

    /**
     * 登陆时检查账号密码的正确与否
     * @param uid:账号, u_pwd:输入的 密码
     * @return
     */
    public static boolean checkLoginAccount_pwd(String uid,String u_pwd) throws SQLException {
        Statement s = con.createStatement();
        String sql = "SELECT u_ID, u_password FROM user";
        ResultSet rs = s.executeQuery(sql);

        while(rs.next()){
            if(rs.getString("u_ID").equals(uid) && rs.getString("u_password").equals(u_pwd)){
                return true;
            }
        }
        return false;
    }

    // 测试
    private void test(){
        try {
            boolean flag = isExistedAccount("23871266");
            System.out.println(flag);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public static void main(String[] args) throws SQLException {
        ServerRepository s = new ServerRepository();
        s.getConnection();
//        boolean flag = isLoginBy_Id_Pwd("1234","2345");
//        System.out.println(flag);

        s.test();

    }
}
