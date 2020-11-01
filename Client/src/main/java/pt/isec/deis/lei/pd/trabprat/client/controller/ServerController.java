package pt.isec.deis.lei.pd.trabprat.client.controller;

import java.io.IOException;
import pt.isec.deis.lei.pd.trabprat.client.App;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.model.TUser;
import pt.isec.deis.lei.pd.trabprat.thread.tcp.TCPHelper;

public final class ServerController {
    private ServerController(){}
    
    public static void Register(TUser User) throws IOException{
        Command command = new Command(ECommand.CMD_REGISTER, User);
        TCPHelper.SendTCPCommand(App.CL_CFG.OOS ,command);
    }
}
