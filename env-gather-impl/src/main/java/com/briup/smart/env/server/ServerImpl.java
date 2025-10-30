package com.briup.smart.env.server;

import com.briup.smart.env.entity.Environment;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;

public class ServerImpl implements Server{
    private int port = 9999;
    private ServerSocket serverSocket;
    Socket socket = null;
    ObjectInputStream ois = null;
    //单线程服务器
    @Override
    public void receive() {
        //1.搭建TCP服务器，接收客户端发送过来的集合对象
        try {
            // 1.搭建服务端
            serverSocket = new ServerSocket(port);
            System.out.println("网络模块服务端启动成功,port: "+port+",等待客户端连接...");

            // 2.接收客户端的连接
            socket = serverSocket.accept();
            System.out.println("客户端成功连接,socket: " + socket);

            // 3.准备对象流
             ois = new ObjectInputStream(socket.getInputStream());

            // 4.读取集合对象
            Collection<Environment> coll = (Collection<Environment>) ois.readObject();
            System.out.println("成功接收到集合对象,内含环境数据个数: "+coll.size());

            //未完待续...
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("服务器即将关闭...");
            // 5.关闭资源
            if (ois != null){
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 关闭服务器方法 暂不考虑实现
    @Override
    public void shutdown() {

    }
}
