package pt.isec.deis.lei.pd.trabprat.model;

import java.io.Serializable;
import java.util.ArrayList;

public class LoginPackage implements Serializable {

    public final TUser LoginAuthor;
    public final ArrayList<TUser> Users;
    public final ArrayList<TChannel> Channels;
    public final ArrayList<TUser> DMUsers;
    public final ArrayList<TChannelUser> ChannelUsers;

    public LoginPackage(TUser LoginAuthor) {
        this(LoginAuthor, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public LoginPackage(TUser LoginAuthor, ArrayList<TUser> Users, ArrayList<TChannel> Channels, ArrayList<TUser> DMUsers, ArrayList<TChannelUser> ChannelUsers) {
        this.LoginAuthor = LoginAuthor;
        this.Users = Users;
        this.Channels = Channels;
        this.DMUsers = DMUsers;
        this.ChannelUsers = ChannelUsers;
    }
}
