package com.briup.env.test;

import com.briup.smart.env.Configuration;
import com.briup.smart.env.ConfigurationImpl;
import org.junit.Test;

public class ConfigurationTest {
    @Test
    public void test() throws Exception {
        Configuration conf = ConfigurationImpl.getInstance();

        System.out.println("getLogger: " + conf.getLogger());

//        Log log = conf.getLogger();
//        log.info("测试日志模块");
    }
}
