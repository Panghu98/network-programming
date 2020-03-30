package com.example.demo.chat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: panghu
 * @Description:
 * @Date: Created in 16:31 2020/3/27
 * @Modified By:
 */
public class ChatServer {

    final static int DEFAULT_SERVER_POST = 8080;
    public final static String QUIET = "quite";
    private ExecutorService executorService;

    private ServerSocket serverSocket;
    /**
     * 从每个Socket获取的Writer都是不同的对象
     */
    private Map<Integer, Writer> connectionClients;

    /**
     * 初始化Map
     */
    public ChatServer() {
        executorService = Executors.newFixedThreadPool(4);
        this.connectionClients = new ConcurrentHashMap<>(16);
    }

    /**
     * 用于添加新连接的客户端
     * @param socket 客户端的socket
     * @throws IOException
     */
    public void addClient(Socket socket) throws IOException {
        if (socket != null) {
            int port = socket.getPort();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            connectionClients.put(port,writer);
            System.out.println("客户端[" + port + "] 连接到服务器...");
        }
    }

    /**
     * 用于移除客户端信息
     * @param socket 要被移除的客户端对应的socket
     * @throws IOException
     */
    public void removeClient(Socket socket) throws IOException {
        if (socket != null) {
            int port = socket.getPort();
            if (connectionClients.containsKey(port)) {
                // 记住关闭输出流
                connectionClients.get(port).close();
            }
            connectionClients.remove(port);
            System.out.println("客户端[" + port + "]已经关闭连接...");
        }
    }

    /**
     * 用于转发消息
     * @param socket 发送消息的socket
     * @param forwardMessage 消息体
     */
    public void forwardMessage(Socket socket,String forwardMessage) throws IOException {
        for (Integer id:connectionClients.keySet()
             ) {
            // 跳过发送者
            if (!id.equals(socket.getPort())) {
                Writer writer = connectionClients.get(id);
                writer.write(forwardMessage);
                writer.flush();
            }
        }
    }

    public void start() {
        //绑定端口
        try {
            serverSocket = new ServerSocket(DEFAULT_SERVER_POST);
            System.out.println("启动服务器，监听端口" + DEFAULT_SERVER_POST);

            while (true) {
                // 等待客户端连接
                Socket socket = serverSocket.accept();
                // 创建ChatHandler
                executorService.execute(new ChatHandler(this,socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public synchronized void close() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.start();
    }

}
