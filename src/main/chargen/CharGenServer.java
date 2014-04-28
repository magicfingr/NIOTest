package main.chargen;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by zxt on 2014/4/24.
 * 字符生成服务器
 */
public class CharGenServer {
    private final static Logger LOGGER = Logger.getLogger("CharGenServer");
    public static int DEFAULT_PORT = 8123;
    public static int BUFFER_LENGTH = 74;

    public static void main(String[] args) {
        byte[] msg = new byte[95 * 2];
        for (byte i = ' '; i <= '~'; i++) {
            msg[i - ' '] = i;
            msg[i - ' ' + 95] = i;
        }
//        LOGGER.log(Level.INFO, "INFO");
//        LOGGER.log(Level.ALL, "ALL");
//        LOGGER.log(Level.CONFIG, "CONFIG");
//        LOGGER.log(Level.FINE, "FINE");
//        LOGGER.log(Level.FINER, "FINER");
//        LOGGER.log(Level.FINEST, "FINEST");
//        LOGGER.log(Level.OFF, "OFF");
//        LOGGER.log(Level.WARNING, "WARNING");
//        LOGGER.log(Level.SEVERE, "SEVERE");

        //新建一个最初的服务器channel
        ServerSocketChannel serverChannel;
        Selector selector;
        try {
            InetSocketAddress addr = new InetSocketAddress(DEFAULT_PORT);
            serverChannel = ServerSocketChannel.open();
            //设置为非阻塞模式
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(addr);
            LOGGER.log(Level.INFO, "server started at: " + serverChannel.socket());
//            System.out.println("[INFO] server started at: " + serverChannel.tcp());
            selector = Selector.open();
            //向选择器注册连接事件
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                //选择就绪的channel
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            //循环处理所有已就绪channel
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                try {
                    //服务器channel就绪
                    if (key.isAcceptable()) {
                        //取得就绪的服务器channel
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        //取得连接的客户端channel，同样设置为非阻塞工作模式
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        LOGGER.log(Level.INFO, "connection accepted: " + client);
//                        System.out.println("[INFO] connection accepted: " + client);
                        //为客户端channel注册写入就绪事件
                        SelectionKey newKey = client.register(selector, SelectionKey.OP_WRITE);
                        //填入数据并连接到channel
                        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_LENGTH);
//                        buffer = ByteBuffer.wrap(msg, 0, BUFFER_LENGTH - 2);
                        buffer.put(msg, 0, BUFFER_LENGTH - 2);
                        buffer.put((byte) '\r');
                        buffer.put((byte) '\n');
                        buffer.flip();
                        newKey.attach(buffer);
                        //传输第一次数据
                        client.write(buffer);
                    } else if (key.isWritable()) {  //客户端channel就绪
                        SocketChannel client = (SocketChannel) key.channel();
                        //获取channel数据并判断是否传输完毕
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        if (!buffer.hasRemaining()) {   //传输完毕则填入新的数据
                            //fill with next line
                            int pos = (int) buffer.get(0) - ' ' + 1;
                            buffer.rewind();
                            buffer.put(msg, pos, BUFFER_LENGTH - 2);
                            buffer.put((byte) '\r');
                            buffer.put((byte) '\n');
                            buffer.flip();
                        }
                        //继续传输
                        client.write(buffer);
                    }
                } catch (IOException e) {
//                    System.out.println("[ERROR] connection closed unexpectedly. ");
                    LOGGER.log(Level.INFO, "connection  closed unexpectedly.");
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
//                    e.printStackTrace();
                }
            }
        }
//        try {
//            selector.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}
