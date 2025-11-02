package com.briup.smart.env.client;

import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.util.Backup;
import com.briup.smart.env.util.BackupImpl;
import com.briup.smart.env.util.Log;
import com.briup.smart.env.util.LogImpl;

import java.io.RandomAccessFile;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

public class GatherBackupImpl implements Gather {
    private static final Log log = new LogImpl();
    @Override
    public Collection<Environment> gather() throws Exception {
        String gatherFilePath = "env-gather-impl/src/main/resources/data-file-simple";
        String backupFilePath = "env-gather-impl/src/main/resources/gather-backup.txt";

        // 1.采集开始前，读取备份文件，获取上次的偏移量
        Backup backup = new BackupImpl();
        Object obj = backup.load(backupFilePath, Backup.LOAD_REMOVE);
        long offset = obj == null ? 0L : (Long) obj + 2;//之前解析位置的下一行的位置(换行符占2字节)
        log.info("备份模块: 获取备份文件偏移量: " + offset);

        //采集开始前，先调整偏移量到适合的位置
        RandomAccessFile raf = new RandomAccessFile(gatherFilePath, "r");
        raf.seek(offset);

        ArrayList<Environment> list = new ArrayList<>();
        String str = null;
        // 2.逐条读取数据
        while ((str = raf.readLine()) != null) {
            //原始数据案例:
            //100|101|2|16|1|3|5d706fbc02|1|1516323604876
            //字符串分割操作，按 | 进行分割，|在正则表达式中有特殊的含义，需要特别处理
            String[] arr = str.split("[|]"); // \\|
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
            environment.setGatherDate(new Timestamp(Long.parseLong(arr[8])));
            //根据arr[3](传感器地址)计算 name、环境值data
            switch (arr[3]) {
                case "16":
                        /*
                        表示温度和湿度数据。当数据为温度和湿度时，环境数据arr[6]（例:5d806ff802）的前两个字节表示温度(即前4位数,例:5d80)
                        中间的两个字节表示湿度(即中间的4位数，例:6ff8)
                        */
                    //处理温度，获取前2个字节，按照公式求温度值
                    environment.setName("温度");
                    String temperature = arr[6].substring(0, 4);
                    //16进制转换为10进制
                    int t = Integer.parseInt(temperature, 16);
                    environment.setData((t * (0.00268127F)) - 46.85F);
                    list.add(environment);
                    //表示湿度,中间两个字节是湿度
                    Environment environment1 = cloneEnvironment(environment);
                    environment1.setName("湿度");
                    String humidity = arr[6].substring(4, 8);
                    int h = Integer.parseInt(humidity, 16);
                    environment1.setData((h * 0.00190735F) - 6);
                    list.add(environment1);
                    break;
                case "256":
                    //光照强度数据，前两个字节是数据值，剩余字节不用管
                    environment.setName("光照强度");
                    environment.setData(Integer.parseInt(arr[6].substring(0, 4), 16));
                    list.add(environment);
                    break;
                case "1280":
                    //二氧化碳数据，前两个字节是数据值，剩余字节不用管
                    environment.setName("二氧化碳浓度");
                    environment.setData(Integer.parseInt(arr[6].substring(0, 4), 16));
                    list.add(environment);
                    break;
                default:
                    System.out.println("数据格式错误: " + str);
                    break;
            }
        }

        // 3.采集完成后，备份采集数据偏移量
        offset = raf.getFilePointer();
        log.info("备份模块: 存储备份文件偏移量: " + offset);
        backup.store(backupFilePath, offset, Backup.STORE_OVERRIDE);

        //释放资源
        raf.close();
        log.info("采集模块: 采集数据完成,本次采集数量: " + list.size() + "条");

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
