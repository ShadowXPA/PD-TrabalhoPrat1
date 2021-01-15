package pt.isec.deis.lei.pd.trabprat.server.thread.tcp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.config.DefaultConfig;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.FileChunk;
import pt.isec.deis.lei.pd.trabprat.model.LoginPackage;
import pt.isec.deis.lei.pd.trabprat.model.TChannel;
import pt.isec.deis.lei.pd.trabprat.model.TChannelMessage;
import pt.isec.deis.lei.pd.trabprat.model.TChannelUser;
import pt.isec.deis.lei.pd.trabprat.model.TDirectMessage;
import pt.isec.deis.lei.pd.trabprat.model.TMessage;
import pt.isec.deis.lei.pd.trabprat.model.TUser;
import pt.isec.deis.lei.pd.trabprat.model.TUserPair;
import pt.isec.deis.lei.pd.trabprat.server.Main;
import pt.isec.deis.lei.pd.trabprat.server.config.DefaultSvMsg;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;
import pt.isec.deis.lei.pd.trabprat.server.db.DatabaseWrapper;
import pt.isec.deis.lei.pd.trabprat.server.explorer.ExplorerController;
import pt.isec.deis.lei.pd.trabprat.model.GenericPair;
import pt.isec.deis.lei.pd.trabprat.thread.tcp.TCPHelper;

// TODO: Notify all observers of:
// - New message
public class TCPUserHandler implements Runnable {

    private final Socket UserSocket;
    private final ObjectOutputStream oOS;
    private final Command Cmd;
    private final String IP;
    private final ServerConfig SV_CFG;

    @Override
    public void run() {
        // React accordingly
        // If logging in add user to clientlist
        // Send via Multicast every info necessary
        try {
            switch (Cmd.CMD) {
                case ECommand.CMD_REGISTER: {
                    HandleRegister();
                    break;
                }
                case ECommand.CMD_LOGIN: {
                    HandleLogin();
                    break;
                }
                case ECommand.CMD_UPLOAD: {
                    HandleUpload();
                    break;
                }
                case ECommand.CMD_DOWNLOAD: {
                    HandleDownload();
                    break;
                }
                case ECommand.CMD_GET_CHANNEL_MESSAGES: {
                    HandleGetChannelMessages();
                    break;
                }
                case ECommand.CMD_GET_DM_MESSAGES: {
                    HandleGetDMMessages();
                    break;
                }
                case ECommand.CMD_CREATE_CHANNEL: {
                    HandleCreateChannel();
                    break;
                }
                case ECommand.CMD_UPDATE_CHANNEL: {
                    HandleUpdateChannel();
                    break;
                }
                case ECommand.CMD_DELETE_CHANNEL: {
                    HandleDeleteChannel();
                    break;
                }
                case ECommand.CMD_CREATE_MESSAGE: {
                    HandleCreateMessage();
                    break;
                }
                case ECommand.CMD_SEARCH_USERS: {
                    HandleSearchUsers();
                    break;
                }
                default: {
                    Command sendCmd = new Command(ECommand.CMD_FORBIDDEN);
                    TCPHelper.SendTCPCommand(oOS, sendCmd);
                    Main.Log("[Server] to " + IP, "" + sendCmd.CMD);
                    break;
                }
            }
        } catch (Exception ex) {
            ExceptionHandler.ShowException(ex);
        }
    }

