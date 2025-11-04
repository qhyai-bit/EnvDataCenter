package com.briup.smart.env.main;

import com.briup.smart.env.ConfigurationImpl;
import com.briup.smart.env.client.*;
import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.util.Log;

import java.util.Collection;

//客户端主方法
public class ClientMain {
    public static void main(String[] args) throws Exception {
        ConfigurationImpl conf = ConfigurationImpl.getInstance();
        Log log = conf.getLogger();
        //1.实例化采集模块对象，调用采集方法，实现采集功能
//        Gather gt = new GatherBackupImpl();
        Gather gt = conf.getGather();
        Collection<Environment> envs = gt.gather();
        //输出集合元素个数
        log.info("元素数量: " + envs.size());

        //2.发送集合对象 到 服务器
//        Client client = new ClientImpl();
        Client client = conf.getClient();
        client.send(envs);
        log.info("客户端成功关闭!");
    }
}
