package pt.isec.deis.lei.pd.trabprat.server;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;
import pt.isec.deis.lei.pd.trabprat.server.db.Database;
import pt.isec.deis.lei.pd.trabprat.server.thread.UDPListener;

public class Main {

    public static ServerConfig SV_CFG;

    public static void main(String[] args) {
        // Catch arguments
        try {
            if (args.length <= 4) {
                System.exit(-1);
            }

            String DBHost = args[0];
            String DBPort = args[1];
            String DBSchema = args[2];
            String DBUser = args[3];
            String DBPassword = args[4];
            String DBConnectionString = "jdbc:mysql://" + DBHost + ":" + DBPort + "/" + DBSchema;
            Database db = new Database(DBConnectionString, DBUser, DBPassword);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Create threads
        // Thread Listen UDP
        Thread td = new Thread(new UDPListener(), "UDPListener");
        td.setDaemon(true);
        td.start();
        // Thread Ping Servers
        // Thread Listen TCP

        // Handle Admin Commands
        boolean Continue = true;
        
        while (Continue) {
            // do this until admin ends
        }
//        try {
//            String IP = "230.0.0.1";
//            int Port = 5432;
//            InetAddress ia = InetAddress.getByName(IP);
//            NetworkInterface ni = NetworkInterface.getByInetAddress(ia);
//            new Thread(() -> {
//                try {
//                    try (DatagramSocket ds = new DatagramSocket(6543)) {
//                        var b = "Olá Mundo".getBytes();
//                        DatagramPacket p = new DatagramPacket(b, b.length, ia, Port);
//                        Thread.sleep(5000);
//                        ds.send(p);
//                    }
//                } catch (Exception ex) {
//                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }).start();
//
//            try (MulticastSocket MSoc = new MulticastSocket(Port)) {
//                SocketAddress sa = new InetSocketAddress(IP, Port);
////                MSoc.joinGroup(ia);
//                MSoc.joinGroup(sa, ni);
//                MSoc.setTimeToLive(30);
//                DatagramPacket p = new DatagramPacket(new byte[1024], 1024);
//                MSoc.receive(p);
//
//                String str = new String(p.getData(), 0, p.getLength());
//                System.out.println("Data: " + str);
//
////                MSoc.leaveGroup(ia);
//                MSoc.leaveGroup(sa, ni);
//            }
//        } catch (Exception ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}
