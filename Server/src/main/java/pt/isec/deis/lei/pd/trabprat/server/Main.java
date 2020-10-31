package pt.isec.deis.lei.pd.trabprat.server;

import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;
import pt.isec.deis.lei.pd.trabprat.server.db.Database;
import pt.isec.deis.lei.pd.trabprat.server.thread.tcp.TCPListener;
import pt.isec.deis.lei.pd.trabprat.server.thread.udp.UDPListener;

public class Main {

    public static ServerConfig SV_CFG;
    public static final Object SV_LOCK = new Object();

    public static void main(String[] args) {
        // Catch arguments
        // Initialize database connection
        // Check for other servers, ask for information if there are other servers already online
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
            SV_CFG = new ServerConfig(db);
        } catch (Exception ex) {
            ExceptionHandler.ShowException(ex);
        }

        // Create threads
        // Thread Listen UDP
        Thread tdUDP = new Thread(new UDPListener(), "UDPListener");
        tdUDP.setDaemon(true);
        tdUDP.start();
        // Thread Listen TCP
        Thread tdTCP = new Thread(new TCPListener(), "TCPListener");
        tdTCP.setDaemon(true);
        tdTCP.start();
        // Thread Multicast
//        Thread tdMC = new Thread();
//        tdMC.setDaemon(true);
//        tdMC.start();
        try {
            // Handle Admin Commands
            new CommandLineHandler(System.in, System.out).Initialize();
        } catch (Exception ex) {
            ExceptionHandler.ShowException(ex);
        }
    }

    public static void Log(String Prefix, String Message) {
        System.out.println(Prefix + ": " + Message);
        System.out.print("Admin: ");
    }
}
