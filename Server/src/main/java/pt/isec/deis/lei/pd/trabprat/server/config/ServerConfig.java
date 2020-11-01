package pt.isec.deis.lei.pd.trabprat.server.config;

import java.util.ArrayList;
import pt.isec.deis.lei.pd.trabprat.comparator.ServerComparator;
import pt.isec.deis.lei.pd.trabprat.model.Server;
import pt.isec.deis.lei.pd.trabprat.server.db.Database;
import pt.isec.deis.lei.pd.trabprat.server.db.DatabaseWrapper;
import pt.isec.deis.lei.pd.trabprat.server.model.Client;

public class ServerConfig {
    public final Database DBConnection;
    public final DatabaseWrapper DB;
    public ServerComparator SvComp;
    public ArrayList<Server> ServerList;
    public final Object ServerListLock = new Object();
    public ArrayList<Client> ClientList;
    public final Object ClientListLock = new Object();

    public void SortServerList() {
//        synchronized (ServerListLock) {
            ServerList.sort(SvComp);
//        }
    }

    public ServerConfig(Database DBConnection) {
        this.DBConnection = DBConnection;
        this.DB = new DatabaseWrapper(this.DBConnection);
        this.SvComp = new ServerComparator();
    }
}
