package pt.isec.deis.lei.pd.trabprat.client.config;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import javafx.stage.Stage;
import pt.isec.deis.lei.pd.trabprat.model.Server;
import pt.isec.deis.lei.pd.trabprat.model.TChannel;
import pt.isec.deis.lei.pd.trabprat.model.TChannelMessage;
import pt.isec.deis.lei.pd.trabprat.model.TChannelUser;
import pt.isec.deis.lei.pd.trabprat.model.TDirectMessage;
import pt.isec.deis.lei.pd.trabprat.model.TMessage;
import pt.isec.deis.lei.pd.trabprat.model.TUser;

public class ClientConfig {

    public ArrayList<Server> ServerList;
    public final Object LockSL = new Object();
    public ArrayList<TUser> OnlineUsers;
    //List of channels
    public ArrayList<TChannel> ChannelsList;
    public ArrayList<TUser> DMUsers;
    //list of users connected to channels
    public ArrayList<TChannelUser> ChannelUsers;
    //Lock channel users
    public final Object LockCU = new Object();
    //list of messages from the channel
    public volatile ArrayList<TChannelMessage> ChannelMessage;
    //Lock channel messages
    public final Object LockCM = new Object();
    public volatile ArrayList<TDirectMessage> DirectMessages;
    //list for found users
    public ArrayList<TUser> FoundUsers;
    //Lock found users
    public final Object LockFo = new Object();
    //Lock channel list
    public final Object LockCL = new Object();
    //Lock DM users
    public final Object LockDMUsers = new Object();
    //Lock online users
    public final Object LockOUsers = new Object();
    //selected channel
    public Object SelectedChannel;
    public TUser MyUser;
    public Stage Stage;
    public Server server;
    private Socket socket; //socket TCP
    private ObjectOutputStream OOS; //ObjectOutputStream para o TCP
    private ObjectInputStream OIS;  //ObjectInputStream para o TCP
    private String Username;
    private boolean LoggedIn = false;
    public volatile boolean closing = false;

    public String getUsername() {
        return this.Username;
    }

    public synchronized boolean isLoggedIn() {
        return LoggedIn;
    }

    public synchronized void setLogin() {
        if (!this.LoggedIn) {
            this.LoggedIn = true;
        }
    }

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

    public TChannel GetChannelByCName(String CName) {
        for (int i = 0; i < ChannelsList.size(); i++) {
            if (ChannelsList.get(i).getCName().equals(CName)) {
                return ChannelsList.get(i);
            }
        }
        return null;
    }

    public int[] GetNumMesagesAndFiles() {
        int[] array = new int[2];
        for (int i = 0; i < ChannelMessage.size(); i++) {
            if (ChannelMessage.get(i).getMID().getMPath() == null) {
                array[0]++;
            } else {
                array[1]++;
            }
        }
        return array;
    }

    public TMessage GetMessageByID(int ID) {
        if (SelectedChannel instanceof TChannel) {
            for (int i = 0; i < ChannelMessage.size(); i++) {
                if (ChannelMessage.get(i).getMID().getMID() == ID) {
                    return ChannelMessage.get(i).getMID();
                }
            }
        } else if (SelectedChannel instanceof TUser) {
            for (int i = 0; i < DirectMessages.size(); i++) {
                if (DirectMessages.get(i).getMID().getMID() == ID) {
                    return DirectMessages.get(i).getMID();
                }
            }
        }
        return null;
    }

    public TUser GetDMByUName(String UName) {
        for (int i = 0; i < DMUsers.size(); i++) {
            if (DMUsers.get(i).getUName().equals(UName)) {
                return DMUsers.get(i);
            }
        }
        return null;
    }

    public int[] GetNumMesagesAndFilesDM() {
        int[] array = new int[2];
        for (int i = 0; i < DirectMessages.size(); i++) {
            if (DirectMessages.get(i).getMID().getMPath() == null) {
                array[0]++;
            } else {
                array[1]++;
            }
        }
        return array;
    }
}
