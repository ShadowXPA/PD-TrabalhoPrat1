package pt.isec.deis.lei.pd.trabprat.server.thread.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.GenericPair;
import pt.isec.deis.lei.pd.trabprat.model.Server;
import pt.isec.deis.lei.pd.trabprat.server.Main;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;
import pt.isec.deis.lei.pd.trabprat.thread.udp.UDPHelper;

public class MulticastHandler implements Runnable {

    private final ServerConfig SV_CFG;
    private final MulticastSocket mCS;
    private final DatagramPacket ReceivedPacket;
    private final String SvID;
    private final Command cmd;

    public MulticastHandler(ServerConfig SV_CFG, MulticastSocket mCS, DatagramPacket ReceivedPacket, String SvID, Command cmd) {
//        this.ReceivedPacket = new DatagramPacket(ReceivedPacket.getData(),
//                ReceivedPacket.getOffset(), ReceivedPacket.getLength(),
//                ReceivedPacket.getAddress(), ReceivedPacket.getPort());
        this.ReceivedPacket = ReceivedPacket;
        this.SV_CFG = SV_CFG;
        this.mCS = mCS;
        this.SvID = SvID;
        this.cmd = cmd;
    }

    @Override
    public void run() {
        try {
            // Read command
            Main.Log("[" + SvID + "]", "" + cmd.CMD);

            // React accordingly
            switch (cmd.CMD) {
                case ECommand.CMD_HELLO: {
                    HandleHello(cmd);
                    break;
                }
                case ECommand.CMD_BYE: {
                    HandleBye(cmd);
                    break;
                }
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
            SV_CFG.AddOrUpdateServer(((GenericPair<String, Server>) cmd.Body).value);
        }
    }

    private void HandleHello(Command cmd) throws IOException {
        GenericPair<String, Server> v;
        Server s = ((GenericPair<String, Server>) cmd.Body).value;
        synchronized (SV_CFG) {
            v = new GenericPair<>(SV_CFG.ServerID, new Server(SV_CFG.ServerID,
                    SV_CFG.ServerStart, SV_CFG.ExternalIP, SV_CFG.UDPPort,
                    SV_CFG.TCPPort, SV_CFG.Clients.size()));
        }
        UDPHelper.SendUDPCommand(mCS, s.getAddress(),
                s.getUDPPort(), new Command(ECommand.CMD_HELLO, v));
    }

    private void HandleBye(Command cmd) {
        Server s = ((GenericPair<String, Server>) cmd.Body).value;
        synchronized (SV_CFG) {
            SV_CFG.ServerList.remove(s);
        }
    }
}
