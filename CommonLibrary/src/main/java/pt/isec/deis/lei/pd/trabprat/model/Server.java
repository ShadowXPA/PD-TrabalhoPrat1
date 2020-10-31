package pt.isec.deis.lei.pd.trabprat.model;

import java.io.Serializable;
import java.net.InetAddress;

public class Server implements Serializable {
    private final InetAddress Address;
    private final int UDPPort, TCPPort;
    private int UserCount;

    public final InetAddress getAddress() {
        return Address;
    }

    public final int getUserCount() {
        return UserCount;
    }

    public int getUDPPort() {
        return UDPPort;
    }

    public int getTCPPort() {
        return TCPPort;
    }

    public final void setUserCount(int UserCount) {
        if (UserCount >= 0) {
            this.UserCount = UserCount;
        } else {
            this.UserCount = 0;
        }
    }

    public Server(InetAddress Address, int UDPPort, int TCPPort, int UserCount) {
        this.Address = Address;
        this.UDPPort = UDPPort;
        this.TCPPort = TCPPort;
        this.setUserCount(UserCount);
    }
}
