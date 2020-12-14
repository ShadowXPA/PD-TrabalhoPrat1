package pt.isec.deis.lei.pd.trabprat.client.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.UUID;
import pt.isec.deis.lei.pd.trabprat.client.App;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.config.DefaultConfig;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.FileChunk;
import pt.isec.deis.lei.pd.trabprat.model.TChannel;
import pt.isec.deis.lei.pd.trabprat.model.TChannelUser;
import pt.isec.deis.lei.pd.trabprat.model.TMessage;
import pt.isec.deis.lei.pd.trabprat.model.TUser;
import pt.isec.deis.lei.pd.trabprat.model.TUserPair;
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
            for (long i = 0; true; i += DefaultConfig.DEFAULT_TCP_PACKET_SIZE) {
                byte[] buffer = new byte[DefaultConfig.DEFAULT_TCP_PACKET_SIZE];
                int read = FIS.readNBytes(buffer, 0, buffer.length);
                if (read == 0) {
                    break;
                }
                byte[] temp = new byte[read];
                System.arraycopy(buffer, 0, temp, 0, read);
                int extIndex = Path.lastIndexOf(".");
                String extension = "";
                if (extIndex != -1) {
                    extension = Path.substring(extIndex);
                }
                //Enviar FIleChunk para o server com o comando upload
                command = new Command(ECommand.CMD_UPLOAD, new FileChunk(temp, i, temp.length, Username, GUID, extension));
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

    public static void ChannelMessages() throws IOException {
        Command command = null;
        if (App.CL_CFG.SelectedChannel instanceof TChannel) {
            command = new Command(ECommand.CMD_GET_CHANNEL_MESSAGES, new TChannelUser((TChannel) App.CL_CFG.SelectedChannel, App.CL_CFG.MyUser));
        } else if (App.CL_CFG.SelectedChannel instanceof TUser) {
            command = new Command(ECommand.CMD_GET_DM_MESSAGES, new TUserPair((TUser) App.CL_CFG.SelectedChannel, App.CL_CFG.MyUser));
        }
        TCPHelper.SendTCPCommand(App.CL_CFG.getOOS(), command);
    }

    public static void GetFile(TMessage message) throws IOException {
        Command command = new Command(ECommand.CMD_DOWNLOAD, message);
        TCPHelper.SendTCPCommand(App.CL_CFG.getOOS(), command);
    }

    public static void NewMessage(Object channel) throws IOException {
        Command command = new Command(ECommand.CMD_CREATE_MESSAGE, channel);
        TCPHelper.SendTCPCommand(App.CL_CFG.getOOS(), command);
    }
    
    public static void CreateChannel(TChannel channel) throws Exception{
        Command command = new Command(ECommand.CMD_CREATE_CHANNEL, channel);
        TCPHelper.SendTCPCommand(App.CL_CFG.getOOS(), command);
    }
    
    public static void EditChannel(TChannelUser channel) throws Exception{
        Command command = new Command(ECommand.CMD_UPDATE_CHANNEL, channel);
        TCPHelper.SendTCPCommand(App.CL_CFG.getOOS(), command);
    }
    
    public static void DeleteChannel(TChannelUser channel) throws Exception{
        Command command = new Command(ECommand.CMD_DELETE_CHANNEL, channel);
        TCPHelper.SendTCPCommand(App.CL_CFG.getOOS(), command);
    }
    
    public static void SearchUser(String str) throws IOException{
        Command command = new Command(ECommand.CMD_SEARCH_USERS, str);
        TCPHelper.SendTCPCommand(App.CL_CFG.getOOS(), command);
    }
}
