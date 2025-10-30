package com.briup.smart.env.main;

import com.briup.smart.env.client.Gather;
import com.briup.smart.env.client.GatherImpl;

//客户端入口
public class ClientMain {
    public static void main(String[] args) {
        Gather gt = new GatherImpl();
        try {
            //采集数据
            gt.gather();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
