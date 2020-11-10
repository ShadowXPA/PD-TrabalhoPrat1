package pt.isec.deis.lei.pd.trabprat.client.thread.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javafx.scene.control.Alert;
import pt.isec.deis.lei.pd.trabprat.client.App;
import pt.isec.deis.lei.pd.trabprat.client.controller.ServerController;
import pt.isec.deis.lei.pd.trabprat.client.dialog.ClientDialog;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.LoginPackage;
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
                        ClientDialog.ShowDialog(Alert.AlertType.INFORMATION, "Information Dialog", "User", "The user has been successfully created!");
                    }
                    break;
                }
                case ECommand.CMD_BAD_REQUEST: {
                    if (command.Body instanceof String) {
                        ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Error", (String) command.Body);
                    }
                    break;
                }
                case ECommand.CMD_LOGIN:{
                    LoginPackage LP = (LoginPackage)command.Body;
                    
                    synchronized (App.CL_CFG){
                        App.CL_CFG.OnlineUsers = LP.Users;
                        App.CL_CFG.ChannelsList = LP.Channels;
                        App.CL_CFG.MyUser = LP.LoginAuthor;
                        App.CL_CFG.setLogin();
                        App.CL_CFG.notifyAll();
                    }
                    break;
                }
                case ECommand.CMD_UNAUTHORIZED:{
                    if (command.Body instanceof String) {
                        ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Error", (String) command.Body);
                    }
                    synchronized (App.CL_CFG){
                        App.CL_CFG.notifyAll();
                    }
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
