package pt.isec.deis.lei.pd.trabprat.server.config;

import java.util.ArrayList;
import pt.isec.deis.lei.pd.trabprat.model.Server;
import pt.isec.deis.lei.pd.trabprat.server.db.Database;
import pt.isec.deis.lei.pd.trabprat.server.model.Client;

public class ServerConfig {
    private final Database DBConnection;
    public ArrayList<Server> ServerList;
    public final Object ServerListLock = new Object();
    public ArrayList<Client> ClientList;
    public final Object ClientListLock = new Object();

    public Database getDBConnection() {
        return DBConnection;
    }

    public ServerConfig(Database DBConnection) {
        this.DBConnection = DBConnection;
    }
}