    private void HandleRegister() throws IOException, FileNotFoundException, InterruptedException {
        // Check if user already exists in the database
        DatabaseWrapper db;
        Command sendCmd;
        TUser user = (TUser) Cmd.Body;
        TUser info;
        String DBName;
        synchronized (SV_CFG) {
            db = SV_CFG.DB;
            info = db.getUserByName(user.getUName());
            DBName = SV_CFG.DBConnection.getSchema();
        }
        if (info != null) {
            // Send internal error
            sendCmd = new Command(ECommand.CMD_BAD_REQUEST, DefaultSvMsg.SV_USER_EXISTS);
            TCPHelper.SendTCPCommand(oOS, sendCmd);
        } else {
            synchronized (SV_CFG) {
                info = db.getUserByUsername(user.getUUsername());
            }
            if (info != null) {
                sendCmd = new Command(ECommand.CMD_BAD_REQUEST, DefaultSvMsg.SV_USERNAME_EXISTS);
                TCPHelper.SendTCPCommand(oOS, sendCmd);
                Main.Log("[Server] to " + IP, "" + sendCmd.CMD);
            } else {
                // Check if password is good
                // Encrypt password
                // Store information
                int extIndex = user.getUPhoto().lastIndexOf(".");
                String extension = "";
                if (extIndex != -1) {
                    extension = user.getUPhoto().substring(extIndex);
                }
//                ExplorerController.CreateUserDirectory(DBName, user.getUUsername());
                ExplorerController.Touch(DBName, ExplorerController.AVATAR_SUBDIR, user.getUUsername() + extension);
                String fullDir = /*DBName + ExplorerController.BASE_DIR +*/ ExplorerController.AVATAR_SUBDIR + "/"
                        + user.getUUsername() + extension;
                TUser insUser = new TUser(0, user.getUName(), user.getUUsername(), user.getUPassword(), fullDir, 0);
                TUser lastUser;
                int inserted;
                synchronized (SV_CFG) {
                    inserted = db.insertUser(insUser);
                    lastUser = db.getLastUser();
                }
                if (inserted <= 0) {
                    // Tell client, server couldn't register
                    sendCmd = new Command(ECommand.CMD_SERVICE_UNAVAILABLE, DefaultSvMsg.SV_INTERNAL_ERROR);
                    TCPHelper.SendTCPCommand(oOS, sendCmd);
                    Main.Log("[Server] to " + IP, "" + sendCmd.CMD);
                } else {
                    // Send OK to the client
                    sendCmd = new Command(ECommand.CMD_CREATED, user);
                    TCPHelper.SendTCPCommand(oOS, sendCmd);
                    Main.Log("[Server] to " + IP, "" + sendCmd.CMD);
                    // After created, the client should send the photo asynchronously
                    // Announce to other servers via multicast
                    synchronized (SV_CFG) {
                        SV_CFG.MulticastMessage(new Command(ECommand.CMD_CREATED,
                                new GenericPair<>(SV_CFG.ServerID, lastUser)));
                    }
                }
            }
        }
    }

    private void HandleLogin() throws IOException {
        // Check if user exists in the database
        DatabaseWrapper db;
        Command sendCmd;
        TUser user = (TUser) Cmd.Body;
        TUser info;
        synchronized (SV_CFG) {
            db = SV_CFG.DB;
            info = db.getUserByUsername(user.getUUsername());
        }
        if (info != null) {
            // Check if password is good and matches
            // Send OK to the client or UNAUTHORIZED
            if (user.getUPassword().equals(info.getUPassword())) {
                // OK
                boolean LoggedIn;
//                info.setPassword();
                var c = new GenericPair<TUser, ObjectOutputStream>(info, oOS);
                synchronized (SV_CFG) {
                    LoggedIn = SV_CFG.ClientListContains(c);
                }
                if (!LoggedIn) {
                    // Send channel list, online users, DMs
                    LoginPackage lp = new LoginPackage(info);
                    synchronized (SV_CFG) {
                        lp.Users.addAll(SV_CFG.GetAllOnlineUsers());
                        var channels = db.getAllChannels();
                        lp.Channels.addAll(channels);
                        var dms = db.getAllDMByUserID(info.getUID());
                        lp.DMUsers.addAll(db.getOtherUserFromDM(dms, info));
                        var channelUsers = db.getAllChannelUsers();
                        lp.ChannelUsers.addAll(channelUsers);
                    }

                    sendCmd = new Command(ECommand.CMD_LOGIN, lp);
                    TCPHelper.SendTCPCommand(oOS, sendCmd);
                    Main.Log("[Server] to " + IP, "" + sendCmd.CMD);
                    Main.Log("[User: (" + info.getUID() + ") " + info.getUUsername() + "]", "has logged in.");
                    // Add user to the client list
                    synchronized (SV_CFG) {
                        SV_CFG.Clients.put(UserSocket, c);
                        // Send to other users that the list of users has been updated
                        SV_CFG.BroadcastOnlineActivity();
                        // Announce to other servers via multicast
                        SV_CFG.MulticastMessage(new Command(ECommand.CMD_LOGIN,
                                new GenericPair<>(SV_CFG.ServerID, info)));
                        // Broadcast to all RMI clients
                        SV_CFG.broadcastToRMI("User " + info.getUUsername() + " has logged in!");
                    }
                } else {
                    // User already logged in
                    sendCmd = new Command(ECommand.CMD_UNAUTHORIZED, DefaultSvMsg.SV_USER_LOGGED_IN);
                    TCPHelper.SendTCPCommand(oOS, sendCmd);
                    Main.Log("[Server] to " + IP, "" + sendCmd.CMD);
                }
            } else {
                // Password doesn't match
                sendCmd = new Command(ECommand.CMD_UNAUTHORIZED, DefaultSvMsg.SV_PASSWORD_DOES_NOT_MATCH);
                TCPHelper.SendTCPCommand(oOS, sendCmd);
                Main.Log("[Server] to " + IP, "" + sendCmd.CMD);
            }
        } else {
            // Username doesn't exist....
            sendCmd = new Command(ECommand.CMD_UNAUTHORIZED, DefaultSvMsg.SV_USERNAME_NOT_EXISTS);
            TCPHelper.SendTCPCommand(oOS, sendCmd);
            Main.Log("[Server] to " + IP, "" + sendCmd.CMD);
        }
    }

