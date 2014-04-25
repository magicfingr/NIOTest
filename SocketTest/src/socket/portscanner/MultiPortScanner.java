package socket.portscanner;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by zxt on 2014/4/22.
 */
public class MultiPortScanner {
    public static Future<ScanResult> isPortOpen(final ExecutorService es, final String ip, final int port, final int timeout) {
        return es.submit(new Callable<ScanResult>() {
            @Override
            public ScanResult call() throws Exception {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, port), timeout);
                    socket.close();
                    return new ScanResult(port, true);
                } catch (IOException e) {
                    return new ScanResult(port, false);
                }
            }
        });
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long time = new Date().getTime();

        ExecutorService es = Executors.newFixedThreadPool(20);
        String ip = "127.0.0.1";
        int timeout = 50;
        List<Future<ScanResult>> futures = new ArrayList<Future<ScanResult>>();

        for (int i = 1; i <= 65535; i++) {
            futures.add(isPortOpen(es, ip, i, timeout));
        }
        es.shutdown();

        int count = 0;
        for (Future<ScanResult> future : futures) {
            if (future.get().isOpen) {
                System.out.println(ip + ": " + "Port " + future.get().port + " is open for service.");
                count++;
            }
        }
        System.out.println("open port count: " + count);
        time = (new Date().getTime()) - time;
        System.out.println("time spend: " + time / 60000.0 + " minutes");   //50ms timeout -->> 2.5 minutes
    }

    static final class ScanResult {
        public final int port;
        public final boolean isOpen;

        ScanResult(int port, boolean isOpen) {
            this.port = port;
            this.isOpen = isOpen;
        }
    }
}
