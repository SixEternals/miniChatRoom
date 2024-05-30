链接
[Java连接mysql数据库方法及代码（jdbc）](https://blog.csdn.net/qq_52050769/article/details/118095034)

[面向对象方式重构聊天室案例 用到了多线程](https://github.com/CHANGEA-code/ChatDemo/blob/main/src/main/java/com/chase/client/WindowBuilder.java)

[utils怎么写](https://www.w3cschool.cn/article/30891882.html)

Java网络编程–TCP+多线程 实现聊天室群聊功能
功能描述：
简单的聊天室，可以有多个用户同时加入聊天，每个用户可以随时发送消息，其他用户都会收到该用户的信息（显示发送者姓名）

分析：
1.采用TCP实现，服务端需要完成的任务：

​ （1）为每个加入聊天室的用户创建一个服务线程，该线程用于接收该用户的信息并发送给其他用户

​ （2）用户加入和退出聊天室时给出相应的提示信息

​ （3）用户退出时，结束其服务线程

2.客户端需要随时接收其他用户的信息，同时也要发送信息，因此单独创建一个线程来完成接收信息的功能

3.为了保存所有加入了聊天室的用户的信息，创建一个public static的容器，存放用户信息

[在Java中操作Redis(详细--＞从环境配置到代码实现)](https://blog.csdn.net/m0_63144319/article/details/132266469)

[java 基于socket多线程的swing聊天室（完整版，附源码，带数据库存储）](https://blog.csdn.net/weixin_43476969/article/details/107012791)
