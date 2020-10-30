package pt.isec.deis.lei.pd.trabprat.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;

public class Main {
    public static ServerConfig SV_CFG;
    
    public static void main(String[] args) {
        SV_CFG = new ServerConfig();
        
        try {
            String IP = "230.0.0.1";
            int Port = 5432;
            InetAddress ia = InetAddress.getByName(IP);
            NetworkInterface ni = NetworkInterface.getByInetAddress(ia);
            new Thread(() -> {
                try {
                    try (DatagramSocket ds = new DatagramSocket(6543)) {
                        var b = "Olá Mundo".getBytes();
                        DatagramPacket p = new DatagramPacket(b, b.length, ia, Port);
                        Thread.sleep(5000);
                        ds.send(p);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }).start();

            try (MulticastSocket MSoc = new MulticastSocket(Port)) {
                SocketAddress sa = new InetSocketAddress(IP, Port);
//                MSoc.joinGroup(ia);
                MSoc.joinGroup(sa, ni);
                MSoc.setTimeToLive(30);
                DatagramPacket p = new DatagramPacket(new byte[1024], 1024);
                MSoc.receive(p);

                String str = new String(p.getData(), 0, p.getLength());
                System.out.println("Data: " + str);

//                MSoc.leaveGroup(ia);
                MSoc.leaveGroup(sa, ni);
            }
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
