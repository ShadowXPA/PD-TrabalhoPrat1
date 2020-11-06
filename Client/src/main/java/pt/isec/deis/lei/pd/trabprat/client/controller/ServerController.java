package pt.isec.deis.lei.pd.trabprat.client.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;
import pt.isec.deis.lei.pd.trabprat.client.App;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.config.DefaultConfig;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.FileChunk;
import pt.isec.deis.lei.pd.trabprat.model.TUser;
import pt.isec.deis.lei.pd.trabprat.thread.tcp.TCPHelper;

public final class ServerController {

    private ServerController() {
    }

    public static void Register(TUser User) throws IOException {
        Command command = new Command(ECommand.CMD_REGISTER, User);
        TCPHelper.SendTCPCommand(App.CL_CFG.getOOS(), command);
    }

    public static void SendFile(String Path, String Username, UUID GUID) throws FileNotFoundException {
        ObjectOutputStream OOS = App.CL_CFG.getOOS();
        try ( FileInputStream FIS = new FileInputStream(Path)) {
            Command command;
            while (true) {
                byte[] buffer = new byte[DefaultConfig.DEFAULT_TCP_PACKET_SIZE];
                int nbytes = FIS.read(buffer);
                command = new Command(ECommand.CMD_UPLOAD,new FileChunk(buffer, Username, GUID));
                if (nbytes == -1) {
                    break;
                }
                TCPHelper.SendTCPCommand(OOS, command);
            }
        } catch (Exception ex) {
            ExceptionHandler.ShowException(ex);
        }
    }

    public static void Login(TUser User) throws IOException {
        Command command = new Command(ECommand.CMD_LOGIN, User);
        TCPHelper.SendTCPCommand(App.CL_CFG.getOOS(), command);
    }
}
