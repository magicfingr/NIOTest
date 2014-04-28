package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by zxt on 2014/4/23.
 */
public class ReadThread extends Thread {
    private static final Logger LOGGER = Logger.getLogger("ReadThread");
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
                //read from tcp
                line = in.readLine();
                if (null != line) {
                    System.out.println("remote: " + line);
                    if (line.equals("quit")) break;
                } else break;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "remote tcp closed unexpectedly.");
//            e.printStackTrace();
        }
    }
}
