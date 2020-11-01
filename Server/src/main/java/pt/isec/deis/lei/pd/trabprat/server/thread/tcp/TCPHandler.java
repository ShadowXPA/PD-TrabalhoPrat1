package pt.isec.deis.lei.pd.trabprat.server.thread.tcp;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.server.Main;
import pt.isec.deis.lei.pd.trabprat.thread.tcp.TCPHelper;

public class TCPHandler implements Runnable {

    private final Socket ClientSocket;
    private final String IP;

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
                // Wait for a read (while (true) keep reading)
                Command cmd = (Command) oIS.readUnshared();
                Main.Log(IP + " to [Server]", "" + cmd.CMD);

                try {
                    Thread td = new Thread(new TCPUserHandler(ClientSocket, cmd));
                    td.setDaemon(true);
                    td.start();
                } catch (Exception ex) {
                    // Send internal server error
                    Command cmdErr = new Command(ECommand.CMD_SERVICE_UNAVAILABLE);
                    TCPHelper.SendTCPCommand(oOS, cmd);
                    Main.Log("[Server] to " + IP, "" + cmdErr.CMD);
                }
            }
        } catch (Exception ex) {
            ExceptionHandler.ShowException(ex);
        }
    }

    public TCPHandler(Socket ClientSocket, String IP) {
        this.ClientSocket = ClientSocket;
        this.IP = IP;
    }
}
