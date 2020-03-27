package com.example.demo.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author: panghu
 * @Description:
 * @Date: Created in 20:08 2020/3/27
 * @Modified By:
 */
public class UserInputHandler implements Runnable {

    private ChatClient chatClient;

    public UserInputHandler(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public void run() {
        // 等待用户输入消息
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

        // 这里坑了自己一波，while循环不小心写到try的外面了
        try {
            while (true) {
                String msg = consoleReader.readLine();

                // 向服务器发送消息
                chatClient.send(msg);
                if (chatClient.readyToQuit(msg)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            chatClient.close();
        }
    }
}
