package com.briup.smart.env.main;

import com.briup.smart.env.ConfigurationImpl;
import com.briup.smart.env.server.ServerImpl;
import com.briup.smart.env.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        ConfigurationImpl conf = ConfigurationImpl.getInstance();
        Log log = conf.getLogger();
        //1.启动服务器，接收客户端发送过来数据并入库
        ServerImpl server = (ServerImpl) conf.getServer();

        //2.启动监控服务器【8888】,接受客户端的连接，
        //一旦有客户端连接上来，则关闭9999服务器
        new Thread() {
            @Override
            public void run() {
                ServerSocket  serverSocket = null;
                Socket socket = null;
                try {
                    int listenPort = server.getListenPort();
                    serverSocket = new ServerSocket(listenPort);
                    log.info("监控服务器启动成功,port: " + listenPort + ",等待客户端连接...");
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
