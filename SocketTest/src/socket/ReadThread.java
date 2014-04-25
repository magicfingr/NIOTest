package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by zxt on 2014/4/23.
 */
public class ReadThread extends Thread {
    BufferedReader in;

    public ReadThread(InputStream inputStream) {
        this.in = new BufferedReader(new InputStreamReader(inputStream));
    }

    @Override
    public void run() {
        super.run();
        String line;
        try {
            while (true) {
                //read from socket
                line = in.readLine();
                if (null != line) {
                    System.out.println("remote: " + line);
                    if (line.equals("quit")) break;
                } else break;
            }
        } catch (IOException e) {
            System.out.println("[ERROR] remote socket closed unexpectedly.");
//            e.printStackTrace();
        }
    }
}
