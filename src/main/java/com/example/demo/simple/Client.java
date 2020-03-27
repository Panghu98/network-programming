package com.example.demo.simple;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * @author: panghu
 * @Description:
 * @Date: Created in 9:02 2020/3/27
 * @Modified By:
 */
public class Client {

    final static String DEFAULT_SERVER_HOST = "127.0.0.1";
    final static int DEFAULT_SERVER_POST = 8080;
    final static String QUIET = "quite";

    public static void getMessage(BufferedReader reader) {

        if (reader == null) {
            throw new NullPointerException("BufferedReader is null ----");
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                while (true) {
                    String msg = reader.readLine();
                    if (msg == null) {
                        break;
                    }
                    System.out.println("\n" + "收到来自服务器的消息" + msg);
                }
            } catch (IOException e) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            }
        });

    }

    public static void main(String[] args) {
        Socket socket = null;
        BufferedWriter writer = null;
        try {
            // 创建socket
            socket = new Socket(DEFAULT_SERVER_HOST,DEFAULT_SERVER_POST);
            // 获取输入流
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            // 获取输出流
            writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            // 等待用于输入信息
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            getMessage(reader);

            String input = null;
            do {
                System.out.print("请输入信息:");
                input = consoleReader.readLine();
                writer.write(input);
                writer.newLine();
                writer.flush();
                // 查看用户是否退出
            } while (!input.equals(QUIET));

        } catch (IOException exception){
            exception.printStackTrace();
        }finally {
            if (writer != null) {
                try {
                    writer.close();
                    System.err.println("关闭Socket");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
