package com.briup.smart.env.client;

import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.util.Log;
import com.briup.smart.env.util.LogImpl;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;

public class ClientImpl implements Client{
    private static final Log log = new LogImpl();
    @Override
    public void send(Collection<Environment> coll) {
        //准备服务器ip和port
        String host = "127.0.0.1";
        int port = 9999;
        //参数判断
        if (coll == null || coll.isEmpty()) {
            log.warn("客户端网络模块: 输入数据有误");
            return;
        }
        //搭建TCP客户端，然后发送collection集合对象到服务器
        Socket socket = null;
        ObjectOutputStream oos = null;
        try {
            //1.搭建客户端，连接到服务器
            socket = new Socket(host,port);
            log.info("客户端网络模块: 连接成功");

            //2.获取IO流
            oos = new ObjectOutputStream(socket.getOutputStream());
            log.info("客户端网络模块: 数据准备完毕");

            //3.发送集合对象
            oos.writeObject(coll);
            log.info("客户端网络模块: 数据发送成功,共" + coll.size() + "条");

        } catch (IOException e) {
            log.error("客户端网络模块: 错误信息: " + e.getMessage());
        } finally {
            //4.关闭资源
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    log.error("客户端网络模块: 错误信息: " + e.getMessage());
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    log.error("客户端网络模块: 错误信息: " + e.getMessage());
                }
            }
        }
    }
}
