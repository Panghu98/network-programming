package com.example.demo.nio_chatroom;


import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * @author panghu
 */
public class ChatServer{
    private static final int DEFAULT_PORT = 8888;
    private static final String QUIT = "quit";
    private static final int BUFFER = 1024;

    private ServerSocketChannel server;
    private Selector selector;
    /**
     * 分配用于数据读取缓冲区
     */
    private ByteBuffer rBuffer = ByteBuffer.allocate(BUFFER);

    /**
     * 用于数据发送的缓冲区
     */
    private ByteBuffer wBuffer = ByteBuffer.allocate(BUFFER);

    private Charset charset = StandardCharsets.UTF_8;

    private int port;

    public ChatServer(int port) {
        this.port = port;
    }

    public ChatServer() {
        this.port = DEFAULT_PORT;
    }

    private void start() {
        try {
            server = ServerSocketChannel.open();
            // 默认是阻塞的，一定要开启这个
            // 但是FileChannel是只能阻塞的
            server.configureBlocking(false);
            // 绑定端口
            server.socket().bind(new InetSocketAddress(port));

            // 初始化选择器
            selector = Selector.open();

            // 监听事件
            server.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("启动服务器，监听端口：" + port + "....");

            // selector线程是会阻塞的
            while (true) {
                // 返回有多少条监听事件被触发
                selector.select();

                // 获取SelectionKeys
                Set<SelectionKey> selectionKeys = selector.keys();
                for (SelectionKey key:selectionKeys
                     ) {
                    // 处理被触发事件
                    handles(key);
                }
                // 使用完成之后需要进行处理
                selectionKeys.clear();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            // selector会首先解除注册，关闭相关channel
            // 所以无需再close server
            close(selector);
        }
    }

    private void handles(SelectionKey key) throws IOException {

        // ACCEPT事件 -- 和客户端建立了连接
        if (key.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel client = server.accept();
            // 转换成非阻塞调用
            client.configureBlocking(false);

            client.register(selector,SelectionKey.OP_READ);
            System.out.println("客户端[" + client.socket().getPort() + "]已经连接");
        }

        // READ事件 - 客户端发送了消息

    }


    private boolean readyToQuit(String msg) {
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

}