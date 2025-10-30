package com.briup.smart.env.main;

import com.briup.smart.env.client.Client;
import com.briup.smart.env.client.ClientImpl;
import com.briup.smart.env.client.Gather;
import com.briup.smart.env.client.GatherImpl;
import com.briup.smart.env.entity.Environment;

import java.util.Collection;

//客户端主方法
public class ClientMain {
    public static void main(String[] args) throws Exception {
        //1.实例化采集模块对象，调用采集方法，实现采集功能
        Gather gt = new GatherImpl();
        Collection<Environment> envs = gt.gather();
        //输出集合元素个数
        System.out.println("元素数量: " + envs.size());

        //2.发送集合对象 到 服务器
        Client client = new ClientImpl();
        client.send(envs);
        System.out.println("客户端成功关闭!");
    }
}
