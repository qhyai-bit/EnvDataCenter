package com.briup.env.test;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class BackUpTest {
    @Test
    public void test10() {
        ArrayList<String> list = new ArrayList<>();
        list.add("hello");
        list.add("world");
        list.add("123");
        list.add("nihao");
        list.add("briup");

        //截取[2,size)
        List<String> sList = list.subList(2, list.size());
        System.out.println(sList);
    }
}
