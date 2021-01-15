package pt.isec.deis.lei.pd.trabprat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.model.GenericPair;
import pt.isec.deis.lei.pd.trabprat.model.Server;
import pt.isec.deis.lei.pd.trabprat.model.TUser;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;

public class CommandLineHandler {

    private final BufferedReader Reader;
    private final BufferedWriter Writer;
    private final ServerConfig SV_CFG;

    public void Initialize() throws IOException {
        boolean Continue = true;
        String Command;

        while (Continue) {
            Write("Admin: ");
            Command = ReadLine();
            if (Command.equals("exit")) {
                System.exit(0);
            } else {
                HandleCommand(Command);
            }
        }
        SV_CFG.BroadcastMessage(new Command(ECommand.CMD_SERVER_SHUTDOWN));
        SV_CFG.Clients.values().forEach(u -> {
            SV_CFG.MulticastMessage(new Command(ECommand.CMD_LOGOUT, new GenericPair<>(SV_CFG.ServerID, u.key)));
        });
        SV_CFG.MulticastMessage(new Command(ECommand.CMD_BYE, new GenericPair<>(SV_CFG.ServerID, new Server(SV_CFG.ServerID))));
    }

    private String ReadLine() throws IOException {
        return Reader.readLine();
    }

    private void Write(String text) throws IOException {
        _Write(text, false);
    }

    private void WriteLine(String text) throws IOException {
        _Write(text, true);
    }

    private void _Write(String text, boolean newLine) throws IOException {
        Writer.write(text);
        if (newLine) {
            Writer.newLine();
        }
        Writer.flush();
    }

    private void HandleCommand(String Command) throws IOException {
        // Do stuff here
        String cmd = Command.toLowerCase();
        switch (cmd) {
            case "users": {
                WriteLine(HandleUsers(cmd));
                break;
            }
            case "online": {
                WriteLine(HandleOnline(cmd));
                break;
            }
            case "servers": {
                WriteLine(HandleServers(cmd));
                break;
            }
            default: {
                break;
            }
        }
    }

    private String HandleUsers(String cmd) {
        StringBuilder str = new StringBuilder();
        str.append(cmd).append(":\n");
        ArrayList<TUser> info;
        synchronized (SV_CFG) {
            info = SV_CFG.DB.getAllUsers();
        }
        if (info != null) {
            for (int i = 0; i < info.size(); i++) {
                str.append(info.get(i).toString());
                str.append("\n");
            }
        }
        str.append("End ").append(cmd).append(".");
        return str.toString();
    }

    private String HandleOnline(String cmd) {
        StringBuilder str = new StringBuilder();
        str.append(cmd).append(":\n");
        synchronized (SV_CFG) {
            var info = SV_CFG.Clients;
            if (info != null) {
                for (var c : info.values()) {
                    str.append(c.toString());
                    str.append("\n");
                }
            }
        }
        str.append("End ").append(cmd).append(".");
        return str.toString();
    }

    private String HandleServers(String cmd) {
        StringBuilder str = new StringBuilder();
        str.append(cmd).append(":\n");
        synchronized (SV_CFG) {
            var info = SV_CFG.ServerList;
            for (Server s : info) {
                str.append(s.toString());
                str.append("\n");
            }
        }
        str.append("End ").append(cmd).append(".");
        return str.toString();
    }

    public CommandLineHandler(InputStream Reader, OutputStream Writer, ServerConfig SV_CFG) {
        this.Reader = new BufferedReader(new InputStreamReader(Reader));
        this.Writer = new BufferedWriter(new OutputStreamWriter(Writer));
        this.SV_CFG = SV_CFG;
    }
}
