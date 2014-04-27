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
public class CharGenClient {
    private static final String DEFAULT_HOST = "localhost";
    public static int DEFAULT_PORT = 8123;
    public static int BUFFER_LENGTH = 74;

    public static void main(String[] args) {
        SocketAddress addr = new InetSocketAddress(DEFAULT_HOST, DEFAULT_PORT);
        try {
            SocketChannel channel = SocketChannel.open(addr);
            //unblocked io
            channel.configureBlocking(false);

            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_LENGTH);
            WritableByteChannel out = Channels.newChannel(System.out);

            while (true){
                int n = channel.read(buffer);
                if(n > 0) {
                    buffer.flip();
                    out.write(buffer);
                    buffer.clear();
                } else if(-1 == n){
                    //server error
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
