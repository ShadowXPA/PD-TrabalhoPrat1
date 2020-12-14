package pt.isec.deis.lei.pd.trabprat.client.thread.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import pt.isec.deis.lei.pd.trabprat.client.App;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;

public class TCPListener implements Runnable {
    private final Socket socket; //socket para o TCP
    private final ObjectOutputStream oOS; //ObjectOutputStream para o TCP
    private final ObjectInputStream oIS;  //ObjectInputStream para o TCP

    public TCPListener(Socket socket, ObjectOutputStream oOS, ObjectInputStream oIS) throws IOException {
        this.socket = socket;
        this.oOS = oOS;
        this.oIS = oIS;
    }

    @Override
    public void run() {
        Command cmd;
        try {
            boolean Continue = true;
            while (Continue) {
                cmd = (Command) oIS.readUnshared();
                System.out.println("Command from server: " + cmd);
                try {
                    Thread td = new Thread(new TCPHandler(socket, oOS, oIS, cmd));
                    td.setDaemon(true);
                    td.start();
                } catch (Exception ex) {
                    ExceptionHandler.ShowException(ex);
                }
            }
        } catch (Exception ex) {
            //Mecanismo Failover
            ExceptionHandler.ShowException(ex);
            if (!App.CL_CFG.closing && !App.CL_CFG.ServerList.isEmpty()) {
                App.CL_CFG.server = App.CL_CFG.ServerList.get(0);
                App.connectionToServer(null);
            }else{
                System.exit(-1);
            }
        }
    }
}
