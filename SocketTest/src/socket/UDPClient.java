package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * Created by zxt on 2014/4/27.
 */
public class UDPClient {
    public static final int DEFAULT_PORT = 9;
    private static final String DEFAULT_HOT = "localhost";

    public static void main(String[] args) {
        try {
            InetAddress serverAddr = InetAddress.getByName(DEFAULT_HOT);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            DatagramSocket client = new DatagramSocket();
            while (true){
                String line = reader.readLine();
                if(line.equals("quit")) break;
                byte[] data = line.getBytes("UTF-8");
                DatagramPacket packet = new DatagramPacket(data, data.length, serverAddr, DEFAULT_PORT);
                client.send(packet);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
