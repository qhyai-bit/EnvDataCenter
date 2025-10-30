package com.briup.smart.env.client;

import com.briup.smart.env.entity.Environment;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

/*
 * @Description: 添加采集模块接口的实现类
 * 解析数据文件，将数据封装成Environment对象，并添加到集合中，最后返回集合
 */
public class GatherImpl implements Gather{
    @Override
    public Collection<Environment> gather() throws Exception {
        //env-gather-impl\src\main\resources\data-file-simple
        File file = new File("env-gather-impl\\src\\main\\resources\\data-file-simple");
        System.out.println("文件是否存在: " + file.exists());
        return Collections.emptyList();
    }
}
