package pt.isec.deis.lei.pd.trabprat.server;

//import java.net.DatagramPacket;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.net.MulticastSocket;
//import java.net.NetworkInterface;
//import java.util.Scanner;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import pt.isec.deis.lei.pd.trabprat.config.DefaultConfig;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;
import pt.isec.deis.lei.pd.trabprat.server.db.Database;
import pt.isec.deis.lei.pd.trabprat.server.thread.multicast.MulticastListener;
import pt.isec.deis.lei.pd.trabprat.server.thread.tcp.TCPListener;
import pt.isec.deis.lei.pd.trabprat.server.thread.udp.UDPListener;

public class Main {

    private static final SimpleDateFormat sDF = new SimpleDateFormat("EEEEE, MMMMM d, yyyy H:mm");

    public static void main(String[] args) {
//        String IP = "239.4.5.6";
//        int Port = 5432;
//        try {
//            try ( MulticastSocket mS = new MulticastSocket(Port)) {
//                InetAddress address = InetAddress.getByName(IP);
//                NetworkInterface nI = NetworkInterface.getByInetAddress(InetAddress.getByName("10.11.14.7"));
//                mS.setTimeToLive(100);
////                mS.setNetworkInterface(nI);
////                mS.joinGroup(address);
//                mS.joinGroup(new InetSocketAddress(address, Port), nI);
//                Scanner sc = new Scanner(System.in);
//                System.out.println("Username: ");
//                String username = sc.nextLine();
//
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        DatagramPacket dP = new DatagramPacket(new byte[1024], 1024);
//                        try {
//                            while (true) {
//                                dP.setLength(1024);
//                                mS.receive(dP);
//                                String raw = new String(dP.getData(), 0, dP.getLength());
//                                System.out.println(dP.getAddress().getHostAddress()
//                                        + ":" + dP.getPort() + ": " + raw);
//                            }
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//                }).start();
//
//                while (true) {
//                    String msg = sc.nextLine();
//
//                    if (msg.equals("exit")) {
//                        break;
//                    }
//
//                    msg = "[" + username + "]: " + msg;
//                    DatagramPacket dP = new DatagramPacket(msg.getBytes(), msg.getBytes().length,
//                            address, Port);
//                    mS.send(dP);
//                }
//
//                mS.leaveGroup(address);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        ServerConfig SV_CFG;
        // Catch arguments
        // Initialize database connection
        try {
            if (args.length <= 8) {
                System.out.println("Wrong number of arguments!\nPlease use "
                        + "'java -jar Server.jar [InternalIP] [MulticastPort] "
                        + "[UDPPort] [TCPPort] [DBHost] [DBPort] [DBSchema] "
                        + "[DBUser] [DBPassword]'");
                System.exit(-1);
            }

            SV_CFG = new ServerConfig(InitDatabase(args), DefaultConfig.getExternalIP(), args[0],
                    Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            System.out.println("External IP: " + SV_CFG.ExternalIP.getHostAddress());
            System.out.println("Server ID: " + SV_CFG.ServerID);
            // Check for other servers, ask for information if there are other servers already online
            // Thread Multicast
            Thread tdMC = new Thread(new MulticastListener(SV_CFG), "MulticastListener");
            tdMC.setDaemon(true);
            tdMC.start();
            synchronized (SV_CFG) {
                SV_CFG.wait();
            }

            // Create threads
            // Thread Listen UDP
            Thread tdUDP = new Thread(new UDPListener(SV_CFG), "UDPListener");
            tdUDP.setDaemon(true);
            tdUDP.start();
            // Thread Listen TCP
            Thread tdTCP = new Thread(new TCPListener(SV_CFG), "TCPListener");
            tdTCP.setDaemon(true);
            tdTCP.start();
            try {
                // Handle Admin Commands
                new CommandLineHandler(System.in, System.out, SV_CFG).Initialize();
            } catch (Exception ex) {
                ExceptionHandler.ShowException(ex);
            }
        } catch (Exception ex) {
            ExceptionHandler.ShowException(ex);
        }
    }

    private static Database InitDatabase(String[] args) throws SQLException, ClassNotFoundException {
        String DBHost = args[4];
        String DBPort = args[5];
        String DBSchema = args[6];
        String DBUser = args[7];
        String DBPassword = args[8];
        return new Database(DBHost, DBPort, DBSchema, DBUser, DBPassword);
    }

    public static void Log(String Prefix, String Message) {
        System.out.print(sDF.format(new Date()) + " ");
        System.out.println(Prefix + ": " + Message);
        System.out.print("Admin: ");
    }
}
