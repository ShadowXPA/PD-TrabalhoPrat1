package pt.isec.deis.lei.pd.trabprat.server.config;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.comparator.ServerComparator;
import pt.isec.deis.lei.pd.trabprat.config.DefaultConfig;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.GenericPair;
import pt.isec.deis.lei.pd.trabprat.model.Server;
import pt.isec.deis.lei.pd.trabprat.model.TUser;
import pt.isec.deis.lei.pd.trabprat.server.db.Database;
import pt.isec.deis.lei.pd.trabprat.server.db.DatabaseWrapper;
import pt.isec.deis.lei.pd.trabprat.thread.tcp.TCPHelper;
import pt.isec.deis.lei.pd.trabprat.thread.udp.UDPHelper;

public class ServerConfig {

    public final String ServerID;
    public final long ServerStart;
    public final Database DBConnection;
    public final DatabaseWrapper DB;
    public final ServerComparator SvComp;
    public final ArrayList<Server> ServerList;
//    public final HashMap<Socket, Client> ClientList;
    public final HashMap<Socket, GenericPair<TUser, ObjectOutputStream>> Clients;
    public final InetAddress ExternalIP;
    public final InetAddress InternalIP;
    public final int MulticastPort;
    public final int UDPPort;
    public final int TCPPort;
    public MulticastSocket MCSocket;
    public InetAddress MCAddress;

//    public boolean ClientListContains(Client user) {
//        return ClientList.containsValue(user);
//    }
    public boolean ClientListContains(GenericPair<TUser, ObjectOutputStream> user) {
        return Clients.containsValue(user);
    }

    public ArrayList<TUser> GetAllOnlineUsers() {
        var temp = new ArrayList<TUser>();
        var users = Clients.values().iterator();
        while (users.hasNext()) {
            var cl = users.next().key;//User;
            temp.add(new TUser(cl.getUID(),
                    cl.getUName(), cl.getUUsername(),
                    null, cl.getUPhoto(), cl.getUDate()));
        }
        return temp;
    }

    public GenericPair<TUser, ObjectOutputStream> GetUser(TUser user) {
        var temp = new GenericPair<TUser, ObjectOutputStream>(user, null);
        if (ClientListContains(temp)) {
            return _GetUser(temp);
        }
        return null;
    }

    private GenericPair<TUser, ObjectOutputStream> _GetUser(GenericPair<TUser, ObjectOutputStream> user) {
        var users = Clients.values().iterator();
        while (users.hasNext()) {
            var i = users.next();
            if (i.equals(user)) {
                return i;
            }
        }
        return null;
    }

    public void BroadcastOnlineActivity() {
        var newUsers = GetAllOnlineUsers();
        var newCmd = new Command(ECommand.CMD_ONLINE_USERS, newUsers);
        BroadcastMessage(newCmd);
    }

    public void BroadcastServerList() {
        var newCmd = new Command(ECommand.CMD_UPDATE_SERVERS, ServerList);
        BroadcastMessage(newCmd);
    }

    public void BroadcastMessage(Command cmd) {
        var users = Clients.values().iterator();
        while (users.hasNext()) {
            try {
                var oos = users.next().value;//User;
                TCPHelper.SendTCPCommand(oos, cmd);
            } catch (Exception ex) {
                ExceptionHandler.ShowException(ex);
            }
        }
    }

    public void AddOrUpdateServer(Server s) {
        int Index = ServerList.indexOf(s);
        if (Index == -1) {
            ServerList.add(s);
        } else {
            ServerList.get(Index).setUserCount(s.getUserCount());
            ServerList.get(Index).setAlive(true);
        }
        SortServerList();
    }

    public void SortServerList() {
        ServerList.sort(SvComp);
    }

    public void RemoveDeadServers() {
        ServerList.removeIf(s -> !s.isAlive());
    }

    public void MulticastMessage(Command cmd) {
        try {
            UDPHelper.SendMulticastCommand(MCSocket, MCAddress, MulticastPort, cmd);
        } catch (IOException ex) {
            ExceptionHandler.ShowException(ex);
        }
    }

    public ServerConfig(Database DBConnection, String ExternalIP, String InternalIP, int MulticastPort, int UDPPort, int TCPPort) throws UnknownHostException {
        this.DBConnection = DBConnection;
        this.DB = new DatabaseWrapper(this.DBConnection);
        this.SvComp = new ServerComparator();
        this.ExternalIP = InetAddress.getByName(ExternalIP);
        this.InternalIP = InetAddress.getByName(InternalIP);
        this.ServerList = new ArrayList<>();
//        this.ClientList = new HashMap<>();
        this.Clients = new HashMap<>();
        this.MulticastPort = (MulticastPort == 0) ? DefaultConfig.DEFAULT_MULTICAST_PORT : MulticastPort;
        this.UDPPort = (UDPPort == 0) ? DefaultConfig.DEFAULT_UDP_PORT : UDPPort;
        this.TCPPort = (TCPPort == 0) ? DefaultConfig.DEFAULT_TCP_PORT : TCPPort;
        this.ServerID = UUID.randomUUID().toString();
        this.ServerStart = (new Date()).getTime();
    }
}
