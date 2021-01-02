package pt.isec.deis.lei.pd.trabprat.server.thread.multicast;

import java.io.IOException;
import java.net.MulticastSocket;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.FileChunk;
import pt.isec.deis.lei.pd.trabprat.model.GenericPair;
import pt.isec.deis.lei.pd.trabprat.model.Server;
import pt.isec.deis.lei.pd.trabprat.model.TChannel;
import pt.isec.deis.lei.pd.trabprat.model.TChannelMessage;
import pt.isec.deis.lei.pd.trabprat.model.TChannelUser;
import pt.isec.deis.lei.pd.trabprat.model.TDirectMessage;
import pt.isec.deis.lei.pd.trabprat.model.TUser;
import pt.isec.deis.lei.pd.trabprat.server.Main;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;
import pt.isec.deis.lei.pd.trabprat.server.explorer.ExplorerController;
import pt.isec.deis.lei.pd.trabprat.thread.tcp.TCPHelper;
import pt.isec.deis.lei.pd.trabprat.thread.udp.UDPHelper;

public class MulticastHandler implements Runnable {

    private final ServerConfig SV_CFG;
    private final MulticastSocket mCS;
    private final String SvID;
    private final Command cmd;

    public MulticastHandler(ServerConfig SV_CFG, MulticastSocket mCS, String SvID, Command cmd) {
        this.SV_CFG = SV_CFG;
        this.mCS = mCS;
        this.SvID = SvID;
        this.cmd = cmd;
    }

    @Override
    public void run() {
        try {
            // Read command
            if (cmd.CMD != ECommand.CMD_HEARTBEAT) {
                Main.Log("[" + SvID + "]", "" + cmd.CMD);
            }

            switch (cmd.CMD) {
                case ECommand.CMD_HELLO: {
                    HandleHello(cmd);
                    break;
                }
                case ECommand.CMD_BYE: {
                    HandleBye(cmd);
                    break;
                }
                case ECommand.CMD_CREATED: {
                    HandleCreated(cmd);
                    break;
                }
                case ECommand.CMD_GET_CHANNEL_MESSAGES: {
                    HandleAddUserToChannel(cmd);
                    break;
                }
                case ECommand.CMD_LOGIN: {
                    HandleLogin(cmd);
                    break;
                }
                case ECommand.CMD_LOGOUT: {
                    HandleLogout(cmd);
                    break;
                }
                case ECommand.CMD_UPLOAD: {
                    HandleUpload(cmd);
                    break;
                }
                case ECommand.CMD_UPDATE_CHANNEL: {
                    HandleUpdateChannel(cmd);
                    break;
                }
                case ECommand.CMD_DELETE_CHANNEL: {
                    HandleDeleteChannel(cmd);
                    break;
                }
                case ECommand.CMD_HEARTBEAT: {
                    HandleHeartbeat(cmd);
                    break;
                }
                default: {
                    // Send CMD_FORBIDDEN command
                    //UDPHelper.SendMulticastCommand(ServerSocket, ReceivedPacket.getAddress(), ReceivedPacket.getPort(), new Command(ECommand.CMD_FORBIDDEN));
                    break;
                }
            }
        } catch (Exception ex) {
            ExceptionHandler.ShowException(ex);
        }
    }

    private void HandleHeartbeat(Command cmd) {
        // Add or update server info
        synchronized (SV_CFG) {
            SV_CFG.AddOrUpdateServer(((GenericPair<String, Server>) cmd.Body).value);
        }
    }

    private void HandleHello(Command cmd) throws IOException {
        GenericPair<String, Server> v;
        Server s = ((GenericPair<String, Server>) cmd.Body).value;
        synchronized (SV_CFG) {
            v = new GenericPair<>(SV_CFG.ServerID, new Server(SV_CFG.ServerID,
                    SV_CFG.ServerStart, SV_CFG.ExternalIP, SV_CFG.UDPPort,
                    SV_CFG.TCPPort, SV_CFG.Clients.size()));
        }
        UDPHelper.SendUDPCommand(mCS, s.getAddress(),
                s.getUDPPort(), new Command(ECommand.CMD_HELLO, v));
    }

    private void HandleBye(Command cmd) {
        Server s = ((GenericPair<String, Server>) cmd.Body).value;
        synchronized (SV_CFG) {
            SV_CFG.ServerList.remove(s);
        }
    }

