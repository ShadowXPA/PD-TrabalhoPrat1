package pt.isec.deis.lei.pd.trabprat.server.model;

import java.io.Serializable;
import java.util.ArrayList;
import pt.isec.deis.lei.pd.trabprat.model.GenericPair;
import pt.isec.deis.lei.pd.trabprat.model.TChannel;
import pt.isec.deis.lei.pd.trabprat.model.TMessage;
import pt.isec.deis.lei.pd.trabprat.model.TUser;

public class SyncDBPackage implements Serializable {

    public final ArrayList<TUser> Users;
    public final ArrayList<TMessage> Messages;
    public final ArrayList<TChannel> Channels;
    public final ArrayList<GenericPair<Integer, Integer>> ChannelUsers;
    public final ArrayList<GenericPair<Integer, Integer>> ChannelMessages;
    public final ArrayList<GenericPair<Integer, Integer>> DirectMessages;

    public SyncDBPackage() {
        this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public SyncDBPackage(ArrayList<TUser> Users, ArrayList<TMessage> Messages, ArrayList<TChannel> Channels, ArrayList<GenericPair<Integer, Integer>> ChannelUsers, ArrayList<GenericPair<Integer, Integer>> ChannelMessages, ArrayList<GenericPair<Integer, Integer>> DirectMessages) {
        this.Users = Users;
        this.Messages = Messages;
        this.Channels = Channels;
        this.ChannelUsers = ChannelUsers;
        this.ChannelMessages = ChannelMessages;
        this.DirectMessages = DirectMessages;
    }
}
