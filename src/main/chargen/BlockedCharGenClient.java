package main.chargen;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Created by zxt on 2014/4/24.
 */
public class BlockedCharGenClient {
    private static final String DEFAULT_HOST = "localhost";
    public static int DEFAULT_PORT = 8123;
    public static int BUFFER_LENGTH = 74;

    public static void main(String[] args) {
        SocketAddress addr = new InetSocketAddress(DEFAULT_HOST, DEFAULT_PORT);
        try {
            SocketChannel channel = SocketChannel.open(addr);
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_LENGTH);
            WritableByteChannel out = Channels.newChannel(System.out);

            while (-1 != channel.read(buffer)){
                buffer.flip();
                out.write(buffer);
                buffer.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
