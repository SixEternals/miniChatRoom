package org.chat.model;

import java.sql.Timestamp;

public class User {
    private String u_ID;// 唯一辨识符
    private String u_name;// 用户的登录名称
    private String u_password;
    public enum Gender{
        MALE,FEMALE,UNKNOWN;


        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }; // 只有三个选项
    private Gender u_gender;
    private String u_Bio; // 个人简介
    private Timestamp u_registrationDate; // 注册时间
    private Timestamp u_lastLogin; // 最后登陆系统的时间
    public enum Status{
        ONLINE,OFFLINE,INVISIBLE
    }; // 状态，在线 离线 隐身等等
    private Status u_status;

    /**
     * 注册时设置默认属性
     */
    public void setDefault(String acc, String pwd, Timestamp time){
        u_ID = acc;
        u_password = pwd;
        u_name = "默认用户";
        u_gender = Gender.UNKNOWN;
        u_Bio = "null";
        u_registrationDate = time;
        u_lastLogin = null;
        u_status = Status.OFFLINE;
    }

    public User(){}

    public String getU_gender() {
        return u_gender.toString();
    }

    public void setU_gender(Gender u_gender) {
        String str = "MALE";
        this.u_gender = Gender.valueOf(str);
        this.u_gender = u_gender;

    }

    public String getU_status() {
        return u_status.toString();
    }

    public void setU_status(Status u_status) {
        this.u_status = u_status;
    }

    public String getU_ID() {
        return u_ID;
    }

    public void setU_ID(String u_ID) {
        this.u_ID = u_ID;
    }

    public String getU_name() {
        return u_name;
    }

    public void setU_name(String u_name) {
        this.u_name = u_name;
    }

    public String getU_password() {
        return u_password;
    }

    public void setU_password(String u_password) {
        this.u_password = u_password;
    }

    public String getU_Bio() {
        return u_Bio;
    }

    public void setU_Bio(String u_Bio) {
        this.u_Bio = u_Bio;
    }

    public Timestamp getU_registrationDate() {
        return u_registrationDate;
    }

    public void setU_registrationDate(Timestamp u_registrationDate) {
        this.u_registrationDate = u_registrationDate;
    }

    public Timestamp getU_lastLogin() {
        return u_lastLogin;
    }

    public void setU_lastLogin(Timestamp u_lastLogin) {
        this.u_lastLogin = u_lastLogin;
    }

    public User(int uId, String uName) {
    }

    @Override
    public String toString() {
        return "User{" +
                "u_ID='" + u_ID + '\'' +
                ", u_name='" + u_name + '\'' +
                ", u_password='" + u_password + '\'' +
                ", u_gender=" + u_gender +
                ", u_Bio='" + u_Bio + '\'' +
                ", u_registrationDate=" + u_registrationDate +
                ", u_lastLogin=" + u_lastLogin +
                ", u_status=" + u_status +
                '}';
    }
}
