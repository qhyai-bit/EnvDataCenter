package com.briup.smart.env.client;

import com.briup.smart.env.entity.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/*
 * @Description: 添加采集模块接口的实现类
 * 解析数据文件，将数据封装成Environment对象，并添加到集合中，最后返回集合
 */
public class GatherImpl implements Gather {
    /* 采集模块功能实现逐行解析data-file-simple中数据，每行 --> 1或2个 Environment对象将所有对象添加到Collection集合中，最终返回*/
    @Override
    public Collection<Environment> gather() throws Exception {
        //注意，我们创建的是模块而非项目，相对路径是相对项目，所以filePath以env-gather-impl开头
        String filePath = "env-gather-impl\\src\\main\\resources\\data-file-simple";
        ArrayList<Environment> list = new ArrayList<>();
        try (//1.使用BufferedReader逐行读取文件内容
                BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String str = null;
            //2.逐行读取数据并处理
            while ((str = br.readLine()) != null) {
                //原始数据案例:
                //100|101|2|16|1|3|5d706fbc02|1|1516323604876
                //字符串分割操作，按 | 进行分割，|在正则表达式中有特殊的含义，需要特别处理
                String[] arr = str.split("[|]");// \\|
                // 数据验证
                if (arr.length < 9) {
                    System.out.println("数据格式不完整: " + str);
                    continue;
                }
                String srcId = arr[0];
                String desId = arr[1];
                String devId = arr[2];
                String sensorAddress = arr[3];
                int count = Integer.parseInt(arr[4]);
                String cmd = arr[5];
                int status = Integer.parseInt(arr[7]);
                //时间戳转换为Timestamp对象
                Timestamp gatherDate = new Timestamp(Long.parseLong(arr[8]));
                //3.核心功能：根据arr[3](传感器地址)计算 name、环境值data
                switch (arr[3]) {
                    case "16":
                        /*
                        表示温度和湿度数据。当数据为温度和湿度时，环境数据arr[6]（例:5d806ff802）的前两个字节表示温度(即前4位数,例:5d80)
                        中间的两个字节表示湿度(即中间的4位数，例:6ff8)
                        */
                        //把十六进制的环境数据，转为十进制的数据之后，如果是温度数据：(data*(0.00268127F))-46.85F
                        Float temperature = (Integer.parseInt(arr[6].substring(0,4),16)*(0.00268127F))-46.85F;
                        list.add(new Environment("温度", srcId, desId, devId, sensorAddress, count, cmd, status, temperature, gatherDate));
                        //把十六进制的环境数据，转为十进制的数据之后，如果是湿度数据：(data*0.00190735F)-6
                        Float humidity = (Integer.parseInt(arr[6].substring(4,8),16)*0.00190735F)-6;
                        list.add(new Environment("湿度", srcId, desId, devId, sensorAddress, count, cmd, status, humidity, gatherDate));
                        break;
                    case "256":
                        //光照强度数据，前两个字节是数据值，剩余字节不用管
                        list.add(new Environment("光照强度", srcId, desId, devId, sensorAddress, count, cmd, status, Integer.parseInt(arr[6].substring(0,4),16), gatherDate));
                        break;
                    case "1280":
                        //二氧化碳数据，前两个字节是数据值，剩余字节不用管
                        list.add(new Environment("二氧化碳浓度", srcId, desId, devId, sensorAddress, count, cmd, status, Integer.parseInt(arr[6].substring(0,4),16), gatherDate));
                        break;
                    default:
                        System.out.println("数据格式错误: " + str);
                        break;
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        System.out.println("采集模块: 采集数据完成，本次共采集:"+list.size()+"条");
        return list;
    }
}
