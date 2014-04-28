package tcp;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by zxt on 2014/4/23.
 */
public class WriteThread extends Thread {
    private static final Logger LOGGER = Logger.getLogger("WriteThread");
    Writer out;

    public WriteThread(OutputStream outputStream) {
        this.out = new OutputStreamWriter(outputStream);
    }

    @Override
    public void run() {
        super.run();
        String line;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (true) {
                //read from console
                line = in.readLine();
                if (null != line) {
                    out.write(line + "\r\n");
                    out.flush();
                    if (line.equals("quit")) break;
                } else break;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "remote tcp closed unexpectedly.");
//            e.printStackTrace();
        }
    }
}
