package http;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zxt on 2014/4/23.
 */
public class MultiHttpServer {
    private File docDirectory;
    private ServerSocket server;
    public static final int DEAULT_THREAD_NUM = 20;
    public static final int DEAULT_PORT = 8080;

    public MultiHttpServer(File docDirectory, int port) throws IOException {
        if (!docDirectory.isDirectory())
            throw new IllegalArgumentException(docDirectory + " does not exist as a directory!");
        this.docDirectory = docDirectory;
        this.server = new ServerSocket(port);
    }

    public void listen(){
        System.out.println("[INFO] server started at: " + server);
        System.out.println("[INFO] document root: " + docDirectory);
        ExecutorService exec = Executors.newFixedThreadPool(DEAULT_THREAD_NUM);
        while(true){
            try {
                Socket request = server.accept();
                exec.execute(new RequestProcessor(docDirectory, request));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        exec.shutdown();
    }

    public static void main(String[] args) {
        File docDirectory;
        int port = DEAULT_PORT;

        //test
        docDirectory = new File("D:\\temp");
        //test
//        try {
//            docDirectory = new File(args[0]);
//        } catch (ArrayIndexOutOfBoundsException e) {
//            System.out.println("Usage: java PooledHttpServer [documentRoot] [port] [indexFileName]");
//            return;
//        }
        try {
            port = Integer.parseInt(args[1]);
        } catch (Exception e) {}
        try {
            MultiHttpServer httpServer = new MultiHttpServer(docDirectory, port);
            httpServer.listen();
        } catch (IOException e) {
            System.out.println("[ERROR] failed to start server!");
            e.printStackTrace();
        }
    }
}
