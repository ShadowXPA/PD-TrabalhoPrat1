package pt.isec.deis.lei.pd.trabprat.server.thread.tcp;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.server.Main;

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
                // React accordingly
                // Send via Multicast every info necessary
            }
        } catch (Exception ex) {
            Logger.getLogger(TCPHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public TCPHandler(Socket ClientSocket, String IP) {
        this.ClientSocket = ClientSocket;
        this.IP = IP;
    }
}
