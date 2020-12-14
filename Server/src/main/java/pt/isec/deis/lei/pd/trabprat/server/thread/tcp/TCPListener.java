package pt.isec.deis.lei.pd.trabprat.server.thread.tcp;

import java.net.ServerSocket;
import java.net.Socket;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.server.Main;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;

public class TCPListener implements Runnable {

    private final ServerConfig SV_CFG;

    public TCPListener(ServerConfig SV_CFG) {
        this.SV_CFG = SV_CFG;
    }

    @Override
    public void run() {
        String IP;
        while (true) {
            try ( ServerSocket SvSocket = new ServerSocket(SV_CFG.TCPPort)) {
                Main.Log("Bound server TCP socket to", SvSocket.getLocalSocketAddress().toString() + ":" + SvSocket.getLocalPort());

                while (true) {
                    Socket ClSocket = SvSocket.accept();
                    IP = ClSocket.getInetAddress().getHostAddress() + ":" + ClSocket.getPort();
                    Main.Log("Established connection with", IP);
                    Thread td = new Thread(new TCPHandler(ClSocket, IP, SV_CFG));
                    td.setDaemon(true);
                    td.start();
                }
            } catch (Exception ex) {
                ExceptionHandler.ShowException(ex);
            }
        }
    }
}
