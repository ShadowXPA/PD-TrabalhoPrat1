package pt.isec.deis.lei.pd.trabprat.server.thread.multicast;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.config.DefaultConfig;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.GenericPair;
import pt.isec.deis.lei.pd.trabprat.model.Server;
import pt.isec.deis.lei.pd.trabprat.server.Main;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;
import pt.isec.deis.lei.pd.trabprat.thread.udp.UDPHelper;

public class MulticastListener implements Runnable {

    private final ServerConfig SV_CFG;
    private final InetAddress InternalIP;
    private InetAddress iA;
    private int Port;

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
            DatagramPacket ReceivedPacket = new DatagramPacket(new byte[DefaultConfig.DEFAULT_UDP_PACKET_SIZE], DefaultConfig.DEFAULT_UDP_PACKET_SIZE);
            // TODO: Synchronize servers
            synchronized (SV_CFG) {
                SV_CFG.notifyAll();
            }
            // Create heartbeat thread
            Thread td2 = new Thread(() -> {
                SendHeartbeat(mCS);
            });
            td2.setDaemon(true);
            td2.start();
            // Listen for multicast packets
            Command cmd;
            String SvID;
            while (true) {
                ReceivedPacket.setLength(DefaultConfig.DEFAULT_UDP_PACKET_SIZE);
                mCS.receive(ReceivedPacket);
                IP = ReceivedPacket.getAddress().getHostAddress() + ":" + ReceivedPacket.getPort();
                cmd = UDPHelper.ReadMulticastCommand(ReceivedPacket);
                SvID = ((GenericPair<String, ?>) cmd.Body).key;

                if (!SvID.equals(SV_CFG.ServerID)) {
                    Main.Log("Received Multicast Packet from", IP);
                    Main.Log("Server ID", SvID);
                    try {
                        // Create handler threads
                        Thread td = new Thread(new MulticastHandler(SV_CFG, mCS, ReceivedPacket, SvID, cmd));
                        td.setDaemon(true);
                        td.start();
                    } catch (Exception ex) {
                        ExceptionHandler.ShowException(ex);
                    }
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
        synchronized (SV_CFG) {
            iAdd = SV_CFG.ExternalIP;
            udpPort = SV_CFG.UDPPort;
            tcpPort = SV_CFG.TCPPort;
            serverID = SV_CFG.ServerID;
        }
        Server sv = new Server(serverID, iAdd, udpPort, tcpPort, 0);
        GenericPair<String, Server> svP = new GenericPair<>(SV_CFG.ServerID, sv);
        Command cmd = new Command(ECommand.CMD_HEARTBEAT, svP);
        while (true) {
            try {
                Thread.sleep(30000);
                // Clear list
                synchronized (SV_CFG) {
                    svP.value.setUserCount(SV_CFG.Clients.size());
//                    Server sv = new Server(serverID, iAdd, udpPort, tcpPort, SV_CFG.Clients.size());
//                    GenericPair<String, Server> svP = new GenericPair<>(sv.ServerID, sv);
//                    Command cmd = new Command(ECommand.CMD_HEARTBEAT, svP);
                }
                UDPHelper.SendMulticastCommand(mCS, iA, Port, cmd);
            } catch (Exception ex) {
                ExceptionHandler.ShowException(ex);
            }
        }
    }
}
