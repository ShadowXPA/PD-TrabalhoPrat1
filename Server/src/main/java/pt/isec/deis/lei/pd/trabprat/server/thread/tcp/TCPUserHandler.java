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
import pt.isec.deis.lei.pd.trabprat.server.model.Client;
import pt.isec.deis.lei.pd.trabprat.model.Pair;
import pt.isec.deis.lei.pd.trabprat.thread.tcp.TCPHelper;

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
        Command sendCmd;
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
                default: {
                    sendCmd = new Command(ECommand.CMD_FORBIDDEN);
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
                ExplorerController.CreateUserDirectory(DBName, user.getUUsername());
                ExplorerController.Touch(DBName, ExplorerController.AVATAR_SUBDIR, user.getUUsername() + extension);
                String fullDir = DBName + ExplorerController.BASE_DIR + ExplorerController.AVATAR_SUBDIR + "/"
                        + user.getUUsername() + extension;
                TUser insUser = new TUser(0, user.getUName(), user.getUUsername(), user.getUPassword(), fullDir, 0);
                int inserted;
                synchronized (SV_CFG) {
                    inserted = db.insertUser(insUser);
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
//                Client c = new Client(info, oOS);
                var c = new Pair<TUser, ObjectOutputStream>(info, oOS);
                synchronized (SV_CFG) {
                    LoggedIn = SV_CFG.ClientListContains(c);
                }
                if (!LoggedIn) {
                    // Send channel list, online users, DMs
                    LoginPackage lp = new LoginPackage(info);
                    synchronized (SV_CFG) {
//                        var users = SV_CFG.ClientList.values().iterator();
                        lp.Users.addAll(SV_CFG.GetAllOnlineUsers());
                        var channels = SV_CFG.DB.getAllChannels();
                        lp.Channels.addAll(channels);
                        var dms = SV_CFG.DB.getAllDMByUserID(info.getUID());
                        lp.DMUsers.addAll(SV_CFG.DB.getOtherUserFromDM(dms, info));
                        var channelUsers = SV_CFG.DB.getAllChannelUsers();
                        lp.ChannelUsers.addAll(channelUsers);
                    }

                    sendCmd = new Command(ECommand.CMD_LOGIN, lp);
                    TCPHelper.SendTCPCommand(oOS, sendCmd);
                    Main.Log("[Server] to " + IP, "" + sendCmd.CMD);
                    Main.Log("[User: (" + info.getUID() + ") " + info.getUUsername() + "]", "has logged in.");
                    // Add user to the client list
                    synchronized (SV_CFG) {
//                        SV_CFG.ClientList.put(UserSocket, c);
                        SV_CFG.Clients.put(UserSocket, c);
                        // Send to other users that the list of users has been updated
                        SV_CFG.BroadcastOnlineActivity();
                    }
                    // Announce to other servers via multicast
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
        ArrayList<TChannel> c = null;
        int ErrorNumber;
        synchronized (SV_CFG) {
            db = SV_CFG.DB;
            ErrorNumber = db.insertChannel(channel);
            if (ErrorNumber > 0) {
                c = db.getAllChannels();
            }
        }
        if (ErrorNumber > 0) {
            // Success
            // Broadcast new channel created to all users
            synchronized (SV_CFG) {
                SV_CFG.BroadcastMessage(new Command(ECommand.CMD_CREATED, c));
            }
        } else {
            // Operation failed
            TCPHelper.SendTCPCommand(oOS, new Command(ECommand.CMD_BAD_REQUEST, DefaultSvMsg.SV_CREATE_CHANNEL_FAIL));
            Main.Log("[Server] to " + IP, "" + ECommand.CMD_BAD_REQUEST);
        }
    }

    private void HandleUpdateChannel() throws IOException {
        // Update channel if user is owner
        DatabaseWrapper db;
        TChannelUser cU = (TChannelUser) Cmd.Body;
        ArrayList<TChannel> c = null;
        int ErrorNumber;
        if (!cU.getCID().getCUID().equals(cU.getUID())) {
            ErrorNumber = 0;
        } else {
            synchronized (SV_CFG) {
                db = SV_CFG.DB;
                ErrorNumber = db.updateChannel(cU.getCID());
                c = db.getAllChannels();
            }
        }
        if (ErrorNumber > 0) {
            // Success
//            sendCmd = new Command(ECommand.CMD_UPDATE_CHANNEL, c);
            // Broadcast newly updated channel to all users
            synchronized (SV_CFG) {
                SV_CFG.BroadcastMessage(new Command(ECommand.CMD_UPDATE_CHANNEL, c));
            }
        } else {
            // Operation failed
            TCPHelper.SendTCPCommand(oOS, new Command(ECommand.CMD_BAD_REQUEST, DefaultSvMsg.SV_UPDATE_CHANNEL_FAIL));
            Main.Log("[Server] to " + IP, "" + ECommand.CMD_BAD_REQUEST);
        }
    }

    private void HandleDeleteChannel() throws IOException {
        // Delete channel if user is owner
        DatabaseWrapper db;
        TChannelUser cU = (TChannelUser) Cmd.Body;
        ArrayList<TChannel> c = null;
        int ErrorNumber;
        if (!cU.getCID().getCUID().equals(cU.getUID())) {
            ErrorNumber = 0;
        } else {
            synchronized (SV_CFG) {
                db = SV_CFG.DB;
                ErrorNumber = db.deleteChannel(cU.getCID());
                c = db.getAllChannels();
            }
        }
        if (ErrorNumber > 0) {
            // Success
//            sendCmd = new Command(ECommand.CMD_DELETE_CHANNEL, c);
            // Broadcast newly deleted channel to all users
            synchronized (SV_CFG) {
                SV_CFG.BroadcastMessage(new Command(ECommand.CMD_DELETE_CHANNEL, c));
            }
        } else {
            // Operation failed
            TCPHelper.SendTCPCommand(oOS, new Command(ECommand.CMD_BAD_REQUEST, DefaultSvMsg.SV_DELETE_CHANNEL_FAIL));
            Main.Log("[Server] to " + IP, "" + ECommand.CMD_BAD_REQUEST);
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
        ArrayList<TChannelMessage> cmL = null;
        ArrayList<TDirectMessage> dmL = null;
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
                        String BaseDir = SV_CFG.DBConnection.getSchema() + ExplorerController.BASE_DIR;
                        String InternalPath = BaseDir + ExplorerController.FILES_SUBDIR
                                + "/" + cm.getMID().getMPath() + Extension;
                        msg = new TMessage(0, cm.getMID().getMUID(), cm.getMID().getMText(), InternalPath, 0);
                    }
                } else {
                    msg = cm.getMID();
                }
                synchronized (SV_CFG) {
                    i += db.insertChannelMessage(cm.getCID(), msg);
                    cmL = db.getAllMessagesFromChannelID(cm.getCID().getCID());
                }
                if (i > 0) {
//                    sendCmd = new Command(ECommand.CMD_CREATED, cmL);
                    // Broadcast new message to all users
                    synchronized (SV_CFG) {
                        SV_CFG.BroadcastMessage(new Command(ECommand.CMD_CREATED, cmL));
                    }
                } else {
                    sendCmd = new Command(ECommand.CMD_BAD_REQUEST, DefaultSvMsg.SV_MESSAGE_FAIL);
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
                        String BaseDir = SV_CFG.DBConnection.getSchema() + ExplorerController.BASE_DIR;
                        String InternalPath = BaseDir + ExplorerController.FILES_SUBDIR
                                + "/" + dm.getMID().getMPath() + Extension;
                        msg = new TMessage(0, dm.getMID().getMUID(), dm.getMID().getMText(), InternalPath, 0);
                    }
                } else {
                    msg = dm.getMID();
                }
                synchronized (SV_CFG) {
                    i += db.insertDirectMessage(dm.getUID(), msg);
                    dmL = db.getAllDMByUserIDAndOtherID(dm.getMID().getMUID().getUID(), dm.getUID().getUID());
                }
                if (i > 0) {
//                    sendCmd = new Command(ECommand.CMD_CREATED, dmL);
                    // Broadcast new message to all users
                    synchronized (SV_CFG) {
                        SV_CFG.BroadcastMessage(new Command(ECommand.CMD_CREATED, dmL));
                    }
                } else {
                    sendCmd = new Command(ECommand.CMD_BAD_REQUEST, DefaultSvMsg.SV_MESSAGE_FAIL);
                }
            }
        }
        TCPHelper.SendTCPCommand(oOS, sendCmd);
        Main.Log("[Server] to " + IP, "" + ((sendCmd == null) ? ECommand.CMD_CREATED : sendCmd.CMD));
    }

    public TCPUserHandler(Socket UserSocket, ObjectOutputStream oOS, Command Cmd, String IP, ServerConfig SV_CFG) throws IOException {
        this.UserSocket = UserSocket;
        this.oOS = oOS;
        this.Cmd = Cmd;
        this.IP = IP;
        this.SV_CFG = SV_CFG;
    }
}
