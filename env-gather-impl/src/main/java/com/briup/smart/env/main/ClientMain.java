package com.briup.smart.env.main;

import com.briup.smart.env.client.Gather;
import com.briup.smart.env.client.GatherImpl;
import com.briup.smart.env.entity.Environment;

import java.util.Collection;

//客户端主方法
public class ClientMain {
    public static void main(String[] args) {
        //1.实例化采集模块对象，调用采集方法，实现采集功能
        Gather gt = new GatherImpl();
        Collection<Environment> envs = null;
        try {
            envs = gt.gather();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //输出集合元素个数
        System.out.println("元素数量: " + envs.size());
    }
}
