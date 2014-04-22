package main;

import java.io.*;
import java.net.Socket;

/**
 * Created by zxt on 2014/4/22.
 *
 * Exception:
 * java.net.ConnectException: Connection refused: connect
 * port 79 is not serving?
 */
public class FingerClient {
    public static final int DEFAULT_PORT = 79;

    public static void main(String[] args) throws IOException {
        String host = "localhost";
        if (args.length > 0)
            host = args[0];
        Socket conn = null;
        try {
            conn = new Socket(host, DEFAULT_PORT);
            Writer out = new OutputStreamWriter(conn.getOutputStream(), "8859_1");
            for (int i = 1; i < args.length; i++) {
                out.write(args[i] + " ");
            }
            out.write("\r\n");
            out.flush();
            InputStream raw = conn.getInputStream();
            InputStreamReader in = new InputStreamReader(new BufferedInputStream(raw), "8859_1");
            int c;
            while (-1 != (c = in.read())) {
                if ((c >= 32 && c < 127) || c == '\t' || c == '\r' || c == '\n') {
                    System.out.write(c);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != conn)
                conn.close();
        }
    }
}
