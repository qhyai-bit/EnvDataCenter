package com.briup.smart.env.main;

import com.briup.smart.env.server.Server;
import com.briup.smart.env.server.ServerImpl;
import com.briup.smart.env.util.Log;
import com.briup.smart.env.util.LogImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    private static final Log log = new LogImpl();
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
                    log.info("监控服务器启动成功,port: " + 8888 + ",等待客户端连接...");
                    socket = serverSocket.accept();
                    log.info("客户端成功连接, socket: " + socket);
                    server.shutdown();
                    log.info("成功关闭 9999 服务器！");
                } catch (Exception e) {
                    log.error(e.getMessage());
                } finally {
                    if(socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            log.error(e.getMessage());
                        }
                    }
                    if(serverSocket != null) {
                        try {
                            serverSocket.close();
                        } catch (IOException e) {
                            log.error(e.getMessage());
                        }
                    }
                }
            }
        }.start();

        //3.接受采集客户端发送的数据
        server.receive();
        log.info("服务器应用程序，正常退出!");
    }
}
