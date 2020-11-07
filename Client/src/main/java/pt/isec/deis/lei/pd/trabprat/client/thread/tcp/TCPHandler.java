package pt.isec.deis.lei.pd.trabprat.client.thread.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import pt.isec.deis.lei.pd.trabprat.client.controller.ServerController;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.TUser;

public class TCPHandler implements Runnable {

    private final Socket socket;
    private final ObjectOutputStream oOS; //ObjectOutputStream para o TCP
    private final ObjectInputStream oIS;  //ObjectInputStream para o TCP
    private final Command command;

    public TCPHandler(Socket socket, ObjectOutputStream oOS, ObjectInputStream oIS, Command command) throws IOException {
        this.socket = socket;
        this.oOS = oOS;
        this.oIS = oIS;
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
                    if (command.Body instanceof TUser) {
                        TUser user = (TUser) command.Body;
                        System.out.println("Utilizador criado!");
                        // Send File
                        ServerController.SendFile(user.getUPhoto(), user.getUUsername(), null);
                    }
                    break;
                }
                case ECommand.CMD_BAD_REQUEST: {

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