    private void HandleUpload() throws IOException {
        // Check if user is in database
        DatabaseWrapper db;
        FileChunk fc = (FileChunk) Cmd.Body;
        TUser user;
        synchronized (SV_CFG) {
            db = SV_CFG.DB;
            user = db.getUserByUsername(fc.getUsername());
        }
        if (user != null) {
            try {
                // Write File
                boolean hasGUID = (fc.getGUID() != null);
                ExplorerController.WriteFile(SV_CFG.DBConnection.getSchema(),
                        !hasGUID ? ExplorerController.AVATAR_SUBDIR : ExplorerController.FILES_SUBDIR,
                        !hasGUID ? fc.getUsername() + fc.getExtension() : fc.getGUID().toString() + fc.getExtension(),
                        fc.getFilePart(),
                        fc.getOffset(),
                        fc.getLength());
                // Send through multicast
                synchronized (SV_CFG) {
                    SV_CFG.MulticastMessage(new Command(ECommand.CMD_UPLOAD,
                            new GenericPair<>(SV_CFG.ServerID, fc)));
                }
            } catch (Exception ex) {
                ExceptionHandler.ShowException(ex);
            }
        } else {
            Command sendCmd = new Command(ECommand.CMD_FORBIDDEN);
            TCPHelper.SendTCPCommand(oOS, sendCmd);
            Main.Log("[Server] to " + IP, "" + sendCmd.CMD);
        }
    }

    private void HandleDownload() throws IOException {
        // Get TMessage or TUser
        String Path = null;
        String _Username = null;
        Command sendCmd;
        if (Cmd.Body instanceof TMessage) {
            TMessage msg = (TMessage) Cmd.Body;
            Path = msg.getMPath();
            _Username = msg.getMText();
        } else if (Cmd.Body instanceof TUser) {
            TUser usr = (TUser) Cmd.Body;
            Path = usr.getUPhoto();
            _Username = usr.getUUsername();
        }
        if (Path != null) {
            // Send file to user
            FileChunk fc;
            try {
                String BaseDir = SV_CFG.DBConnection.getSchema() + ExplorerController.BASE_DIR;
                Path = BaseDir + Path;
                int extIndex = Path.lastIndexOf(".");
                String Extension = "";
                if (extIndex != -1) {
                    Extension = Path.substring(extIndex);
                }
                int Length = DefaultConfig.DEFAULT_TCP_PACKET_SIZE;
                int Offset = 0;
                byte[] buffer = ExplorerController._ReadFile(Path, Offset, Length);
                while (buffer.length > 0) {
                    fc = new FileChunk(buffer, Offset, buffer.length, _Username, null, Extension);
                    sendCmd = new Command(ECommand.CMD_DOWNLOAD, fc);
                    TCPHelper.SendTCPCommand(oOS, sendCmd);
                    Main.Log("[Server] to " + IP, "" + sendCmd.CMD);
                    Offset += Length;
                    buffer = ExplorerController._ReadFile(Path, Offset, Length);
                }
                fc = new FileChunk(null, 0, 0, _Username, null, Extension);
                sendCmd = new Command(ECommand.CMD_DOWNLOAD, fc);
                TCPHelper.SendTCPCommand(oOS, sendCmd);
                Main.Log("[Server] to " + IP, "" + sendCmd.CMD);
            } catch (Exception ex) {
                sendCmd = new Command(ECommand.CMD_SERVICE_UNAVAILABLE, DefaultSvMsg.SV_DOWNLOAD_FILE_FAIL2);
                TCPHelper.SendTCPCommand(oOS, sendCmd);
                Main.Log("[Server] to " + IP, "" + sendCmd.CMD);
            }
        } else {
            sendCmd = new Command(ECommand.CMD_BAD_REQUEST, DefaultSvMsg.SV_DOWNLOAD_FILE_FAIL);
            TCPHelper.SendTCPCommand(oOS, sendCmd);
            Main.Log("[Server] to " + IP, "" + sendCmd.CMD);
        }
    }

