package pt.isec.deis.lei.pd.trabprat.server.thread.udp;

import pt.isec.deis.lei.pd.trabprat.thread.udp.UDPHelper;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.config.DefaultConfig;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.Server;
import pt.isec.deis.lei.pd.trabprat.server.Main;

public class UDPHandler implements Runnable {

    private final DatagramSocket ServerSocket;
    private final DatagramPacket ReceivedPacket;
    private final String IP;

    @Override
    public void run() {
        try {
            // Read command
            Command cmd = UDPHelper.ReadUDPCommand(ReceivedPacket);
            Main.Log(IP + " to [Server]", "" + cmd.CMD);

            // React accordingly
            switch (cmd.CMD) {
                case ECommand.CMD_CONNECT: {
                    HandleConnect(IP);
                    break;
                }
                default: {
                    // Send CMD_FORBIDDEN command
                    UDPHelper.SendUDPCommand(ServerSocket, ReceivedPacket.getAddress(), ReceivedPacket.getPort(), new Command(ECommand.CMD_FORBIDDEN));
                    break;
                }
            }
        } catch (Exception ex) {
            ExceptionHandler.ShowException(ex);
        }
    }

    private void HandleConnect(String IP) throws IOException {
        // Ask other servers via multicast if they have less than 50% of the load (use lock)
        Command cmd;
        Object Accept = null; // Get response via multicast
        if (Accept == null) {
            // Accepted
            ArrayList<Server> body = new ArrayList<>();
            body.add(new Server(InetAddress.getByName(DefaultConfig.getExternalIP()), DefaultConfig.DEFAULT_UDP_PORT, DefaultConfig.DEFAULT_TCP_PORT, 0));
            cmd = new Command(ECommand.CMD_ACCEPTED, body);
            UDPHelper.SendUDPCommand(ServerSocket, ReceivedPacket.getAddress(), ReceivedPacket.getPort(), cmd);
        } else {
            // Rejected
            cmd = new Command(ECommand.CMD_MOVED_PERMANENTLY, Accept);
            UDPHelper.SendUDPCommand(ServerSocket, ReceivedPacket.getAddress(), ReceivedPacket.getPort(), cmd);
        }
        Main.Log("[Server] to " + IP, "" + cmd.CMD);
    }

    public UDPHandler(DatagramSocket ServerSocket, DatagramPacket ReceivedPacket, String IP) throws IOException {
        this.ServerSocket = ServerSocket;
        this.ReceivedPacket = new DatagramPacket(ReceivedPacket.getData(),
                ReceivedPacket.getOffset(), ReceivedPacket.getLength(),
                ReceivedPacket.getAddress(), ReceivedPacket.getPort());
        this.IP = IP;
    }
}
