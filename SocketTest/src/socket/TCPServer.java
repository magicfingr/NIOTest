package socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by zxt on 2014/4/23.
 * <p/>
 * 默认监听本机8123端口，显示客户端输入，并可以向客户端输出
 * 也可从控制台指定本机端口
 * 服务器端输入 quit 终止输出，待客户端也终止输入后关闭 socket
 */
public class TCPServer {
    private final static Logger LOGGER = Logger.getLogger("TCPServer");
    public static final int DEFAULT_PORT = 8123;
    private static int count = 1;

    public static void main(String[] args) {
        ServerSocket server = null;
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
        try {
            server = new ServerSocket(port);
            LOGGER.log(Level.INFO, "server started at: " + server);

            //listening forever
            while (true) {
                // Blocks until a connection occurs:
                Socket socket = server.accept();
                LOGGER.log(Level.INFO, "connection " + (count++) + " accepted: " + socket);
                Thread readThread = new ReadThread(socket.getInputStream());
                readThread.start();
                Thread writeThread = new WriteThread(socket.getOutputStream());
                writeThread.start();
                //wait for input and output
                try {
                    readThread.join();
                    writeThread.join();
                    LOGGER.log(Level.INFO, "IO finished,  closing socket...");
                    socket.close();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            //服务器启动、监听问题
            e.printStackTrace();
            try {
                if (null != server)
                    server.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

}
