package com.example.demo.nio_chatroom;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * @author: panghu
 * @Description:
 * @Date: Created in 22:51 2020/4/1
 * @Modified By:
 */
public class ChatClient {
    private static final String DEFAULT_SERVER_HOST = "127.0.0.1";
    private static final int DEFAULT_SERVER_PORT = 8888;
    private static final String QUIT = "quit";
    private static final int BUFFER = 1024;

    private String host;
    private int port;
    private SocketChannel client;
    private ByteBuffer rBuffer = ByteBuffer.allocate(BUFFER);
    private ByteBuffer wBuffer = ByteBuffer.allocate(BUFFER);
    private Selector selector;
    private Charset charset = StandardCharsets.UTF_8;

    public ChatClient() {
        this(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT);
    }

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean readyToQuit(String msg) {
        return QUIT.equals(msg);
    }

    private void close(Closeable closable) {
        if (closable != null) {
            try {
                closable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        try {
            // 初始化channel
            client = SocketChannel.open();
            client.configureBlocking(false);

            selector = Selector.open();
            client.register(selector, SelectionKey.OP_CONNECT);
            client.connect(new InetSocketAddress(host,port));

            while (true) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey key:selectionKeys
                     ) {
                    handles(key);
                }
                selectionKeys.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(client);
        }

    }

    private void handles(SelectionKey key) throws IOException {
        // CONNECT 连接就绪事件
        if (key.isConnectable()) {
            SocketChannel client = (SocketChannel) key.channel();
            // 连接就绪状态，连接了不是马上就绪，懂？
            if (client.isConnectionPending()) {
                // 完成连接建立
                client.finishConnect();
                // 处理用户输入
                // 处理用户的输入
                new Thread(new UserInputHandler(this)).start();
            }
        }


        // READ事件 -  服务器转发消息
        else if (key.isReadable()) {
            SocketChannel client = (SocketChannel) key.channel();
            String msg = receive(client);
            if (msg.isEmpty()) {
                // 服务器异常
                close(selector);
            } else {
                System.out.println(msg);
            }
        }
    }

    public void send(String msg) throws IOException {
        if (msg.isEmpty()) {
            return;
        }

        wBuffer.clear();
        wBuffer.put(charset.encode(msg));
        wBuffer.flip();
        while (wBuffer.hasRemaining()) {
            client.write(wBuffer);
        }

        // 检查用户是否准备退出
        if (readyToQuit(msg)) {
            close(selector);
        }
    }


    private String receive(SocketChannel client) throws IOException {
        rBuffer.clear();
        while (client.read(rBuffer) > 0) {
            rBuffer.flip();
        }
        return String.valueOf(charset.decode(rBuffer));
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient("127.0.0.1", 7777);
        client.start();
    }

}