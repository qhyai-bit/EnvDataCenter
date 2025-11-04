package com.briup.env.test;

import java.io.Serializable;

//对象如果需要在IO流中传输，则一定要实现序列化接口
public class Student implements Serializable {
    private int id;
    private String name;

    public Student() {
    }
    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
