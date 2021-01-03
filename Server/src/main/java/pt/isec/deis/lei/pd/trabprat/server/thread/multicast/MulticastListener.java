package pt.isec.deis.lei.pd.trabprat.server.thread.multicast;

import java.io.File;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.comparator.ServerStartComparator;
import pt.isec.deis.lei.pd.trabprat.config.DefaultConfig;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.GenericPair;
import pt.isec.deis.lei.pd.trabprat.model.Server;
import pt.isec.deis.lei.pd.trabprat.rmi.RemoteServerRMI;
import pt.isec.deis.lei.pd.trabprat.server.Main;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;
import pt.isec.deis.lei.pd.trabprat.server.explorer.ExplorerController;
import pt.isec.deis.lei.pd.trabprat.thread.udp.UDPHelper;

public class MulticastListener implements Runnable {

    private final int ServerLookupTimeout = 1000;
    private final int ServerHeartbeatTimeout = 10000;
    private final int ServerDBSyncTimeout = 5000;
    private final ServerConfig SV_CFG;
    private final InetAddress InternalIP;
    private InetAddress iA;
    private final int Port;

    public MulticastListener(ServerConfig SV_CFG) {
        this.SV_CFG = SV_CFG;
        this.InternalIP = this.SV_CFG.InternalIP;
        this.Port = SV_CFG.MulticastPort;
    }

    @Override
    public void run() {
        String IP;
        try ( MulticastSocket mCS = new MulticastSocket(Port)) {
            Main.Log("Bound server Multicast socket to", mCS.getLocalSocketAddress().toString() + ":" + mCS.getLocalPort());
            iA = InetAddress.getByName(DefaultConfig.DEFAULT_MULTICAST_IP);
            NetworkInterface nI = NetworkInterface.getByInetAddress(this.InternalIP);
            mCS.joinGroup(new InetSocketAddress(iA, Port), nI);
            Main.Log("Joined Multicast group", DefaultConfig.DEFAULT_MULTICAST_IP + ":" + Port);
            synchronized (SV_CFG) {
                SV_CFG.MCSocket = mCS;
                SV_CFG.MCAddress = iA;
                Server thisSv = new Server(SV_CFG.ServerID, SV_CFG.ServerStart,
                        SV_CFG.ExternalIP, SV_CFG.UDPPort, SV_CFG.TCPPort, 0);
                try {
                    Main.Log("[Server]", "Looking for other servers online...");
                    UDPHelper.SendMulticastCommand(mCS, iA, Port,
                            new Command(ECommand.CMD_HELLO,
                                    new GenericPair<>(SV_CFG.ServerID,
                                            thisSv)));
                    SV_CFG.wait(ServerLookupTimeout);
                } catch (Exception ex) {
                } finally {
                    Main.Log("[Server]", "Seems like no other server is running...");
                }

                // TODO: Verify if there are more servers
                // create registry or get registry
                // rebind to registry service name: ServerID + "_" + RMIName
                if (!SV_CFG.ServerList.isEmpty()) {
                    SV_CFG.ServerList.sort(new ServerStartComparator());
                    SV_CFG.SortServerList();
                    Main.Log("[Server]", "Synchronizing...");
                    // Initiate synchronization
                    Server syncSv = SV_CFG.ServerList.get(0);
                    // Erase DB and files
                    SV_CFG.DB.devEraseDatabase();
                    String BaseDir = SV_CFG.DBConnection.getSchema() + ExplorerController.BASE_DIR;
                    File AvatarDir = new File(BaseDir + ExplorerController.AVATAR_SUBDIR);
                    File[] AvatarFiles = AvatarDir.listFiles();
                    for (File f : AvatarFiles) {
                        f.delete();
                    }
                    File FilesDir = new File(BaseDir + ExplorerController.FILES_SUBDIR);
                    File[] FilesFiles = FilesDir.listFiles();
                    for (File f : FilesFiles) {
                        f.delete();
                    }
                    // Ask server for synchronization
                    UDPHelper.SendUDPCommand(mCS, syncSv.getAddress(),
                            syncSv.getUDPPort(), new Command(ECommand.CMD_SYNC,
                            new GenericPair<>(SV_CFG.ServerID, thisSv)));
                    // Wait
                    synchronized (SV_CFG.DB) {
                        SV_CFG.DB.wait(ServerDBSyncTimeout);
                    }
                    Main.Log("[Server]", "Synchronization finished...");
                    try {
                        SV_CFG.registry = LocateRegistry.getRegistry(SV_CFG.InternalIP.getHostAddress(), DefaultConfig.DEFAULT_RMI_PORT);
                    } catch (RemoteException ex) {
                        SV_CFG.registry = LocateRegistry.createRegistry(DefaultConfig.DEFAULT_RMI_PORT);
                    }
                } else {
                    SV_CFG.registry = LocateRegistry.createRegistry(DefaultConfig.DEFAULT_RMI_PORT);
                }
                SV_CFG.registry.rebind(SV_CFG.ServerID + "_" + RemoteServerRMI.SERVICE_NAME, SV_CFG.serverRMI);

                SV_CFG.notifyAll();
            }
            // Create heartbeat thread
            Thread td2 = new Thread(() -> {
                SendHeartbeat(mCS);
            });
            td2.setName("Multicast Heartbeat");
            td2.setDaemon(true);
            td2.start();
            // Listen for multicast packets
            while (true) {
                try {
                    DatagramPacket ReceivedPacket = new DatagramPacket(new byte[DefaultConfig.DEFAULT_UDP_PACKET_SIZE * 16], DefaultConfig.DEFAULT_UDP_PACKET_SIZE * 16);
                    mCS.receive(ReceivedPacket);
                    IP = ReceivedPacket.getAddress().getHostAddress() + ":" + ReceivedPacket.getPort();
                    Command cmd = UDPHelper.ReadMulticastCommand(ReceivedPacket);
                    String SvID = ((GenericPair<String, ?>) cmd.Body).key;

                    if (!SvID.equals(SV_CFG.ServerID)) {
                        try {
                            // Create handler threads
                            Thread td = new Thread(new MulticastHandler(SV_CFG, mCS, SvID, cmd));
                            td.setDaemon(true);
                            td.start();
                        } catch (Exception ex) {
                            ExceptionHandler.ShowException(ex);
                        }
                    }
                } catch (Exception ex) {
                    ExceptionHandler.ShowException(ex);
                }
            }
        } catch (Exception ex) {
            ExceptionHandler.ShowException(ex);
        }
    }

