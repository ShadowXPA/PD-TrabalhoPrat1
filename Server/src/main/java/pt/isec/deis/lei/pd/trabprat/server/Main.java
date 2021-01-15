package pt.isec.deis.lei.pd.trabprat.server;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import pt.isec.deis.lei.pd.trabprat.config.DefaultConfig;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.rmi.RemoteServerRMI;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;
import pt.isec.deis.lei.pd.trabprat.server.db.Database;
import pt.isec.deis.lei.pd.trabprat.server.springboot.MainRestAPI;
import pt.isec.deis.lei.pd.trabprat.server.thread.multicast.MulticastListener;
import pt.isec.deis.lei.pd.trabprat.server.thread.tcp.TCPListener;
import pt.isec.deis.lei.pd.trabprat.server.thread.udp.UDPListener;

public class Main {

    public static final SimpleDateFormat sDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static void main(String[] args) {
        ServerConfig SV_CFG;
        // Catch arguments
        // Initialize database connection
        try {
            if (args.length <= 9) {
                System.out.println("Wrong number of arguments!\nPlease use "
                        + "'java -jar Server.jar [InternalIP] [MulticastPort] "
                        + "[UDPPort] [TCPPort] [DBHost] [DBPort] [DBSchema] "
                        + "[DBUser] [DBPassword] [SpringBootPort]'");
                System.exit(-1);
            }

            SV_CFG = new ServerConfig(InitDatabase(args), DefaultConfig.getExternalIP(), args[0],
                    Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), args[9]);
            System.out.println("External IP: " + SV_CFG.ExternalIP.getHostAddress());
            System.out.println("Server ID: " + SV_CFG.ServerID);
            // Create threads
            // Check for other servers, ask for information if there are other servers already online
            // Thread Listen UDP
            Thread tdUDP = new Thread(new UDPListener(SV_CFG), "UDPListener");
            tdUDP.setDaemon(true);
            tdUDP.start();
            // Thread Multicast
            Thread tdMC = new Thread(new MulticastListener(SV_CFG), "MulticastListener");
            tdMC.setDaemon(true);
            tdMC.start();
            synchronized (SV_CFG) {
                SV_CFG.wait();
            }
            // Thread Listen TCP
            Thread tdTCP = new Thread(new TCPListener(SV_CFG), "TCPListener");
            tdTCP.setDaemon(true);
            tdTCP.start();
            // Thread Springboot
            var mainRestAPI = new MainRestAPI();
            Thread tdSpring = new Thread(mainRestAPI, "Springboot");
            tdSpring.setDaemon(true);
            tdSpring.start();
            mainRestAPI.setServerConfig(SV_CFG);
            try {
                // Handle Admin Commands
                new CommandLineHandler(System.in, System.out, SV_CFG).Initialize();
                if (SV_CFG.registry != null) {
                    SV_CFG.registry.unbind(SV_CFG.ServerID + "_" + RemoteServerRMI.SERVICE_NAME);
                }
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
