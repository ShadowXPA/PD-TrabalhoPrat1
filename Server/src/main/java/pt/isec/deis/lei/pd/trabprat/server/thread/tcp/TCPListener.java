package pt.isec.deis.lei.pd.trabprat.server.thread.tcp;

import java.net.ServerSocket;
import java.net.Socket;
import pt.isec.deis.lei.pd.trabprat.config.DefaultConfig;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.server.Main;

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
                ExceptionHandler.ShowException(ex);
            }
        }
    }
}
