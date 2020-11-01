package pt.isec.deis.lei.pd.trabprat.client.thread.tcp;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;

public class TCPListener implements Runnable{
    private final Socket socket;
    private final ObjectOutputStream OOS; //ObjectOutputStream para o TCP
    private final ObjectInputStream OIS;  //ObjectInputStream para o TCP
    
    public TCPListener(Socket socket, ObjectOutputStream OOS, ObjectInputStream OIS){
        this.socket = socket;
        this.OOS = OOS;
        this.OIS = OIS;
    }
    
    @Override
    public void run() {
        Command command;
        while (true) {
            try{
                command = (Command) OIS.readUnshared();
                Thread thread = new Thread(new TCPHandler(socket, OOS, OIS, command));
                thread.setDaemon(true);
                thread.start();
            }catch(Exception ex){
                ExceptionHandler.ShowException(ex);
            }
        }
    }
    
}
