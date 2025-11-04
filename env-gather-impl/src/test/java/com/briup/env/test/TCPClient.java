package com.briup.env.test;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TCPClient {
    public static void main(String[] args) {
        // 服务器ip及端口
        String host = "127.0.0.1";
        int port = 8888;

        Socket socket = null;
        ObjectOutputStream oos = null;
        try {
            //对象创建成功，就表示连接服务器端成功
            socket = new Socket(host,port);

            //创建对象输出流对象
            oos = new ObjectOutputStream(socket.getOutputStream());

            //准备一个对象
            Student student = new Student(1,"James");
            System.out.println("准备发送的对象是: " + student);

            //通过对象流发送对象到服务器
            oos.writeObject(student);
            System.out.println("发送对象成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //关闭资源
            if(oos != null){
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