    private void SendHeartbeat(final MulticastSocket mCS) {
        InetAddress iAdd;
        int udpPort;
        int tcpPort;
        String serverID;
        long serverStart;
        synchronized (SV_CFG) {
            iAdd = SV_CFG.ExternalIP;
            udpPort = SV_CFG.UDPPort;
            tcpPort = SV_CFG.TCPPort;
            serverID = SV_CFG.ServerID;
            serverStart = SV_CFG.ServerStart;
        }
        Server sv = new Server(serverID, serverStart, iAdd, udpPort, tcpPort, 0);
        GenericPair<String, Server> svP = new GenericPair<>(SV_CFG.ServerID, sv);
        Command cmd = new Command(ECommand.CMD_HEARTBEAT, svP);
        while (!mCS.isClosed()) {
            try {
                synchronized (SV_CFG) {
                    svP.value.setUserCount(SV_CFG.Clients.size());
                    SV_CFG.SetServersDead();
                }
                UDPHelper.SendMulticastCommand(mCS, iA, Port, cmd);
                Thread.sleep(ServerHeartbeatTimeout);
                synchronized (SV_CFG) {
                    SV_CFG.RemoveDeadServers();
                    SV_CFG.BroadcastServerList();
                }
            } catch (Exception ex) {
                ExceptionHandler.ShowException(ex);
            }
        }
    }
}
