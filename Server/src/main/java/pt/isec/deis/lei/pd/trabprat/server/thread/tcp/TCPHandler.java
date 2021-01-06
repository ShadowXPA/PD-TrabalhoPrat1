package pt.isec.deis.lei.pd.trabprat.server.thread.tcp;

import java.io.EOFException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.GenericPair;
import pt.isec.deis.lei.pd.trabprat.server.Main;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;
import pt.isec.deis.lei.pd.trabprat.thread.tcp.TCPHelper;

public class TCPHandler implements Runnable {

    private final Socket ClientSocket;
    private final String IP;
    private final ServerConfig SV_CFG;

    @Override
    public void run() {
        try (ClientSocket) {
            boolean Continue = true;
            // Get streams
            OutputStream oS = ClientSocket.getOutputStream();
            ObjectOutputStream oOS = new ObjectOutputStream(oS);
            InputStream iS = ClientSocket.getInputStream();
            ObjectInputStream oIS = new ObjectInputStream(iS);
            while (Continue) {
                Command cmd = (Command) oIS.readUnshared();
                Main.Log(IP + " to [Server]", "" + cmd.CMD);

                try {
                    Thread td = new Thread(new TCPUserHandler(ClientSocket, oOS, cmd, IP, SV_CFG));
                    td.setDaemon(true);
                    td.start();
                } catch (Exception ex) {
                    // Send internal server error
                    Command cmdErr = new Command(ECommand.CMD_SERVICE_UNAVAILABLE);
                    TCPHelper.SendTCPCommand(oOS, cmd);
                    Main.Log("[Server] to " + IP, "" + cmdErr.CMD);
                }
            }
        } catch (EOFException ex) {
        } catch (Exception ex) {
            ExceptionHandler.ShowException(ex);
        }
        synchronized (SV_CFG) {
            // Removes client if they were logged in
            var client = SV_CFG.Clients.remove(ClientSocket);
            if (client != null) {
                Main.Log("[User: (" + client.key.getUID() + ") "
                        + client.key.getUUsername() + "]", "has disconnected.");
                SV_CFG.BroadcastOnlineActivity();
                SV_CFG.MulticastMessage(new Command(ECommand.CMD_LOGOUT,
                        new GenericPair<>(SV_CFG.ServerID, client.key)));
                try {
                    SV_CFG.broadcastToRMI("User '" + client.key.getUUsername() + "' has logged in!");
                } catch (RemoteException ex) {
                    ExceptionHandler.ShowException(ex);
                }
            }
        }
        Main.Log("Closed connection with", IP);
    }

    public TCPHandler(Socket ClientSocket, String IP, ServerConfig SV_CFG) {
        this.ClientSocket = ClientSocket;
        this.IP = IP;
        this.SV_CFG = SV_CFG;
    }
}
