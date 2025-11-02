package com.briup.smart.env.client;

import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.util.Log;
import com.briup.smart.env.util.LogImpl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

/*
 * @Description: 添加采集模块接口的实现类
 * 解析数据文件，将数据封装成Environment对象，并添加到集合中，最后返回集合
 */
public class GatherImpl implements Gather {
    private static final Log log = new LogImpl();
    /* 采集模块功能实现逐行解析data-file-simple中数据，每行 --> 1或2个 Environment对象将所有对象添加到Collection集合中，最终返回*/
    @Override
    public Collection<Environment> gather(){
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
                    log.warn("数据格式不完整: " + str);
                    continue;
                }
                //将数据封装到Environment对象中
                Environment environment = new Environment();
                //设置发送端id
                environment.setSrcId(arr[0]);
                //设置树莓派系统id
                environment.setDesId(arr[1]);
                //设置实验箱区域模块id(1-8)
                environment.setDevId(arr[2]);
                //设置模块上传感器地址
                environment.setSensorAddress(arr[3]);
                //设置传感器个数 String转int
                environment.setCount(Integer.parseInt(arr[4]));
                //设置发送指令标号 3表示接收数据 16表示发送命令
                environment.setCmd(arr[5]);
                //设置状态(默认1,表示成功) String类型转换为int类型
                environment.setStatus(Integer.parseInt(arr[7]));
                //设置采集时间 时间戳 String转long
                environment.setGatherDate(new
                        Timestamp(Long.parseLong(arr[8])));

                //3.核心功能：根据arr[3](传感器地址)计算 name、环境值data
                switch (arr[3]) {
                    case "16":
                        /*
                        表示温度和湿度数据。当数据为温度和湿度时，环境数据arr[6]（例:5d806ff802）的前两个字节表示温度(即前4位数,例:5d80)
                        中间的两个字节表示湿度(即中间的4位数，例:6ff8)
                        */
                        environment.setName("温度");
                        //把十六进制的环境数据，转为十进制的数据之后，如果是温度数据：(data*(0.00268127F))-46.85F
                        environment.setData((Integer.parseInt(arr[6].substring(0,4),16)*(0.00268127F))-46.85F);
                        list.add(environment);

                        Environment environment1 = cloneEnvironment(environment);
                        environment1.setName("湿度");
                        //把十六进制的环境数据，转为十进制的数据之后，如果是湿度数据：(data*0.00190735F)-6
                        environment1.setData((Integer.parseInt(arr[6].substring(4,8),16)*0.00190735F)-6);
                        list.add(environment1);
                        break;
                    case "256":
                        environment.setName("光照强度");
                        //光照强度数据，前两个字节是数据值，剩余字节不用管
                        environment.setData(Integer.parseInt(arr[6].substring(0,4),16));
                        list.add(environment);
                        break;
                    case "1280":
                        environment.setName("二氧化碳浓度");
                        //二氧化碳数据，前两个字节是数据值，剩余字节不用管
                        environment.setData(Integer.parseInt(arr[6].substring(0,4),16));
                        list.add(environment);
                        break;
                    default:
                        log.error("数据格式错误: " + str);
                        break;
                }
            }
        } catch(IOException e) {
            log.error("数据采集失败");
        }
        log.info("采集模块: 采集数据完成，本次共采集:"+list.size()+"条");
        return list;
    }
    /**
     * 克隆Environment对象。对于属性，除了name(环境种类名称)和data(环境值)都会复制
     *
     * @param e 被克隆的Environment对象
     * @return 克隆得到的的Environment对象
     */
    private Environment cloneEnvironment(Environment e) {
        return new Environment(null, e.getSrcId(), e.getDesId(),
                e.getDevId(), e.getSensorAddress(), e.getCount(), e.getCmd(),
                e.getStatus(), 0, e.getGatherDate());
    }
}
