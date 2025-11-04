package com.briup.env.test;

import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Calendar;

public class DBStoreTest {
    //获取Timestamp对象中的day
    @Test
    public void test01() {
        Timestamp gather_date =
                new Timestamp(System.currentTimeMillis() + 10*60*60*1000L);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(gather_date.getTime());
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        System.out.println("第一种方式: " + day);

        // 将Timestamp对象转换为LocalDate对象
        LocalDate localDate = gather_date.toLocalDateTime().toLocalDate();
        // 获取天
        day = localDate.getDayOfMonth();
        System.out.println("第二种方式: " + day);
    }

}
