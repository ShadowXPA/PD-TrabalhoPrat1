package pt.isec.deis.lei.pd.trabprat.server.model;

import java.io.ObjectOutputStream;
import pt.isec.deis.lei.pd.trabprat.model.TUser;

public class Client {
    public final TUser User;
    public final ObjectOutputStream oOS;

    @Override
    public String toString() {
        return User.toString();
    }

    public Client(TUser User, ObjectOutputStream oOS) {
        this.User = User;
        this.oOS = oOS;
    }
}
