package pt.isec.deis.lei.pd.trabprat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class CommandLineHandler {

    private final BufferedReader Reader;
    private final BufferedWriter Writer;

    public void Initialize() throws IOException {
        boolean Continue = true;
        String Command;

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
        }
    }

    private String HandleUsers(String cmd) {
        StringBuilder str = new StringBuilder();
        str.append(cmd).append(":\n");
        ArrayList<HashMap<String, String>> info;
        synchronized (Main.SV_LOCK) {
            info = Main.SV_CFG.getDBConnection().Select("tuser", null, null, null, null);
        }
        for (int i = 0; i < info.size(); i++) {
            var hm = info.get(i);
            var keys = hm.keySet();
            for (var key : keys) {
                str.append("Column: ").append(key);
                str.append("\nValue: ").append(hm.get(key));
                str.append("\n");
            }
            str.append("--------------------------------------\n");
        }
        str.append("End ").append(cmd).append(".");
        return str.toString();
    }

    public CommandLineHandler(InputStream Reader, OutputStream Writer) {
        this.Reader = new BufferedReader(new InputStreamReader(Reader));
        this.Writer = new BufferedWriter(new OutputStreamWriter(Writer));

    }
}
