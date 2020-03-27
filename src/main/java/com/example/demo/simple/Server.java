package com.example.demo.simple;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * @author: panghu
 * @Description:
 * @Date: Created in 8:40 2020/3/27
 * @Modified By:
 */
public class Server {

    final static int DEFAULT_PORT = 8080;
    final static String QUIET = "quite";

    private static void getMsg(BufferedReader reader,Socket socket) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try{
                    while (true) {
                        // 读取客户端发送的消息，可能会得到空的信息
                        String msg = reader.readLine();
                        if (msg != null ) {
                            if (!msg.equals(QUIET)) {
                                System.out.println("客户端[" + socket.getInetAddress() + socket.getPort() + "]发来消息 ：" + msg);
                            }else {
                                System.out.println("客户端" + socket.getInetAddress() + socket.getPort()
                                        + "断开连接");
                                break;
                            }
                        }
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        // 进行bind
        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("启动服务器，监听端口" + DEFAULT_PORT);
            // accept阻塞直到有请求发送过来
            // 等待客户端连接
            Socket socket = serverSocket.accept();
            System.out.println("客户端["+ socket.getInetAddress() + socket.getPort() + "]已连接");
            // 获取输入流
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            // 获取输出流
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            // 等待用于输入信息
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            getMsg(reader,socket);
            while (true) {

                System.out.print("请输入信息：");
                String response = consoleReader.readLine();
                //回复消息,加一个回车符，这样就可以使用readLine
                writer.write("服务器回复给 " +socket.getInetAddress() + socket.getPort()
                        + response + "\n");
                //保证数据全部都发送出去
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (serverSocket != null) {
                try {
                    // 有异常才会关闭
                    serverSocket.close();
                    System.err.println("服务器已经关闭");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
