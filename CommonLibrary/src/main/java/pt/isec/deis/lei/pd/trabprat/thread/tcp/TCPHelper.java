package pt.isec.deis.lei.pd.trabprat.thread.tcp;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import pt.isec.deis.lei.pd.trabprat.communication.Command;

public final class TCPHelper {

    public static void SendTCPCommand(ObjectOutputStream oOS, Command cmd) throws IOException {
        oOS.writeUnshared(cmd);
        oOS.flush();
    }

    private TCPHelper() {
    }
}
