package pt.isec.deis.lei.pd.trabprat.server.config;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import pt.isec.deis.lei.pd.trabprat.comparator.ServerComparator;
import pt.isec.deis.lei.pd.trabprat.model.Server;
import pt.isec.deis.lei.pd.trabprat.server.db.Database;
import pt.isec.deis.lei.pd.trabprat.server.db.DatabaseWrapper;
import pt.isec.deis.lei.pd.trabprat.server.model.Client;

public class ServerConfig {

    public final Database DBConnection;
    public final DatabaseWrapper DB;
    public final ServerComparator SvComp;
    public final ArrayList<Server> ServerList;
    public final HashMap<Socket, Client> ClientList;
    public final InetAddress ExternalIP;

    public void SortServerList() {
        ServerList.sort(SvComp);
    }

    public ServerConfig(Database DBConnection, String ExternalIP) throws UnknownHostException {
        this.DBConnection = DBConnection;
        this.DB = new DatabaseWrapper(this.DBConnection);
        this.SvComp = new ServerComparator();
        this.ExternalIP = InetAddress.getByName(ExternalIP);
        this.ServerList = new ArrayList<>();
        this.ClientList = new HashMap<>();
    }
}
