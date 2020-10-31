package pt.isec.deis.lei.pd.trabprat.server.thread.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
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
                    UDPHelper.SendUDPCommand(ServerSocket, ReceivedPacket, new Command(ECommand.CMD_FORBIDDEN));
                    break;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(UDPHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void HandleConnect(String IP) throws IOException {
        // Ask other servers via multicast if they have less than 50% of the load (use lock)
        Command cmd;
        Object Accept = null; // Get response via multicast
        if (Accept == null) {
            // Accepted
            cmd = new Command(ECommand.CMD_ACCEPTED);
            UDPHelper.SendUDPCommand(ServerSocket, ReceivedPacket, cmd);
        } else {
            // Rejected
            cmd = new Command(ECommand.CMD_MOVED_PERMANENTLY, Accept);
            UDPHelper.SendUDPCommand(ServerSocket, ReceivedPacket, cmd);
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