    private void HandleGetChannelMessages() throws IOException {
        // Add channel user if they don't exist
        DatabaseWrapper db;
        TChannelUser cU = (TChannelUser) Cmd.Body;
        ArrayList<TChannelMessage> messages = new ArrayList<>();
        Command sendCmd;
        synchronized (SV_CFG) {
            db = SV_CFG.DB;
            if (!db.doesUserBelongToChannel(cU.getCID(), cU.getUID())) {
                db.insertChannelUser(cU.getCID(), cU.getUID());
                var cUs = db.getAllChannelUsers();
                SV_CFG.BroadcastMessage(new Command(ECommand.CMD_UPDATE_CHANNEL_USERS, cUs));
                // Send through multicast
                SV_CFG.MulticastMessage(new Command(ECommand.CMD_GET_CHANNEL_MESSAGES,
                        new GenericPair<>(SV_CFG.ServerID, cU)));
            }
            messages.addAll(db.getAllMessagesFromChannelID(cU.getCID().getCID()));
        }
        // Send messages from channel
        sendCmd = new Command(ECommand.CMD_GET_CHANNEL_MESSAGES, messages);
        TCPHelper.SendTCPCommand(oOS, sendCmd);
        Main.Log("[Server] to " + IP, "" + sendCmd.CMD);
    }

    private void HandleGetDMMessages() throws IOException {
        DatabaseWrapper db;
        TUserPair pair = (TUserPair) Cmd.Body;
        Command sendCmd;
        ArrayList<TDirectMessage> DMs;
        synchronized (SV_CFG) {
            db = SV_CFG.DB;
            DMs = db.getAllDMByUserIDAndOtherID(pair.User1.getUID(), pair.User2.getUID());
        }
        sendCmd = new Command(ECommand.CMD_GET_DM_MESSAGES, DMs);
        TCPHelper.SendTCPCommand(oOS, sendCmd);
        Main.Log("[Server] to " + IP, "" + sendCmd.CMD);
    }

    private void HandleCreateChannel() throws IOException {
        // Insert channel into database
        DatabaseWrapper db;
        TChannel channel = (TChannel) Cmd.Body;
        ArrayList<TChannel> c;
        TChannel lastChannel;
        int ErrorNumber;
        synchronized (SV_CFG) {
            db = SV_CFG.DB;
            ErrorNumber = db.insertChannel(channel);
            if (ErrorNumber > 0) {
                // Success
                c = db.getAllChannels();
                lastChannel = db.getLastChannel();
                // Broadcast new channel created to all users
                SV_CFG.BroadcastMessage(new Command(ECommand.CMD_CREATED, c));
                // Send through multicast
                SV_CFG.MulticastMessage(new Command(ECommand.CMD_CREATED,
                        new GenericPair<>(SV_CFG.ServerID, lastChannel)));
            } else {
                // Operation failed
                TCPHelper.SendTCPCommand(oOS, new Command(ECommand.CMD_BAD_REQUEST, DefaultSvMsg.SV_CREATE_CHANNEL_FAIL));
                Main.Log("[Server] to " + IP, "" + ECommand.CMD_BAD_REQUEST);
            }
        }
    }

    private void HandleUpdateChannel() throws IOException {
        // Update channel if user is owner
        DatabaseWrapper db;
        TChannelUser cU = (TChannelUser) Cmd.Body;
        ArrayList<TChannel> c;
        TChannel updatedChannel;
        int ErrorNumber;
        if (cU.getCID().getCUID().equals(cU.getUID())) {
            synchronized (SV_CFG) {
                db = SV_CFG.DB;
                ErrorNumber = db.updateChannel(cU.getCID());
                if (ErrorNumber > 0) {
                    // Success
                    c = db.getAllChannels();
                    updatedChannel = db.getChannelByID(cU.getCID().getCID());
                    // Broadcast newly updated channel to all users
                    SV_CFG.BroadcastMessage(new Command(ECommand.CMD_UPDATE_CHANNEL, c));
                    // Send through multicast
                    SV_CFG.MulticastMessage(new Command(ECommand.CMD_UPDATE_CHANNEL,
                            new GenericPair<>(SV_CFG.ServerID, updatedChannel)));
                } else {
                    // Operation failed
                    TCPHelper.SendTCPCommand(oOS, new Command(ECommand.CMD_BAD_REQUEST, DefaultSvMsg.SV_UPDATE_CHANNEL_FAIL));
                    Main.Log("[Server] to " + IP, "" + ECommand.CMD_BAD_REQUEST);
                }
            }
        }
    }

