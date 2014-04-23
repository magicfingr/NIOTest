package http;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by zxt on 2014/4/23.
 *
 * Note: the file encoding should be the same with content-type in the response http header,
 * or there will be some encoding problems.
 */
public class RequestProcessor implements Runnable {
    private static List<Socket> pool = new LinkedList<Socket>();
    private File docDirectory;
    private Socket request;

    public RequestProcessor(File docDirectory, Socket request) {
        this.docDirectory = docDirectory;
        this.request = request;
    }

    @Override
    public void run() {
        try {
            OutputStream raw = new BufferedOutputStream(request.getOutputStream());
            Writer out = new OutputStreamWriter(raw);
            BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line = in.readLine();
            System.out.println("[INFO] request: " + line);
            StringTokenizer tokenizer = new StringTokenizer(line);

            String fileName;
            String contentType;
            String method = tokenizer.nextToken();
            String version = "";
            if (method.endsWith("GET")) {
                fileName = tokenizer.nextToken();
                contentType = guessContentTypeFromName(fileName);
                if (tokenizer.hasMoreTokens())
                    version = tokenizer.nextToken();
                File file = new File(docDirectory, fileName.substring(1));
                if (file.canRead() && file.getCanonicalPath().startsWith(docDirectory.getPath())) {
                    //read file
                    DataInputStream fs = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                    byte[] data = new byte[(int) file.length()];
                    fs.readFully(data);
                    fs.close();
                    //send MIME head
                    if (version.startsWith("HTTP ")) {
                        out.write("HTTP/1.0 200 OK\r\n");
                        out.write("Date: " + new Date() + "\r\n");
                        out.write("Server: PooledHttpServer/1.0\r\n");
                        out.write("Content-length: " + data.length + "\r\n");
                        out.write("Content-type: " + contentType + "\r\n\r\n");
                        out.flush();
                    }
                    //send content
                    raw.write(data);
                    raw.flush();
                } else { //file not found
                    //send MIME head
                    if (version.startsWith("HTTP ")) {
                        out.write("HTTP/1.0 404 File Not Found\r\n");
                        out.write("Date: " + new Date() + "\r\n");
                        out.write("Server: PooledHttpServer/1.0\r\n");
                        out.write("Content-type: text/html\r\n\r\n");
                    }
                    out.write("<HTML>\r\n");
                    out.write("<HEAD><TITLE>File Not Found</TITLE>\r\n");
                    out.write("<BODY>");
                    out.write("<H1>HTTP Error 404: File Not Found</H1>\r\n");
                    out.write("</BODY></HTML>\r\n");
                    out.flush();
                }
            } else { //not "GET"
                if (version.startsWith("HTTP ")) {
                    out.write("HTTP/1.0 501 Not Implemented\r\n");
                    out.write("Date: " + new Date() + "\r\n");
                    out.write("Server: PooledHttpServer/1.0\r\n");
                    out.write("Content-type: text/html\r\n\r\n");
                }
                out.write("<HTML>\r\n");
                out.write("<HEAD><TITLE>Not Implemented</TITLE>\r\n");
                out.write("<BODY>");
                out.write("<H1>HTTP Error 501: Not Implemented</H1>\r\n");
                out.write("</BODY></HTML>\r\n");
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                request.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String guessContentTypeFromName(String fileName) {
        if (fileName.endsWith(".html") || fileName.endsWith(".htm"))
            return "text.html";
        else if (fileName.endsWith(".gif"))
            return "image/gif";
        else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))
            return "image/jpeg";
        else if (fileName.endsWith(".class"))
            return "application/octet-stream";
        else
            return "text/plain";
    }
}
