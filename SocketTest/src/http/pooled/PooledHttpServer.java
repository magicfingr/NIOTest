package http.pooled;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by zxt on 2014/4/23.
 * use MultiHttpServer instead
 */
public class PooledHttpServer {
    private File docDirectory;
    private String indexFileName;
    private ServerSocket server;
    public static final int DEAULT_THREAD_NUM = 20;
    public static final int DEAULT_PORT = 8080;
    public static final String DEAULT_INDEX_FILE = "index.html";

    public PooledHttpServer(File docDirectory, int port, String indexFileName) throws IOException {
        if (!docDirectory.isDirectory())
            throw new IllegalArgumentException(docDirectory + " does not exist as a directory!");
        this.docDirectory = docDirectory;
        this.indexFileName = indexFileName;
        this.server = new ServerSocket(port);
    }

   public void listen(){
        for (int i = 0; i < DEAULT_THREAD_NUM; i++) {
            Thread t = new Thread(new PooledRequestProcessor(docDirectory, indexFileName));
            t.start();
        }
       System.out.println("[INFO] server started at: " + server);
       System.out.println("[INFO] document root: " + docDirectory);
       while(true){
           try {
               Socket request = server.accept();
               PooledRequestProcessor.processRequest(request);
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
    }

    public static void main(String[] args) {
        File docDirectory;
        int port = DEAULT_PORT;
        String indexFileName = DEAULT_INDEX_FILE;
        try {
            docDirectory = new File(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Usage: java PooledHttpServer [documentRoot] [port] [indexFileName]");
            return;
        }
        try {
            port = Integer.parseInt(args[1]);
        } catch (Exception e) {}
        if(args.length >= 3)
            indexFileName = args[2];
        try {
            PooledHttpServer httpServer = new PooledHttpServer(docDirectory, port, indexFileName);
            httpServer.listen();
        } catch (IOException e) {
            System.out.println("[ERROR] failed to start server!");
            e.printStackTrace();
        }
    }
}
