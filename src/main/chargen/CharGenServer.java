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

        ServerSocketChannel serverChannel;
        Selector selector;
        try {
            InetSocketAddress addr = new InetSocketAddress(DEFAULT_PORT);
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(addr);
            LOGGER.log(Level.INFO, "server started at: " + serverChannel.socket());
//            System.out.println("[INFO] server started at: " + serverChannel.socket());
            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                try {
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        LOGGER.log(Level.INFO, "connection accepted: " + client);
//                        System.out.println("[INFO] connection accepted: " + client);
                        client.configureBlocking(false);
                        SelectionKey newKey = client.register(selector, SelectionKey.OP_WRITE);
                        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_LENGTH);
//                        buffer = ByteBuffer.wrap(msg, 0, BUFFER_LENGTH - 2);
                        buffer.put(msg, 0, BUFFER_LENGTH - 2);
                        buffer.put((byte) '\r');
                        buffer.put((byte) '\n');
                        buffer.flip();
                        newKey.attach(buffer);
                        client.write(buffer);
                    } else if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        if (!buffer.hasRemaining()) {
                            //fill with next line
                            int pos = (int) buffer.get(0) - ' ' + 1;
                            buffer.rewind();
                            buffer.put(msg, pos, BUFFER_LENGTH - 2);
                            buffer.put((byte) '\r');
                            buffer.put((byte) '\n');
                            buffer.flip();
                        }
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
