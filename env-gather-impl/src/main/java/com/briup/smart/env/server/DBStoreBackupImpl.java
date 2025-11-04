package com.briup.smart.env.server;

import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.util.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

//入库(考虑备份)功能实现
public class DBStoreBackupImpl implements DBStore {
    private String dbstoreBackupFilePath;// = "env-gather-impl/src/main/resources/db-backup.txt";
    private Log log;// = new LogImpl();
    private Backup backup;// = new BackupImpl();

    //多表入库
    @Override
    public void saveDB(Collection<Environment> collection) throws Exception {
        log.info("in saveDB, coll.size: " + collection.size());
        //一、提取备份数据，添加到collection集合头部
        Object obj = backup.load(dbstoreBackupFilePath, Backup.LOAD_REMOVE);
        //如果备份文件存在且包含有效数据，则将其读取出来添加到collection的头部
        if(obj != null) {
            ArrayList<Environment> list = (ArrayList<Environment>) obj;
            list.addAll(collection);
            collection = list;
            log.info("添加备份数据后coll.size: " + collection.size());
        }
        //二、常规入库功能实现
        Connection conn = null;
        PreparedStatement pstmt = null;
        //实际入库条数
        int saveCount = 0;
        try {
            //1 2.获取数据库连接
            conn = JdbcUtils.getConnection();
            //System.out.println("conn: " + conn);
            conn.setAutoCommit(false);
            // 准备计数器
            int count = 0;
            //
            int preDay = -1;
            int currDay = -1;
            //循环计数器
            int i = 0;
            for(Environment env : collection) {
                //获取采集天
                Timestamp gatherDate = env.getGatherDate();
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(gatherDate.getTime());
                currDay = cal.get(Calendar.DAY_OF_MONTH);
                if(preDay == -1 || preDay != currDay) {
                    //如果读取到新的一天，则将前一个pstmt对象
                    if(preDay != -1) {
                        pstmt.executeBatch();
                        conn.commit();
                        saveCount += count;
                        count = 0;
                        pstmt.close();
                        log.info("提交事务,saveCount: " + saveCount);
                    }
                    //3.获取pstmt对象
                    String sql = "insert into e_detail_"+currDay+" (name,srcId,desId,devId,sensorAddress,count,cmd,status,data,gather_date) values(?,?,?,?,?,?,?,?,?,?)";
                    pstmt = conn.prepareStatement(sql);
                    log.info("创建新pstmt: " + sql);
                }
                //4.1 设置?值
                pstmt.setString(1, env.getName());
                pstmt.setString(2, env.getSrcId());
                pstmt.setString(3, env.getDesId());
                pstmt.setString(4, env.getDevId());
                pstmt.setString(5, env.getSensorAddress());
                pstmt.setInt(6, env.getCount());
                pstmt.setString(7, env.getCmd());
                pstmt.setFloat(8, env.getData());
                pstmt.setInt(9, env.getStatus());
                pstmt.setTimestamp(10, env.getGatherDate());
                count++;
                i++;
                //模拟异常的产生
                if(i == 20) {
                //i = 10 / 0;
                }
                //4.2 添加到批处理
                pstmt.addBatch();
                //4.3 执行插入操作
                if(count % 3 == 0) {
                    pstmt.executeBatch();
                    //提交事务
                    conn.commit();
                    //记录实际入库数据
                    saveCount += count;
                    //重置计数器为0
                    count = 0;
                    log.info("提交事务,saveCount: " + saveCount);
                }
                //当前数据处理完成，记录当前天数
                preDay = currDay;
            }
            //4.4 出循环再次执行批处理
            if (pstmt != null) {
                pstmt.executeBatch();
                //提交事务
                conn.commit();
                //记录实际入库数据
                saveCount += count;
                log.info("成功入库数据条数: " + saveCount);
            }
        } catch (Exception e) {
            //1.回滚事务
            if(conn != null) {
                conn.rollback();
                log.info("回滚事务成功!");
            }
            log.info("成功入库数据条数: " + saveCount);
            //2.获取尚未入库的数据添加到集合中
            ArrayList<Environment> list = (ArrayList<Environment>) collection;
            // size: 22 saveCount: 18 subList(18,22);[18,22)
            List<Environment> subList = list.subList(saveCount, list.size());
            ArrayList<Environment> backUpList = new ArrayList<>();
            backUpList.addAll(subList);
            //3.将尚未入库的数据备份到本地文件
            //Backup backup = new BackupImpl();
            backup.store(dbstoreBackupFilePath,backUpList, Backup.STORE_OVERRIDE);
            log.info("成功备份数据条数: " + backUpList.size());
            log.error("发生异常: " + e.getMessage());
        } finally {
            JdbcUtils.close(pstmt,conn);
        }
    }
}