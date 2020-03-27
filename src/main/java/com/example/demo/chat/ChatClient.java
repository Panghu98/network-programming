package com.example.demo.chat;

import java.io.*;
import java.net.Socket;

/**
 * @author: panghu
 * @Description:
 * @Date: Created in 19:46 2020/3/27
 * @Modified By:
 */
public class ChatClient {

    private final int DEFAULT_SERVER_PORT = 8080;
    private final String DEFAULT_SERVER_HOST = "127.0.0.1";
    private final String QUIET = "quite";

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    public ChatClient() {
    }

    /**
     * 发送消息给服务器
     */
    public void send(String msg) throws IOException {
        // 输出流是开放的状态
        if (!socket.isOutputShutdown()) {
            writer.write(msg + "\n");
            writer.flush();
        }
    }

    /**
     * 从服务器接收消息
     */
    public String receive() throws IOException {
        String msg = null;
        if (!socket.isInputShutdown()) {
            msg = reader.readLine();
        }
        return msg;
    }

    /**
     * 检查用户是否准备退出
     * @param msg 消息体
     * @return
     */
    public boolean readyToQuit(String msg) {
        return QUIET.equals(msg);
    }

    public void start() {
        try {
            // 创建Socket
            socket = new Socket(DEFAULT_SERVER_HOST,DEFAULT_SERVER_PORT);

            // 创建IO流
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //处理用户的输入
            new Thread(new UserInputHandler(this)).start();
            //读取服务器转发的消息
            String msg = null;
            while ((msg = receive()) != null) {
                System.out.println(msg);
                if (readyToQuit(msg)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close();
        }
    }

    public void close() {
        if (writer != null) {
            try {
                System.out.println("关闭Socket");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        chatClient.start();
    }

}
