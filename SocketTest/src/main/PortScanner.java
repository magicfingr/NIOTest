package main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by zxt on 2014/4/22.
 *
 * working but really slow when scanning, see MultiPortScanner.java
 */
public class PortScanner {
    public static void main(String[] args) throws IOException {
        String host = "localhost";

        if (args.length > 0)
            host = args[0];

        //test port availability
        Socket s = null;
        for (int i = 8085; i < 8090; i++) {
            try {
                InetAddress addr = InetAddress.getByName(host);
                s = new Socket(addr, i);
                System.out.println("There is a server on port " + i + " of " + host);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
//                e.printStackTrace();
            } finally {
                if (null != s)
                    s.close();
            }
        }
    }
}