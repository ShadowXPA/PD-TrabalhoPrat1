package pt.isec.deis.lei.pd.trabprat.config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;

public final class DefaultConfig {

    private DefaultConfig() {
    }

    public static final String getExternalIP() {
        String[] Urls = {
            "https://api-ipv4.ip.sb/ip",
            "https://checkip.amazonaws.com/",
            "https://api.ipify.org"
        };
        for (String Url : Urls) {
            try {
                URL ExtIP = new URL(Url);
                BufferedReader br = new BufferedReader(new InputStreamReader(ExtIP.openStream()));
                return br.readLine();
            } catch (Exception ex) {
                ExceptionHandler.ShowException(ex);
            }
        }
        return "127.0.0.1";
    }

    public static final String DEFAULT_MULTICAST_IP = "239.14.3.22";

    // Default Packet Sizes
    public static final int DEFAULT_UDP_PACKET_SIZE = 5120;
    public static final int DEFAULT_TCP_PACKET_SIZE = 5120;

    // Default Ports
    public static final int DEFAULT_MULTICAST_PORT = 5432;
    public static final int DEFAULT_UDP_PORT = 5433;
    public static final int DEFAULT_TCP_PORT = 5434;
    public static final int DEFAULT_RMI_PORT = 1099;
}
