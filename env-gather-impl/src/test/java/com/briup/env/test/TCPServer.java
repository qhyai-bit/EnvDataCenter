package com.briup.env.test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    public static void main(String[] args) {
        int port = 8888;
        ServerSocket server = null;
        Socket socket = null;
        ObjectInputStream ois = null;

        try {
            server = new ServerSocket(port);
            System.out.println("服务器启动,监听端口：" + port + ",等待客户端的连接");

            //如果有客户端成功连接，则返回 通信socket对象；如果没有客户端连接，则阻塞
            socket = server.accept();
            System.out.println("服务器接收到客户端的连接: " + socket);

            //从socket中获取输入流，用对象流进行包装
            ois = new ObjectInputStream(socket.getInputStream());

            //读取客户端发送过来的 Student对象
            Student stu = (Student)ois.readObject();
            System.out.println("接收到的对象是:"+stu);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //关闭资源
            if(ois != null){
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
