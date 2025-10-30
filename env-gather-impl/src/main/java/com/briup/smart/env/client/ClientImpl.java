package com.briup.smart.env.client;

import com.briup.smart.env.entity.Environment;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;

public class ClientImpl implements Client{
    @Override
    public void send(Collection<Environment> coll) {
        //准备服务器ip和port
        String host = "127.0.0.1";
        int port = 9999;
        //参数判断
        if (coll == null || coll.isEmpty()) {
            System.out.println("客户端网络模块: 接收的数据有误");
            return;
        }
        //搭建TCP客户端，然后发送collection集合对象到服务器
        Socket socket = null;
        ObjectOutputStream oos = null;
        try {
            //1.搭建客户端，连接到服务器
            socket = new Socket(host,port);
            System.out.println("客户端网络模块: 连接成功");

            //2.获取IO流
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("客户端网络模块: 准备发送数据");

            //3.发送集合对象
            oos.writeObject(coll);
            System.out.println("客户端网络模块: 数据发送成功,共" + coll.size() + "条");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //4.关闭资源
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
