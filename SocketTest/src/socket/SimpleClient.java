package socket;

import util.ReadThread;
import util.WriteThread;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 14-4-19.
 */
public class SimpleClient {
    public static void main(String[] args) {
        // Passing null to getByName() produces the special "Local Loopback"
        // IP address, for testing on one machine w/o a network:
        InetAddress addr = null;
        try {
            addr = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Socket socket = null;
        try {
            socket = new Socket(addr, SimpleServer.DEFAULT_PORT);
            System.out.println("[INFO] connected to addr: " + addr + ",  socket: " + socket);

            Thread readThread = new ReadThread(socket.getInputStream());
            readThread.start();
            Thread writeThread = new WriteThread(socket.getOutputStream());
            writeThread.start();
            //wait for input and output complete
            try {
                readThread.join();
                writeThread.join();
                System.out.println("[INFO]IO finished,  closing socket...");
                socket.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
