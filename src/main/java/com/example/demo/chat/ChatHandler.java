package com.example.demo.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static com.example.demo.chat.ChatServer.QUIET;

/**
 * @author: panghu
 * @Description:
 * @Date: Created in 17:56 2020/3/27
 * @Modified By:
 */
public class ChatHandler implements Runnable{

    private ChatServer chatServer;
    private Socket socket;

    public ChatHandler(ChatServer chatServer, Socket socket) {
        this.chatServer = chatServer;
        this.socket = socket;
    }

    public boolean readyToQuit(String msg) {
        return QUIET.equals(msg);
    }


    /**
     * run()方法是不能抛出异常的
     */
    @Override
    public void run() {
        try {
            // 存储新上线的用户
            chatServer.addClient(socket);
            // 读取用户发送的信息
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            String msg =  null;
            while ((msg = reader.readLine()) != null) {
                String forwardMsg = "客户端[" + socket.getPort() + "] 发送消息: " + msg + "\n";
                System.out.println("forwardMsg = " + forwardMsg);
                //将收到的消息转发给其他的用户
                chatServer.forwardMessage(socket,forwardMsg);

                // 检测用户是否准备退出
                if (readyToQuit(msg)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                chatServer.removeClient(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
