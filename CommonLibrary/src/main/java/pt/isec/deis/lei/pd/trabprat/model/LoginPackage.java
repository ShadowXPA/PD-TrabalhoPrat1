package pt.isec.deis.lei.pd.trabprat.model;

import java.io.Serializable;
import java.util.ArrayList;

public class LoginPackage implements Serializable {

    public final ArrayList<TUser> Users;
    public final ArrayList<TChannel> Channels;
    public final ArrayList<TUser> DMUsers;

    public LoginPackage() {
        this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public LoginPackage(ArrayList<TUser> Users, ArrayList<TChannel> Channels, ArrayList<TUser> DMUsers) {
        this.Users = Users;
        this.Channels = Channels;
        this.DMUsers = DMUsers;
    }
}
