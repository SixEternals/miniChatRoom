package org.chat.server;

import java.sql.ResultSet;
import java.sql.SQLException;
public class registerVerifyAcount {
    ServerRepository serverRepository = new ServerRepository();
    public registerVerifyAcount() {
        serverRepository = new ServerRepository();
        serverRepository.getConnection(); // 获取连接
    }

    // 客户端注册时 由客户端把账号字符串发过来
    // 在服务器进行对比 如果是已经存在的账号则不予通过
    // 否则新建账号
    // 返回true说明账号合法 运行创建
    public boolean isNewAccount(int acc){
        try {
            serverRepository.getConnection();
            ResultSet rs = ServerRepository.getAllU_ID();

            while(rs.next()){
                int id = rs.getInt("u_ID");
//                System.out.println(id);
                if(id == acc) {
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    // 检查账号密码
    public boolean isExistedAccount(String input_uid,String input_pwd) throws SQLException {
        return ServerRepository.isLoginBy_Id_Pwd(input_uid,input_pwd);
    }
    public static void main(String[] args) throws SQLException {
        registerVerifyAcount e = new registerVerifyAcount();
//        boolean flag = e.isNewAccount(1234);
//        System.out.println(flag);
        boolean flag2 = e.isExistedAccount("1234","2345");
        System.out.println("flag2 = " + flag2);
    }

}
