package pt.isec.deis.lei.pd.trabprat.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;

public final class DefaultConfig {
    private DefaultConfig() {}

    public static final String getExternalIP() {
        try {
            URL ExtIP = new URL("https://checkip.amazonaws.com/");
            BufferedReader br = new BufferedReader(new InputStreamReader(ExtIP.openStream()));
            return br.readLine();
        } catch (Exception ex) {
            ExceptionHandler.ShowException(ex);
        }
        return "Unknown";
    }

    // Default Packet Sizes
    public static final int DEFAULT_UDP_PACKET_SIZE = 2048;
    public static final int DEFAULT_TCP_PACKET_SIZE = 2048;

    // Default Ports
    public static final int DEFAULT_MULTICAST_PORT = 5432;
    public static final int DEFAULT_UDP_PORT = 5433;
    public static final int DEFAULT_TCP_PORT = 5434;
}
