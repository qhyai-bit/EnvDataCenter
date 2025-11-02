package com.briup.smart.env.server;

import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.util.Log;
import com.briup.smart.env.util.LogImpl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;

public class ServerImpl implements Server{
    private static final Log log = new LogImpl();
    //新增属性：服务器起始状态(默认未关闭)
    private boolean isStop = false;
    private int port = 9999;
    private ServerSocket serverSocket;
    Socket socket = null;
    ObjectInputStream ois = null;
    //多线程服务器
    @Override
    public void receive() {
        //1.搭建TCP服务器，接收客户端发送过来的集合对象
        try {
            // 1.搭建服务端
            serverSocket = new ServerSocket(port);
            log.info("网络模块服务端启动成功,port: "+port+",等待客户端连接...");
            while (!isStop) {
                // 2.接收客户端的连接
                socket = serverSocket.accept();
                log.info("成功接收来自客户端的连接,socket: " + socket);

                // 3.分离子线程为客户端提供服务
                Thread th = new Thread(){
                    @Override
                    public void run() {
                    try {
                        // 3.准备对象流
                        ois = new ObjectInputStream(socket.getInputStream());

                        // 4.读取集合对象
                        Collection<Environment> coll = (Collection<Environment>) ois.readObject();
                        log.info("成功接收集合对象,内含环境数据个数: " + coll.size());

                        //后续 入库代码
                        DBStore dbStore = new DBStoreBackupImpl();
                        dbStore.saveDB(coll);

                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }finally {
                        log.info("连接客户端" + socket + " 已关闭...");
                        // 5.关闭资源
                        if (ois != null){
                            try {
                                ois.close();
                            } catch (IOException e) {
                                log.error(e.getMessage());
                            }
                        }
                        if (socket != null){
                            try {
                                socket.close();
                            } catch (IOException e) {
                                log.error(e.getMessage());
                            }
                        }
                    }
                    }
                };
                th.start();
            }
            log.info("服务端网络模块: receive执行完毕");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    // 关闭服务器方法 暂不考虑实现
    @Override
    public void shutdown() throws IOException {
        isStop = true;
        log.info("服务端网络模块: shutdown");
        if (serverSocket != null)
            serverSocket.close();
        log.info("服务端网络模块: shutdown执行完毕");
    }
}
