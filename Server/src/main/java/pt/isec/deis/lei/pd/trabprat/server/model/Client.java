package pt.isec.deis.lei.pd.trabprat.server.model;

import java.net.Socket;
import pt.isec.deis.lei.pd.trabprat.model.TUser;

public class Client {
    public final TUser User;
    public final Socket Socket;

    public Client(TUser User, Socket Socket) {
        this.User = User;
        this.Socket = Socket;
    }
}
