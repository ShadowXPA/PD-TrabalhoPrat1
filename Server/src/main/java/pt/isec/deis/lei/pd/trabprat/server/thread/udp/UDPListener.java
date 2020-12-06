package pt.isec.deis.lei.pd.trabprat.server.thread.udp;

import pt.isec.deis.lei.pd.trabprat.thread.udp.UDPHelper;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.config.DefaultConfig;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.server.Main;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;

public class UDPListener implements Runnable {

    private final ServerConfig SV_CFG;

    public UDPListener(ServerConfig SV_CFG) {
        this.SV_CFG = SV_CFG;
    }

    @Override
    public void run() {
        String IP;
        while (true) {
            try ( DatagramSocket ServerSocket = new DatagramSocket(SV_CFG.UDPPort)) {
                Main.Log("Bound server UDP socket to", ServerSocket.getLocalSocketAddress().toString() + ":" + ServerSocket.getLocalPort());

                while (true) {
                    // Receive UDP packet
                    DatagramPacket ReceivedPacket = new DatagramPacket(new byte[DefaultConfig.DEFAULT_UDP_PACKET_SIZE], DefaultConfig.DEFAULT_UDP_PACKET_SIZE);
                    ServerSocket.receive(ReceivedPacket);
                    IP = ReceivedPacket.getAddress().getHostAddress() + ":" + ReceivedPacket.getPort();
                    Main.Log("Received UDP Packet from", IP);

                    try {
                        Thread td = new Thread(new UDPHandler(ServerSocket, ReceivedPacket, IP, SV_CFG));
                        td.setDaemon(true);
                        td.start();
                    } catch (Exception ex) {
                        // Send internal server error
                        Command cmd = new Command(ECommand.CMD_SERVICE_UNAVAILABLE);
                        UDPHelper.SendUDPCommand(ServerSocket, ReceivedPacket.getAddress(), ReceivedPacket.getPort(), cmd);
                        Main.Log("[Server] to " + IP, "" + cmd.CMD);
                    }
                }
            } catch (Exception ex) {
                ExceptionHandler.ShowException(ex);
            }
        }
    }
}
