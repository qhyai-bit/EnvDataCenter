package com.briup.smart.env.main;

import com.briup.smart.env.server.Server;
import com.briup.smart.env.server.ServerImpl;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        //1.启动服务器，接收客户端发送过来数据并入库
        Server server = new ServerImpl();
        server.receive();
        System.out.println("服务器应用程序，正常退出!");
    }
}
