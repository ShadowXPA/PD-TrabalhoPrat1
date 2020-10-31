package pt.isec.deis.lei.pd.trabprat.server.config;

import java.util.ArrayList;
import pt.isec.deis.lei.pd.trabprat.model.Server;
import pt.isec.deis.lei.pd.trabprat.server.db.Database;

public class ServerConfig {
    private final Database DBConnection;
    public ArrayList<Server> ServerList;
    public static final Object ServerListLock = new Object();

    public Database getDBConnection() {
        return DBConnection;
    }

    public ServerConfig(Database DBConnection) {
        this.DBConnection = DBConnection;
    }
}
