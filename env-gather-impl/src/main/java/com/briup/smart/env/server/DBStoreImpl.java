package com.briup.smart.env.server;

import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.util.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;

public class DBStoreImpl implements DBStore{
    //将coll集合中全部环境数据，保存到数据库中
    //要求使用: pstmt预处理对象+批处理+手动事务管理
    @Override
    public void saveDB(Collection<Environment> collection) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        //实际入库条数
        int saveCount = 0;
        try {
            //1、2.获取数据库连接
            conn = JdbcUtils.getConnection();
            //设置不自动提交
            conn.setAutoCommit(false);

            // 准备计数器
            int count = 0;
            int currDay = -1;
            int preDay = -1;

            //执行sql语句，插入数据
            for(Environment env : collection) {
                //获取采集天
                Timestamp gatherDate = env.getGatherDate();
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(gatherDate.getTime());
                currDay = cal.get(Calendar.DAY_OF_MONTH);

                if (preDay == -1 || currDay != preDay) {
                    if (preDay != -1){
                        //执行批处理
                        pstmt.executeBatch();
                        //提交事务
                        conn.commit();
                        //记录实际入库数据
                        saveCount += count;
                        count = 0;
                        pstmt.close();
                        System.out.println("提交事务,saveCount: " + saveCount);
                    }
                    //3.获取pstmt对象
                    String sql = "insert into e_detail_" +currDay+ "(name,srcId,desId,devId,sensorAddress,count,cmd,status,data,gather_date) " + "values(?,?,?,?,?,?,?,?,?,?)";
                    pstmt = conn.prepareStatement(sql);
                    System.out.println("创建新pstmt: " + sql);
                }
                //4.1 设置?值
                pstmt.setString(1,env.getName());
                pstmt.setString(2,env.getSrcId());
                pstmt.setString(3,env.getDesId());
                pstmt.setString(4,env.getDevId());
                pstmt.setString(5,env.getSensorAddress());
                pstmt.setInt(6,env.getCount());
                pstmt.setString(7,env.getCmd());
                pstmt.setInt(8,env.getStatus());
                pstmt.setFloat(9,env.getData());
                pstmt.setTimestamp(10,env.getGatherDate());

                //4.2 添加到批处理
                pstmt.addBatch();
                count++;

                //4.3 每3条 执行一次批处理
                if(count % 3 == 0) {
                    //执行批处理
                    pstmt.executeBatch();
                    //提交事务
                    conn.commit();
                    //记录实际入库数据
                    saveCount += count;
                    count = 0;
                    System.out.println("提交事务,saveCount: " + saveCount);
                }
                //当前数据处理完成，记录当前天数
                preDay = currDay;
            }
            //4.4 最后再执行一次批处理
            if (count != 0) {
                pstmt.executeBatch();
                //提交事务
                conn.commit();
                //记录实际入库数据
                saveCount += count % 3;
                System.out.println("实际入库数据: " + saveCount + "条");
            }
        } catch (Exception e) {
            if(conn != null) {
                try {
                    conn.rollback();
                    System.out.println("事务回滚成功!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            JdbcUtils.close(pstmt,conn);
        }
    }
}
