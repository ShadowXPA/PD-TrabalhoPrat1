package pt.isec.deis.lei.pd.trabprat.server.thread.multicast;

import java.net.DatagramPacket;
import java.net.MulticastSocket;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.Server;
import pt.isec.deis.lei.pd.trabprat.server.Main;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;
import pt.isec.deis.lei.pd.trabprat.thread.udp.UDPHelper;

public class MulticastHandler implements Runnable {

    private final ServerConfig SV_CFG;
    private final MulticastSocket mCS;
    private final DatagramPacket ReceivedPacket;
    private final String IP;

    public MulticastHandler(ServerConfig SV_CFG, MulticastSocket mCS, DatagramPacket ReceivedPacket, String IP) {
        this.ReceivedPacket = new DatagramPacket(ReceivedPacket.getData(),
                ReceivedPacket.getOffset(), ReceivedPacket.getLength(),
                ReceivedPacket.getAddress(), ReceivedPacket.getPort());
        this.SV_CFG = SV_CFG;
        this.mCS = mCS;
        this.IP = IP;
    }

    @Override
    public void run() {
        try {
            // Read command
            Command cmd = UDPHelper.ReadMulticastCommand(ReceivedPacket);
            Main.Log(IP + " to [Server]", "" + cmd.CMD);

            // React accordingly
            switch (cmd.CMD) {
                case ECommand.CMD_HEARTBEAT: {
                    HandleHeartbeat(cmd);
                    break;
                }
                default: {
                    // Send CMD_FORBIDDEN command
                    //UDPHelper.SendMulticastCommand(ServerSocket, ReceivedPacket.getAddress(), ReceivedPacket.getPort(), new Command(ECommand.CMD_FORBIDDEN));
                    break;
                }
            }
        } catch (Exception ex) {
            ExceptionHandler.ShowException(ex);
        }
    }

    private void HandleHeartbeat(Command cmd) {
        // Add or update server info
        synchronized (SV_CFG) {
            SV_CFG.AddOrUpdateServer((Server) cmd.Body);
        }
    }
}
