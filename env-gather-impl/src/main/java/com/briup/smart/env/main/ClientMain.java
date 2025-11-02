package com.briup.smart.env.main;

import com.briup.smart.env.client.*;
import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.util.Log;
import com.briup.smart.env.util.LogImpl;

import java.util.Collection;

//客户端主方法
public class ClientMain {
    private static final Log log = new LogImpl();
    public static void main(String[] args) throws Exception {
        //1.实例化采集模块对象，调用采集方法，实现采集功能
        Gather gt = new GatherBackupImpl();
        Collection<Environment> envs = gt.gather();
        //输出集合元素个数
        log.info("元素数量: " + envs.size());

        //2.发送集合对象 到 服务器
        Client client = new ClientImpl();
        client.send(envs);
        log.info("客户端成功关闭!");
    }
}