    private void HandleDeleteChannel() throws IOException {
        // Delete channel if user is owner
        DatabaseWrapper db;
        TChannelUser cU = (TChannelUser) Cmd.Body;
        ArrayList<TChannel> c;
        int ErrorNumber;
        if (cU.getCID().getCUID().equals(cU.getUID())) {
            synchronized (SV_CFG) {
                db = SV_CFG.DB;
                ErrorNumber = db.deleteChannel(cU.getCID());
                if (ErrorNumber > 0) {
                    // Success
                    c = db.getAllChannels();
                    // Broadcast newly deleted channel to all users
                    SV_CFG.BroadcastMessage(new Command(ECommand.CMD_DELETE_CHANNEL, c));
                    // Send through multicast
                    SV_CFG.MulticastMessage(new Command(ECommand.CMD_DELETE_CHANNEL,
                            new GenericPair<>(SV_CFG.ServerID, cU.getCID().getCID())));
                } else {
                    // Operation failed
                    TCPHelper.SendTCPCommand(oOS, new Command(ECommand.CMD_BAD_REQUEST, DefaultSvMsg.SV_DELETE_CHANNEL_FAIL));
                    Main.Log("[Server] to " + IP, "" + ECommand.CMD_BAD_REQUEST);
                }
            }
        }

    }

