package pt.isec.deis.lei.pd.trabprat.server.thread.tcp;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.isec.deis.lei.pd.trabprat.config.DefaultConfig;
import pt.isec.deis.lei.pd.trabprat.server.Main;
import pt.isec.deis.lei.pd.trabprat.server.thread.udp.UDPListener;

public class TCPListener implements Runnable {

    @Override
    public void run() {
        String IP;
        while (true) {
            try ( ServerSocket SvSocket = new ServerSocket(DefaultConfig.DEFAULT_TCP_PORT)) {
                Main.Log("Bound server TCP socket to", SvSocket.getLocalSocketAddress().toString() + ":" + SvSocket.getLocalPort());

                while (true) {
                    Socket ClSocket = SvSocket.accept();
                    IP = ClSocket.getInetAddress().getHostAddress() + ":" + ClSocket.getPort();
                    Main.Log("Established connection with", IP);
                    Thread td = new Thread(new TCPHandler(ClSocket, IP));
                    td.setDaemon(true);
                    td.start();
                }
            } catch (Exception ex) {
                Logger.getLogger(UDPListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
