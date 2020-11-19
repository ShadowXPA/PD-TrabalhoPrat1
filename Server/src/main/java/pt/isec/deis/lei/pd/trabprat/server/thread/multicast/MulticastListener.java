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
    }

    @Override
    public void run() {
        String IP;
        Port = DefaultConfig.DEFAULT_MULTICAST_PORT;
        try ( MulticastSocket mCS = new MulticastSocket(Port)) {
            Main.Log("Bound server Multicast socket to", mCS.getLocalSocketAddress().toString() + ":" + mCS.getLocalPort());
            iA = InetAddress.getByName(DefaultConfig.DEFAULT_MULTICAST_IP);
            NetworkInterface nI = NetworkInterface.getByInetAddress(this.InternalIP);
            mCS.joinGroup(new InetSocketAddress(iA, DefaultConfig.DEFAULT_MULTICAST_PORT), nI);
            Main.Log("Joined Multicast group", DefaultConfig.DEFAULT_MULTICAST_IP + ":" + Port);
            DatagramPacket ReceivedPacket = new DatagramPacket(new byte[DefaultConfig.DEFAULT_UDP_PACKET_SIZE], DefaultConfig.DEFAULT_UDP_PACKET_SIZE);
            // Create heartbeat thread
            Thread td2 = new Thread(() -> {
                SendHeartbeat(mCS);
            });
            td2.setDaemon(true);
            td2.start();
            // Listen for multicast packets
            while (true) {
                ReceivedPacket.setLength(DefaultConfig.DEFAULT_UDP_PACKET_SIZE);
                mCS.receive(ReceivedPacket);
                IP = ReceivedPacket.getAddress().getHostAddress() + ":" + ReceivedPacket.getPort();
                Main.Log("Received Multicast Packet from", IP);

                try {
                    // Create handler threads
                    Thread td = new Thread(new MulticastHandler(SV_CFG, mCS, ReceivedPacket, IP));
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

    private void SendHeartbeat(final MulticastSocket mCS) {
        Command cmd;
        while (true) {
            try {
                Thread.sleep(10000);
                // Clear list
                int userCount;
                synchronized (SV_CFG) {
//                    userCount = SV_CFG.ClientList.size();
                    userCount = SV_CFG.Clients.size();
                }
                cmd = new Command(ECommand.CMD_HEARTBEAT, new Server(SV_CFG.ExternalIP,
                        DefaultConfig.DEFAULT_UDP_PORT, DefaultConfig.DEFAULT_TCP_PORT, userCount));
                UDPHelper.SendMulticastCommand(mCS, iA, Port, cmd);
            } catch (Exception ex) {
                ExceptionHandler.ShowException(ex);
            }
        }
    }
}
