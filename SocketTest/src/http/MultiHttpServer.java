package http;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by zxt on 2014/4/23.
 * <p/>
 * 提供 docDirectory 目录下的文件访问
 * 默认工作在80端口
 */
public class MultiHttpServer {
    private final static Logger LOGGER = Logger.getLogger("MultiHttpServer");
    public static final int DEFAULT_THREAD_NUM = 20;
    public static final int DEFAULT_PORT = 80;
    private File docDirectory;
    private ServerSocket server;

    public MultiHttpServer(File docDirectory, int port) throws IOException {
        if (!docDirectory.isDirectory())
            throw new IllegalArgumentException(docDirectory + " does not exist as a directory!");
        this.docDirectory = docDirectory;
        this.server = new ServerSocket(port);
    }

    public static void main(String[] args) {
        File docDirectory;
        int port = DEFAULT_PORT;

        //测试文件路径
        docDirectory = new File("D:\\temp");
        //从命令行取得文件路径
//        try {
//            docDirectory = new File(args[0]);
//        } catch (ArrayIndexOutOfBoundsException e) {
//            System.out.println("Usage: java PooledHttpServer [documentRoot] [port] [indexFileName]");
//            return;
//        }
        try {
            port = Integer.parseInt(args[1]);
        } catch (Exception e) {
        }
        try {
            MultiHttpServer httpServer = new MultiHttpServer(docDirectory, port);
            httpServer.listen();
        } catch (IOException e) {
            System.out.println("[ERROR] failed to start server!");
            e.printStackTrace();
        }
    }

    public void listen() {
        LOGGER.log(Level.INFO, "server started at: " + server);
        LOGGER.log(Level.INFO, "document root: " + docDirectory);
        ExecutorService exec = Executors.newFixedThreadPool(DEFAULT_THREAD_NUM);
        //持续监听
        while (true) {
            try {
                Socket request = server.accept();
                exec.execute(new RequestProcessor(docDirectory, request));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        exec.shutdown();
    }
}
