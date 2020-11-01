package pt.isec.deis.lei.pd.trabprat.server.thread.tcp;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.TUser;
import pt.isec.deis.lei.pd.trabprat.server.Main;
import pt.isec.deis.lei.pd.trabprat.server.config.DefaultSvMsg;
import pt.isec.deis.lei.pd.trabprat.server.db.DatabaseWrapper;
import pt.isec.deis.lei.pd.trabprat.thread.tcp.TCPHelper;

public class TCPUserHandler implements Runnable {

    private final Socket UserSocket;
    private final ObjectOutputStream oOS;
    private final Command Cmd;

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
        DatabaseWrapper con;
        Command sendCmd;
        TUser user = (TUser) Cmd.Body;
        TUser info;
        synchronized (Main.SV_LOCK) {
            con = Main.SV_CFG.DB;
            info = con.getUserByName(user.getUName());
        }
        if (info != null) {
            // Send internal error
            sendCmd = new Command(ECommand.CMD_BAD_REQUEST, DefaultSvMsg.SV_USER_EXISTS);
            TCPHelper.SendTCPCommand(oOS, sendCmd);
        } else {
            synchronized (Main.SV_LOCK) {
                info = con.getUserByUsername(user.getUUsername());
            }
            if (info != null) {
                sendCmd = new Command(ECommand.CMD_BAD_REQUEST, DefaultSvMsg.SV_USERNAME_EXISTS);
                TCPHelper.SendTCPCommand(oOS, sendCmd);
            } else {
                // Check if password is good
                // Encrypt password
                // Store information
                int inserted;
                synchronized (Main.SV_LOCK) {
                    inserted = con.insertUser(user);
                }
                if (inserted <= 0) {
                    // Tell client, server couldn't register
                    sendCmd = new Command(ECommand.CMD_SERVICE_UNAVAILABLE, DefaultSvMsg.SV_INTERNAL_ERROR);
                    TCPHelper.SendTCPCommand(oOS, sendCmd);
                } else {
                    // Send OK to the client
                    sendCmd = new Command(ECommand.CMD_CREATED);
                    TCPHelper.SendTCPCommand(oOS, sendCmd);
                    // After created, the client should send the photo asynchronously
                    // Announce to other servers via multicast
                }
            }
        }
    }

    public TCPUserHandler(Socket UserSocket, Command Cmd) throws IOException {
        this.UserSocket = UserSocket;
        this.oOS = new ObjectOutputStream(this.UserSocket.getOutputStream());
        this.Cmd = Cmd;
    }
}
