package pt.isec.deis.lei.pd.trabprat.client.config;

import java.io.IOException;
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
    private Socket socket; //socket TCP
    private ObjectOutputStream OOS; //ObjectOutputStream para o TCP
    private ObjectInputStream OIS;  //ObjectInputStream para o TCP

    public void setSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.OOS = new ObjectOutputStream(socket.getOutputStream());
        this.OIS = new ObjectInputStream(socket.getInputStream());
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectOutputStream getOOS() {
        return OOS;
    }

    public ObjectInputStream getOIS() {
        return OIS;
    }
}
