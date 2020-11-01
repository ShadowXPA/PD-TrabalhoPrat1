package pt.isec.deis.lei.pd.trabprat.client.config;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import javafx.stage.Stage;
import pt.isec.deis.lei.pd.trabprat.model.Server;

public class ClientConfig {
    public ArrayList<Server> ServerList;
    public Stage Stage;
    public Server server;
    public Socket socket; //socket TCP
    public ObjectOutputStream OOS; //ObjectOutputStream para o TCP
    public ObjectInputStream OIS;  //ObjectInputStream para o TCP
    
}
