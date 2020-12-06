package pt.isec.deis.lei.pd.trabprat.model;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Objects;

public class Server implements Serializable {

    public final String ServerID;
    public final long ServerStart;
    private final InetAddress Address;
    private final int UDPPort, TCPPort;
    private volatile int UserCount;
    private boolean Alive;

    public boolean isAlive() {
        return Alive;
    }

    public void setAlive(boolean Alive) {
        this.Alive = Alive;
    }

    public final InetAddress getAddress() {
        return Address;
    }

    public final int getUserCount() {
        return UserCount;
    }

    public final int getUDPPort() {
        return UDPPort;
    }

    public final int getTCPPort() {
        return TCPPort;
    }

    public final void setUserCount(int UserCount) {
        if (UserCount >= 0) {
            this.UserCount = UserCount;
        } else {
            this.UserCount = 0;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.ServerID);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Server other = (Server) obj;
        return Objects.equals(this.ServerID, other.ServerID);
    }

    @Override
    public String toString() {
        return "Server{" + "ServerID=" + ServerID + ", Address=" + Address.getHostAddress() + ", UDPPort=" + UDPPort + ", TCPPort=" + TCPPort + ", UserCount=" + UserCount + '}';
    }

    public Server(String ServerID) {
        this(ServerID, 0, null, 0, 0, 0);
    }

    public Server(String ServerID, long ServerStart, InetAddress Address, int UDPPort, int TCPPort, int UserCount) {
        this.ServerID = ServerID;
        this.ServerStart = ServerStart;
        this.Address = Address;
        this.UDPPort = UDPPort;
        this.TCPPort = TCPPort;
        this.setUserCount(UserCount);
        this.Alive = true;
    }
}
