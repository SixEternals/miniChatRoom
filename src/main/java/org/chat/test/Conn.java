package org.chat.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Properties;

public class Conn{
    private static Connection con;

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

    public static void executeTest() throws SQLException {
        // 执行SQL对象statement
        Statement s = con.createStatement();

        // 返回结果集
        String sql = "SELECT u_ID FROM user";
        ResultSet rs = s.executeQuery(sql);
        while(rs.next()) {
            System.out.println(rs.getInt("u_ID"));
        }
    }

    public static void main(String[] args) throws SQLException {
        Conn c = new Conn();
        c.getConnection();
        executeTest();
    }
}
