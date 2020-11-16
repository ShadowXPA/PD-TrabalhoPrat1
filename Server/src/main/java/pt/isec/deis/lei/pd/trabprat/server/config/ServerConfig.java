package pt.isec.deis.lei.pd.trabprat.server.config;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.comparator.ServerComparator;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.Pair;
import pt.isec.deis.lei.pd.trabprat.model.Server;
import pt.isec.deis.lei.pd.trabprat.model.TUser;
import pt.isec.deis.lei.pd.trabprat.server.db.Database;
import pt.isec.deis.lei.pd.trabprat.server.db.DatabaseWrapper;
import pt.isec.deis.lei.pd.trabprat.server.model.Client;
import pt.isec.deis.lei.pd.trabprat.thread.tcp.TCPHelper;

public class ServerConfig {

    public final Database DBConnection;
    public final DatabaseWrapper DB;
    public final ServerComparator SvComp;
    public final ArrayList<Server> ServerList;
//    public final HashMap<Socket, Client> ClientList;
//    public final HashMap<Socket, GenericPair<TUser, ObjectOutputStream>> Clients;
    public final HashMap<Socket, Pair<TUser, ObjectOutputStream>> Clients;
    public final InetAddress ExternalIP;
    public final InetAddress InternalIP;

//    public boolean ClientListContains(Client user) {
//        return ClientList.containsValue(user);
//    }
    public boolean ClientListContains(Pair<TUser, ObjectOutputStream> user) {
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

    public void BroadcastOnlineActivity() {
        var newUsers = GetAllOnlineUsers();
        var newCmd = new Command(ECommand.CMD_ONLINE_USERS, newUsers);
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
        }
        SortServerList();
    }

    public void SortServerList() {
        ServerList.sort(SvComp);
    }

    public ServerConfig(Database DBConnection, String ExternalIP, String InternalIP) throws UnknownHostException {
        this.DBConnection = DBConnection;
        this.DB = new DatabaseWrapper(this.DBConnection);
        this.SvComp = new ServerComparator();
        this.ExternalIP = InetAddress.getByName(ExternalIP);
        this.InternalIP = InetAddress.getByName(InternalIP);
        this.ServerList = new ArrayList<>();
//        this.ClientList = new HashMap<>();
        this.Clients = new HashMap<>();
    }
}
