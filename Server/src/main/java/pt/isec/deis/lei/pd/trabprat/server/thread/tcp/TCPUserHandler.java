package pt.isec.deis.lei.pd.trabprat.server.thread.tcp;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
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
                    // Check if user already exists in the database
                    // Check if password is good
                    // Encrypt password
                    // Store information
                    // Send OK to the client
                    // Announce to other servers via multicast
                }
                case ECommand.CMD_LOGIN: {
                    // Check if user exists in the database
                    // Check if password is good and matches
                    // Send OK to the client
                    // Add user to the client list
                    // Announce to other servers via multicast
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

    public TCPUserHandler(Socket UserSocket, Command Cmd) throws IOException {
        this.UserSocket = UserSocket;
        this.oOS = new ObjectOutputStream(this.UserSocket.getOutputStream());
        this.Cmd = Cmd;
    }
}
