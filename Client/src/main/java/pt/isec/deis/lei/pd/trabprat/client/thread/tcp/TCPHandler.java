package pt.isec.deis.lei.pd.trabprat.client.thread.tcp;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.TUser;
import pt.isec.deis.lei.pd.trabprat.thread.tcp.TCPHelper;

public class TCPHandler implements Runnable {

    private final Socket socket;
    private final ObjectOutputStream OOS; //ObjectOutputStream para o TCP
    private final ObjectInputStream OIS;  //ObjectInputStream para o TCP
    private final Command command;

    public TCPHandler(Socket socket, ObjectOutputStream OOS, ObjectInputStream OIS, Command command) {
        this.socket = socket;
        this.OOS = OOS;
        this.OIS = OIS;
        this.command = command;
    }

    @Override
    public void run() {
        Command sendCmd;
        try {
            switch (command.CMD) {
                case ECommand.CMD_SERVICE_UNAVAILABLE: {

                    break;
                }
                case ECommand.CMD_CREATED: {
                    if(command.Body instanceof TUser){
                        System.out.println("Utilizador criado!");
                    }
                    break;
                }
                case ECommand.CMD_BAD_REQUEST: {

                    break;
                }

                case ECommand.CMD_LOGIN: {
                    //HandleLogin();
                    // Check if user exists in the database
                    // Check if password is good and matches
                    // Send OK to the client
                    // Add user to the client list
                    // Announce to other servers via multicast
                    break;
                }
                default: {
                    
                }
            }
        } catch (Exception ex) {
            ExceptionHandler.ShowException(ex);
        }
    }
}
