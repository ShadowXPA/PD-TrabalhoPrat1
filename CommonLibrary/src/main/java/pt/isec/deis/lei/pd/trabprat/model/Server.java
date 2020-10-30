package pt.isec.deis.lei.pd.trabprat.model;

import java.io.Serializable;
import java.net.InetAddress;

public class Server implements Serializable {
    private final InetAddress Address;
    private final int Port;
    private int UserCount;

    public final InetAddress getAddress() {
        return Address;
    }

    public final int getPort() {
        return Port;
    }

    public final int getUserCount() {
        return UserCount;
    }

    public final void setUserCount(int UserCount) {
        if (UserCount >= 0) {
            this.UserCount = UserCount;
        } else {
            this.UserCount = 0;
        }
    }

    public Server(InetAddress Address, int Port, int UserCount) {
        this.Address = Address;
        this.Port = Port;
        this.setUserCount(UserCount);
    }
}