    private void HandleCreateMessage() throws IOException {
        // Get TChannelMessage or TDirectMessage
        // Add message according to the instance of Cmd.Body
        // Send error message if message fails to add
        DatabaseWrapper db;
        Command sendCmd = null;
        TChannelMessage cm = null;
        TDirectMessage dm = null;
        ArrayList<TChannelMessage> cmL;
        ArrayList<TDirectMessage> dmL;
        ArrayList<TUser> dmU;
        TMessage lastMessage;
        if (Cmd.Body instanceof TChannelMessage) {
            cm = (TChannelMessage) Cmd.Body;
        } else if (Cmd.Body instanceof TDirectMessage) {
            dm = (TDirectMessage) Cmd.Body;
        }
        if (cm == null && dm == null) {
            // Both null send error
            sendCmd = new Command(ECommand.CMD_BAD_REQUEST, DefaultSvMsg.SV_MESSAGE_FAIL);
        } else {
            synchronized (SV_CFG) {
                db = SV_CFG.DB;
            }
            int i = 0;
            TMessage msg;
            if (dm == null) {
                // Channel Message
                if (cm.getMID().getMPath() != null) {
                    int extIndex = cm.getMID().getMText().lastIndexOf(".");
                    String Extension = "";
                    if (extIndex != -1) {
                        Extension = cm.getMID().getMText().substring(extIndex);
                    }
                    synchronized (SV_CFG) {
                        String InternalPath = ExplorerController.FILES_SUBDIR
                                + "/" + cm.getMID().getMPath() + Extension;
                        msg = new TMessage(0, cm.getMID().getMUID(), cm.getMID().getMText(), InternalPath, 0);
                    }
                } else {
                    msg = cm.getMID();
                }
                synchronized (SV_CFG) {
                    i += db.insertChannelMessage(cm.getCID(), msg);
                    if (i > 0) {
                        cmL = db.getAllMessagesFromChannelID(cm.getCID().getCID());
                        lastMessage = db.getLastMessage();
                        TChannelMessage lastCM = new TChannelMessage(cm.getCID(), lastMessage);
                        // Broadcast new message to all users
                        SV_CFG.BroadcastMessage(new Command(ECommand.CMD_CREATED, cmL));
                        // Send through multicast
                        SV_CFG.MulticastMessage(new Command(ECommand.CMD_CREATED,
                                new GenericPair<>(SV_CFG.ServerID, lastCM)));
                        StringBuilder sb = new StringBuilder();
                        sb.append("[Channel Message] ");
                        sb.append(cm.getMID().getMUID().getUName());
                        sb.append(" to ");
                        sb.append(cm.getCID().getCName());
                        sb.append(": ");
                        sb.append(cm.getMID().getMText());
                        for (var entry : SV_CFG.RMIClients.entrySet()) {
                            if (db.doesUserBelongToChannel(cm.getCID(), entry.getValue())) {
                                SV_CFG.sendToRMI(entry.getKey(), sb.toString());
                            }
                        }
                    } else {
                        sendCmd = new Command(ECommand.CMD_BAD_REQUEST, DefaultSvMsg.SV_MESSAGE_FAIL);
                    }
                }
            } else {
                // Direct Message
                if (dm.getMID().getMPath() != null) {
                    int extIndex = dm.getMID().getMText().lastIndexOf(".");
                    String Extension = "";
                    if (extIndex != -1) {
                        Extension = dm.getMID().getMText().substring(extIndex);
                    }
                    synchronized (SV_CFG) {
                        String InternalPath = ExplorerController.FILES_SUBDIR
                                + "/" + dm.getMID().getMPath() + Extension;
                        msg = new TMessage(0, dm.getMID().getMUID(), dm.getMID().getMText(), InternalPath, 0);
                    }
                } else {
                    msg = dm.getMID();
                }
                synchronized (SV_CFG) {
                    var temp = dm.getUID();
                    if (dm.getUID().getUID() == 0) {
                        temp = db.getUserByUsername(dm.getUID().getUUsername());
                        if (temp == null) {
                            temp = db.getUserByName(dm.getUID().getUUsername());
                        }
                    }
                    dm = new TDirectMessage(msg, temp);
                    i += db.insertDirectMessage(dm.getUID(), msg);
                    if (i > 0) {
                        lastMessage = db.getLastMessage();
                        TDirectMessage lastDM = new TDirectMessage(lastMessage, dm.getUID());
                        dmL = db.getAllDMByUserIDAndOtherID(dm.getMID().getMUID().getUID(), dm.getUID().getUID());
                        // Broadcast new message to all users
                        dmU = db.getOtherUserFromDM(db.getAllDMByUserID(dm.getUID().getUID()), dm.getUID());
                        sendCmd = new Command(ECommand.CMD_CREATED, new GenericPair<>(dmL, dmU));
                        var ou = SV_CFG.GetUser(dm.getUID());
                        if (ou != null) {
                            TCPHelper.SendTCPCommand(ou.value, sendCmd);
                        }
                        dmU = db.getOtherUserFromDM(db.getAllDMByUserID(dm.getMID().getMUID().getUID()), dm.getMID().getMUID());
                        sendCmd.Body = new GenericPair<>(dmL, dmU);
                        // Send through multicast
                        SV_CFG.MulticastMessage(new Command(ECommand.CMD_CREATED,
                                new GenericPair<>(SV_CFG.ServerID, lastDM)));
                        var rmiClient = SV_CFG.getRMIClient(dm.getUID());
                        StringBuilder sb = new StringBuilder();
                        sb.append("[Direct Message] ");
                        sb.append(dm.getMID().getMUID().getUName());
                        sb.append(" to ");
                        sb.append(dm.getUID().getUName());
                        sb.append(": ");
                        sb.append(dm.getMID().getMText());
                        SV_CFG.sendToRMI(rmiClient, sb.toString());
                    } else {
                        sendCmd = new Command(ECommand.CMD_BAD_REQUEST, DefaultSvMsg.SV_MESSAGE_FAIL);
                    }
                }
            }
        }
        if (sendCmd != null) {
            TCPHelper.SendTCPCommand(oOS, sendCmd);
        }
        Main.Log("[Server] to " + IP, "" + ((sendCmd == null) ? ECommand.CMD_CREATED : sendCmd.CMD));
    }

    private void HandleSearchUsers() throws IOException {
        DatabaseWrapper db;
        Command sendCmd;
        ArrayList<TUser> users;
        synchronized (SV_CFG) {
            db = SV_CFG.DB;
            users = db.findUserByUNameOrUUsername((String) Cmd.Body);
            users.forEach(u -> ((TUser) u).setPassword());
        }
        sendCmd = new Command(ECommand.CMD_SEARCH_USERS, users);
        TCPHelper.SendTCPCommand(oOS, sendCmd);
        Main.Log("[Server] to " + IP, "" + sendCmd.CMD);
    }

    public TCPUserHandler(Socket UserSocket, ObjectOutputStream oOS, Command Cmd, String IP, ServerConfig SV_CFG) throws IOException {
        this.UserSocket = UserSocket;
        this.oOS = oOS;
        this.Cmd = Cmd;
        this.IP = IP;
        this.SV_CFG = SV_CFG;
    }
}
