package socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Administrator on 14-4-19.
 */
public class TCPClient {
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 8123;
    private final static Logger LOGGER = Logger.getLogger("TCPClient");

    public static void main(String[] args) {
        InetAddress addr = null;
        try {
            addr = InetAddress.getByName(DEFAULT_HOST);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                if (port < 0 || port >= 65535) {
                    LOGGER.log(Level.SEVERE, "port must between 0 ~ 65535");
                    return;
                }
            } catch (NumberFormatException e) {
                //use default port
            }
        }
        Socket socket = null;
        try {
            socket = new Socket(addr, port);
            LOGGER.log(Level.INFO, "connected to addr: " + addr + ",  socket: " + socket);

            Thread readThread = new ReadThread(socket.getInputStream());
            readThread.start();
            Thread writeThread = new WriteThread(socket.getOutputStream());
            writeThread.start();
            //wait for input and output complete
            try {
                readThread.join();
                writeThread.join();
                LOGGER.log(Level.INFO, "IO finished,  closing socket...");
                socket.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Connection to " + addr + ":" + port + " failed.");
//            e.printStackTrace();
        } finally {
            try {
                if (null != socket)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
