package pt.isec.deis.lei.pd.trabprat.server.model;

import java.io.ObjectOutputStream;
import java.util.Objects;
import pt.isec.deis.lei.pd.trabprat.model.TUser;

public class Client {

    public final TUser User;
    public final ObjectOutputStream oOS;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.User);
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
        final Client other = (Client) obj;
        return Objects.equals(this.User, other.User);
    }

    @Override
    public String toString() {
        return User.toString();
    }

    public Client(TUser User, ObjectOutputStream oOS) {
        this.User = User;
        this.oOS = oOS;
    }
}
