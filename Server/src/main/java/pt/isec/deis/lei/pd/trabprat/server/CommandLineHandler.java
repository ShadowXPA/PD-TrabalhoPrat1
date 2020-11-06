package pt.isec.deis.lei.pd.trabprat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import pt.isec.deis.lei.pd.trabprat.model.TUser;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;
import pt.isec.deis.lei.pd.trabprat.server.explorer.ExplorerController;

public class CommandLineHandler {

    private final BufferedReader Reader;
    private final BufferedWriter Writer;
    private final ServerConfig SV_CFG;

    public void Initialize() throws IOException {
        boolean Continue = true;
        String Command;
        ExplorerController.CreateBaseDirectories(SV_CFG.DBConnection.getSchema());

        while (Continue) {
            Write("Admin: ");
            Command = ReadLine();
            if (Command.equals("exit")) {
                Continue = false;
            } else {
                HandleCommand(Command);
            }
        }
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

    public CommandLineHandler(InputStream Reader, OutputStream Writer, ServerConfig SV_CFG) {
        this.Reader = new BufferedReader(new InputStreamReader(Reader));
        this.Writer = new BufferedWriter(new OutputStreamWriter(Writer));
        this.SV_CFG = SV_CFG;
    }
}
