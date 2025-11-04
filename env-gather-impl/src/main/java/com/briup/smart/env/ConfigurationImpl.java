package com.briup.smart.env;

import com.briup.smart.env.client.Client;
import com.briup.smart.env.client.Gather;
import com.briup.smart.env.server.DBStore;
import com.briup.smart.env.server.Server;
import com.briup.smart.env.support.ConfigurationAware;
import com.briup.smart.env.support.PropertiesAware;
import com.briup.smart.env.util.Backup;
import com.briup.smart.env.util.Log;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

//配置模块 实现类，其包含所有模块的实例对象，及所有模块中的属性信息
public class ConfigurationImpl implements Configuration{
    private static ConfigurationImpl conf = new ConfigurationImpl();

    private ConfigurationImpl(){}

    public static ConfigurationImpl getInstance(){
        return conf;
    }

    //模块实例对象集合
    private static Map<String,Object> map = new HashMap<>();
    //属性信息集合
    private static Properties prop = new Properties();

    //静态代码块: 解析配置文件,获取属性信息添加prop中;
    //          获取模块实例对象,添加map中
    static {
        Document document = null;
        try {
            //1.创建SAXReader对象
            SAXReader reader = new SAXReader();

            //2.创建Document对象
            document = reader.read("env-gather-impl/src/main/resources/config.xml");

            //3.获取根标签
            Element rootElement = document.getRootElement();

            // 4.1 获取全部1级子标签,创建对象,并添加到map集合中
            for(Element element : rootElement.elements()){
                //获取标签名
                String name = element.getName();
                //再获取 class属性值
                String className = element.attributeValue("class");
                //利用反射技术,创建className对象
                Class clazz = Class.forName(className);
                Object obj = clazz.newInstance();

//                System.out.println(name + " :" + obj);

                //添加 模块对象到map集合中
                map.put(name,obj);

                // 4.2 获取全部2级子标签,创建属性值,并添加到prop集合中
                List<Element> secEleList = element.elements();
                if (secEleList != null && !secEleList.isEmpty()){
                    for(Element secEle : secEleList){
                        //获取属性名
                        String propName = secEle.getName();
                        //获取属性值
                        String propValue = secEle.getText();
                        //添加属性值到prop集合中
//                        System.out.println(propName + ":" + propValue);
                        prop.setProperty(propName,propValue);
                    }
                }
            }
            //至此，所有模块对象、属性信息已经添加到map和prop集合中
            //遍历map集合，获取所有模块对象，执行初始化方法
            for (Object obj : map.values()){
                //1.模块对象 注入 其他模块对象
                if (obj instanceof ConfigurationAware){
                    ((ConfigurationAware) obj).setConfiguration(conf);
                }

                //2.模块对象 注入 属性信息
                if (obj instanceof PropertiesAware){
                    ((PropertiesAware) obj).init(prop);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public Log getLogger() {
        return (Log) map.get("logger");
    }

    @Override
    public Server getServer() {
        return (Server) map.get("server");
    }

    @Override
    public Client getClient() {
        return (Client) map.get("client");
    }

    @Override
    public DBStore getDbStore() {
        return (DBStore) map.get("dbStore");
    }

    @Override
    public Gather getGather() {
        return (Gather) map.get("gather");
    }

    @Override
    public Backup getBackup() {
        return (Backup) map.get("backup");
    }
}
