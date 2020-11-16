package pt.isec.deis.lei.pd.trabprat.model;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Objects;

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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.Address.getHostAddress());
        hash = 67 * hash + this.UDPPort;
        hash = 67 * hash + this.TCPPort;
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
        if (this.UDPPort != other.UDPPort) {
            return false;
        }
        if (this.TCPPort != other.TCPPort) {
            return false;
        }
        return Objects.equals(this.Address.getHostAddress(), other.Address.getHostAddress());
    }

    @Override
    public String toString() {
        return "Server{" + "Address=" + Address.getHostAddress() + ", UDPPort=" + UDPPort + ", TCPPort=" + TCPPort + ", UserCount=" + UserCount + '}';
    }

    public Server(InetAddress Address, int UDPPort, int TCPPort, int UserCount) {
        this.Address = Address;
        this.UDPPort = UDPPort;
        this.TCPPort = TCPPort;
        this.setUserCount(UserCount);
    }
}
