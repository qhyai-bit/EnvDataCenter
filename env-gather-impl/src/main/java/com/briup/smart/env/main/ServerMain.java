package com.briup.smart.env.main;

import com.briup.smart.env.server.Server;
import com.briup.smart.env.server.ServerImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        //1.启动服务器，接收客户端发送过来数据并入库
        Server server = new ServerImpl();

        //2.启动监控服务器【8888】,接受客户端的连接，
        //一旦有客户端连接上来，则关闭9999服务器
        new Thread() {
            @Override
            public void run() {
                ServerSocket  serverSocket = null;
                Socket socket = null;
                try {
                    serverSocket = new ServerSocket(8888);
                    System.out.println("监控服务器启动成功,port: " + 8888 + ",等待客户端连接...");
                    socket = serverSocket.accept();
                    System.out.println("客户端成功连接, socket: " + socket);
                    server.shutdown();
                    System.out.println("成功关闭 9999 服务器！");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(serverSocket != null) {
                        try {
                            serverSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();

        //3.接受采集客户端发送的数据
        server.receive();
        System.out.println("服务器应用程序，正常退出!");
    }
}
