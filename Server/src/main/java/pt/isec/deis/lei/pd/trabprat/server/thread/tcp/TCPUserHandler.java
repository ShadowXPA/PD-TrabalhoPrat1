package pt.isec.deis.lei.pd.trabprat.server.thread.tcp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.FileChunk;
import pt.isec.deis.lei.pd.trabprat.model.LoginPackage;
import pt.isec.deis.lei.pd.trabprat.model.TChannelMessage;
import pt.isec.deis.lei.pd.trabprat.model.TChannelUser;
import pt.isec.deis.lei.pd.trabprat.model.TUser;
import pt.isec.deis.lei.pd.trabprat.server.Main;
import pt.isec.deis.lei.pd.trabprat.server.config.DefaultSvMsg;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;
import pt.isec.deis.lei.pd.trabprat.server.db.DatabaseWrapper;
import pt.isec.deis.lei.pd.trabprat.server.explorer.ExplorerController;
import pt.isec.deis.lei.pd.trabprat.server.model.Client;
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
                case ECommand.CMD_GET_CHANNEL_MESSAGES: {
                    HandleGetChannelMessages();
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
                Client c = new Client(info, oOS);
                synchronized (SV_CFG) {
                    LoggedIn = SV_CFG.ClientListContains(c);
                }
                if (!LoggedIn) {
                    // Send channel list, online users, DMs
                    LoginPackage lp = new LoginPackage(info);
                    synchronized (SV_CFG) {
                        var users = SV_CFG.ClientList.values().iterator();
                        while (users.hasNext()) {
                            TUser cl = users.next().User;
                            lp.Users.add(new TUser(cl.getUID(),
                                    cl.getUName(), cl.getUUsername(),
                                    null, cl.getUPhoto(), cl.getUDate()));
                        }
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
                        SV_CFG.ClientList.put(UserSocket, c);
                    }
                    // Announce to other servers via multicast
                    // Send to other users that the list of users has been updated
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

    public TCPUserHandler(Socket UserSocket, ObjectOutputStream oOS, Command Cmd, String IP, ServerConfig SV_CFG) throws IOException {
        this.UserSocket = UserSocket;
        this.oOS = oOS;
        this.Cmd = Cmd;
        this.IP = IP;
        this.SV_CFG = SV_CFG;
    }
}
