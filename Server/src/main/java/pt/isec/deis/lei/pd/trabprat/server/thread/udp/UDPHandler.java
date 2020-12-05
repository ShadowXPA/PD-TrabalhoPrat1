package pt.isec.deis.lei.pd.trabprat.server.thread.udp;

import pt.isec.deis.lei.pd.trabprat.thread.udp.UDPHelper;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.Server;
import pt.isec.deis.lei.pd.trabprat.server.Main;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;

public class UDPHandler implements Runnable {

    private final DatagramSocket ServerSocket;
    private final DatagramPacket ReceivedPacket;
    private final String IP;
    private final ServerConfig SV_CFG;

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
        Server Accept = null; // Get response via multicast
        ArrayList<Server> body = new ArrayList<>();
        Server thisSv;
        synchronized (SV_CFG) {
            thisSv = new Server(SV_CFG.ServerID, SV_CFG.ServerStart, SV_CFG.ExternalIP, SV_CFG.UDPPort, SV_CFG.TCPPort, SV_CFG.Clients.size());
            Accept = thisSv;
            body.add(thisSv);//SV_CFG.ClientList.size()));
            body.addAll(SV_CFG.ServerList);
            if (body.size() > 1) {
                body.sort(SV_CFG.SvComp);
                Server lowestCapSv = body.get(0);
                if ((thisSv.getUserCount() * 0.5) >= lowestCapSv.getUserCount()) {
                    Accept = lowestCapSv;
                }
            }
        }
        if (thisSv.equals(Accept)) {
            // Accepted
            cmd = new Command(ECommand.CMD_ACCEPTED, body);
            UDPHelper.SendUDPCommand(ServerSocket, ReceivedPacket.getAddress(), ReceivedPacket.getPort(), cmd);
        } else {
            // Rejected
            cmd = new Command(ECommand.CMD_MOVED_PERMANENTLY, Accept);
            UDPHelper.SendUDPCommand(ServerSocket, ReceivedPacket.getAddress(), ReceivedPacket.getPort(), cmd);
        }
        Main.Log("[Server] to " + IP, "" + cmd.CMD);
    }

    public UDPHandler(DatagramSocket ServerSocket, DatagramPacket ReceivedPacket, String IP, ServerConfig SV_CFG) throws IOException {
        this.ReceivedPacket = new DatagramPacket(ReceivedPacket.getData(),
                ReceivedPacket.getOffset(), ReceivedPacket.getLength(),
                ReceivedPacket.getAddress(), ReceivedPacket.getPort());
        this.ServerSocket = ServerSocket;
        this.IP = IP;
        this.SV_CFG = SV_CFG;
    }
}