    private void HandleCreated(Command cmd) throws IOException {
        // CMD_CREATED (TUser, TChannel, TChannelMessage, TDirectMessage)
        GenericPair<String, ?> gp = (GenericPair<String, ?>) cmd.Body;
        // Insert into the database
        if (gp.value instanceof TUser) {
            TUser user = (TUser) gp.value;
            synchronized (SV_CFG) {
                SV_CFG.DB.devInsertUser(user);
            }
        } else if (gp.value instanceof TChannel) {
            TChannel channel = (TChannel) gp.value;
            synchronized (SV_CFG) {
                SV_CFG.DB.devInsertChannel(channel);
                // Broadcast to server users
                var c = SV_CFG.DB.getAllChannels();
                SV_CFG.BroadcastMessage(new Command(ECommand.CMD_CREATED, c));
            }
        } else if (gp.value instanceof TChannelMessage) {                                                               // TODO: RMIObserver Message Received
            TChannelMessage cM = (TChannelMessage) gp.value;
            synchronized (SV_CFG) {
                SV_CFG.DB.devInsertMessage(cM.getMID());
                SV_CFG.DB.devInsertChannelMessage(cM.getCID().getCID(), cM.getMID().getMID());
                // Broadcast to server users
                SV_CFG.BroadcastMessage(new Command(ECommand.CMD_CREATED,
                        SV_CFG.DB.getAllMessagesFromChannelID(cM.getCID().getCID())));
            }
        } else if (gp.value instanceof TDirectMessage) {                                                                // TODO: RMIObserver Message Received
            TDirectMessage dM = (TDirectMessage) gp.value;
            synchronized (SV_CFG) {
                SV_CFG.DB.devInsertMessage(dM.getMID());
                SV_CFG.DB.devInsertDirectMessage(dM.getUID().getUID(), dM.getMID().getMID());
                // Send to other user
                var ou = SV_CFG.GetUser(dM.getUID());
                if (ou != null) {
                    var dmL = SV_CFG.DB.getAllDMByUserIDAndOtherID(dM.getMID().getMUID().getUID(), dM.getUID().getUID());
                    var dmU = SV_CFG.DB.getOtherUserFromDM(SV_CFG.DB.getAllDMByUserID(dM.getUID().getUID()), dM.getUID());
                    var sendCmd = new Command(ECommand.CMD_CREATED, new GenericPair<>(dmL, dmU));
                    TCPHelper.SendTCPCommand(ou.value, sendCmd);
                }
            }
        }
    }

    private void HandleAddUserToChannel(Command cmd) {
        // CMD_GET_CHANNEL_MESSAGES (TChannelUser)
        GenericPair<String, TChannelUser> gp = (GenericPair<String, TChannelUser>) cmd.Body;
        // Insert to database
        TChannelUser cU = gp.value;
        synchronized (SV_CFG) {
            SV_CFG.DB.devInsertChannelUser(cU.getCID().getCID(), cU.getUID().getUID());
            var cUs = SV_CFG.DB.getAllChannelUsers();
            SV_CFG.BroadcastMessage(new Command(ECommand.CMD_UPDATE_CHANNEL_USERS, cUs));
        }
    }

    private void HandleLogin(Command cmd) {
        // CMD_LOGIN (TUser)
        GenericPair<String, TUser> gp = (GenericPair<String, TUser>) cmd.Body;
        TUser user = gp.value;
        // Add to OtherUsers list
        synchronized (SV_CFG) {
            SV_CFG.OtherSvClients.add(user);
            // Broadcast to server users
            SV_CFG.BroadcastOnlineActivity();
        }
    }

    private void HandleUpload(Command cmd) throws IOException, InterruptedException {
        // CMD_UPLOAD (FileChunk)
        GenericPair<String, FileChunk> gp = (GenericPair<String, FileChunk>) cmd.Body;
        FileChunk fc = gp.value;
        // Write to local files
        boolean hasGUID = (fc.getGUID() != null);
        ExplorerController.WriteFile(SV_CFG.DBConnection.getSchema(),
                !hasGUID ? ExplorerController.AVATAR_SUBDIR : ExplorerController.FILES_SUBDIR,
                !hasGUID ? fc.getUsername() + fc.getExtension() : fc.getGUID().toString() + fc.getExtension(),
                fc.getFilePart(),
                fc.getOffset(),
                fc.getLength());
    }

    private void HandleUpdateChannel(Command cmd) {
        // CMD_UPDATE_CHANNEL (TChannel)
        GenericPair<String, TChannel> gp = (GenericPair<String, TChannel>) cmd.Body;
        TChannel updatedChannel = gp.value;
        // Update database
        synchronized (SV_CFG) {
            SV_CFG.DB.updateChannel(updatedChannel);
            // Broadcast to server users
            var c = SV_CFG.DB.getAllChannels();
            SV_CFG.BroadcastMessage(new Command(ECommand.CMD_UPDATE_CHANNEL, c));
        }
    }

    private void HandleDeleteChannel(Command cmd) {
        // CMD_DELETE_CHANNEL (int)
        GenericPair<String, Integer> gp = (GenericPair<String, Integer>) cmd.Body;
        int channel = gp.value;
        // Delete from database
        synchronized (SV_CFG) {
            SV_CFG.DB.devDeleteChannel(channel);
            // Broadcast to server users
            var c = SV_CFG.DB.getAllChannels();
            SV_CFG.BroadcastMessage(new Command(ECommand.CMD_DELETE_CHANNEL, c));
        }
    }

    private void HandleLogout(Command cmd) {
        // CMD_LOGOUT (TUser)
        GenericPair<String, TUser> gp = (GenericPair<String, TUser>) cmd.Body;
        TUser user = gp.value;
        // Remove from OtherSvUsers list
        synchronized (SV_CFG) {
            SV_CFG.OtherSvClients.remove(user);
            SV_CFG.BroadcastOnlineActivity();
        }
    }
}
