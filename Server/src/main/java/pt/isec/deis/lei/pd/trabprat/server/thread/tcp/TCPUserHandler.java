package pt.isec.deis.lei.pd.trabprat.server.thread.tcp;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.FileChunk;
import pt.isec.deis.lei.pd.trabprat.model.TUser;
import pt.isec.deis.lei.pd.trabprat.server.Main;
import pt.isec.deis.lei.pd.trabprat.server.config.DefaultSvMsg;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;
import pt.isec.deis.lei.pd.trabprat.server.db.DatabaseWrapper;
import pt.isec.deis.lei.pd.trabprat.server.explorer.ExplorerController;
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
                    // Check if user exists in the database
                    // Check if password is good and matches
                    // Send OK to the client
                    // Add user to the client list
                    // Announce to other servers via multicast
                    break;
                }
                case ECommand.CMD_UPLOAD: {
                    HandleUpload();
                    break;
                }
                default: {
                    sendCmd = new Command(ECommand.CMD_FORBIDDEN);
                    TCPHelper.SendTCPCommand(oOS, sendCmd);
                }
            }
        } catch (Exception ex) {
            ExceptionHandler.ShowException(ex);
        }
    }

    private void HandleRegister() throws IOException {
        // Check if user already exists in the database
        DatabaseWrapper db;
        Command sendCmd;
        TUser user = (TUser) Cmd.Body;
        TUser info;
        synchronized (SV_CFG) {
            db = SV_CFG.DB;
            info = db.getUserByName(user.getUName());
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
            } else {
                // Check if password is good
                // Encrypt password
                // Store information
                int inserted;
                synchronized (SV_CFG) {
                    inserted = db.insertUser(user);
                }
                if (inserted <= 0) {
                    // Tell client, server couldn't register
                    sendCmd = new Command(ECommand.CMD_SERVICE_UNAVAILABLE, DefaultSvMsg.SV_INTERNAL_ERROR);
                    TCPHelper.SendTCPCommand(oOS, sendCmd);
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

    private void HandleUpload() {
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
                        !hasGUID ? fc.getUsername() : fc.getGUID().toString(),
                        fc.getFilePart(),
                        fc.getOffset(),
                        fc.getLength());
                // Send through multicast
            } catch (Exception ex) {
                ExceptionHandler.ShowException(ex);
            }
        }
    }

    public TCPUserHandler(Socket UserSocket, ObjectOutputStream oOS, Command Cmd, String IP, ServerConfig SV_CFG) throws IOException {
        this.UserSocket = UserSocket;
        this.oOS = oOS;
        this.Cmd = Cmd;
        this.IP = IP;
        this.SV_CFG = SV_CFG;
    }
}
