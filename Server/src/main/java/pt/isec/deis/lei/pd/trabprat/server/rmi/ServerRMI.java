package pt.isec.deis.lei.pd.trabprat.server.rmi;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.GenericPair;
import pt.isec.deis.lei.pd.trabprat.model.TDirectMessage;
import pt.isec.deis.lei.pd.trabprat.model.TMessage;
import pt.isec.deis.lei.pd.trabprat.model.TUser;
import pt.isec.deis.lei.pd.trabprat.rmi.RemoteObserverRMI;
import pt.isec.deis.lei.pd.trabprat.rmi.RemoteServerRMI;
import pt.isec.deis.lei.pd.trabprat.server.Main;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;
import pt.isec.deis.lei.pd.trabprat.server.db.DatabaseWrapper;
import pt.isec.deis.lei.pd.trabprat.server.explorer.ExplorerController;
import pt.isec.deis.lei.pd.trabprat.thread.tcp.TCPHelper;

public class ServerRMI extends UnicastRemoteObject implements RemoteServerRMI {

    private final ServerConfig SV_CFG;

    @Override
    public void registerUser(RemoteObserverRMI observer, TUser user) throws RemoteException {
        synchronized (SV_CFG) {
            DatabaseWrapper db = SV_CFG.DB;
            TUser info = db.getUserByName(user.getUName());
            if (info != null) {
                observer.notifyObserver("Name already in use!");
                Main.Log("[RMIClient]", "Name '" + user.getUName() + "' already in use!");
            } else {
                info = db.getUserByUsername(user.getUUsername());
                if (info != null) {
                    observer.notifyObserver("Username already in use!");
                    Main.Log("[RMIClient]", "Username '" + user.getUUsername() + "' already in use!");
                } else {
                    String fullDir = ExplorerController.AVATAR_SUBDIR + "/default.png";
                    TUser insUser = new TUser(0, user.getUName(), user.getUUsername(), user.getUPassword(), fullDir, 0);
                    TUser lastUser;
                    int inserted;
                    inserted = db.insertUser(insUser);
                    lastUser = db.getLastUser();
                    if (inserted <= 0) {
                        // Tell client, server couldn't register
                        observer.notifyObserver("Couldn´t register the new user!");
                        Main.Log("[RMIClient]", "User '" + user.getUUsername() + "' couldn´t be registered!");
                    } else {
                        // Send OK to the client
                        observer.notifyObserver("Registered the new user!");
                        // After created, the client should send the photo asynchronously
                        // Announce to other servers via multicast
                        SV_CFG.MulticastMessage(new Command(ECommand.CMD_CREATED, new GenericPair<>(SV_CFG.ServerID, lastUser)));
                        Main.Log("[RMIClient]", "User '" + user.getUUsername() + "' registered!");
                    }
                }
            }
        }
    }

    @Override
    public void sendMessage(RemoteObserverRMI observer, TMessage message) throws RemoteException {
        synchronized (SV_CFG) {
            DatabaseWrapper db = SV_CFG.DB;
            for (var users : SV_CFG.Clients.values()) {
                TUser user = users.key;
                TDirectMessage dm = new TDirectMessage(message, user);
                int i = db.insertDirectMessage(dm.getUID(), message);
                if (i > 0) {
                    TMessage lastMessage = db.getLastMessage();
                    TDirectMessage lastDM = new TDirectMessage(lastMessage, dm.getUID());

                    ArrayList<TDirectMessage> dmL = db.getAllDMByUserIDAndOtherID(dm.getMID().getMUID().getUID(), dm.getUID().getUID());
                    // Broadcast new message to all users
                    ArrayList<TUser> dmU = db.getOtherUserFromDM(db.getAllDMByUserID(dm.getUID().getUID()), dm.getUID());
                    Command sendCmd = new Command(ECommand.CMD_CREATED, new GenericPair<>(dmL, dmU));
                    try {
                        TCPHelper.SendTCPCommand(users.value, sendCmd);
                    } catch (IOException ex) {
                        ExceptionHandler.ShowException(ex);
                    }
                    // Send through multicast
                    SV_CFG.MulticastMessage(new Command(ECommand.CMD_CREATED,
                            new GenericPair<>(SV_CFG.ServerID, lastDM)));
                    observer.notifyObserver("The message has been sent to '" + user.getUUsername()+ "'!");
                    Main.Log("[RMIClient]", "The message has been sent to '" + user.getUUsername()+ "'!");
                } else {
                    observer.notifyObserver("Couldn´t insert message!");
                    Main.Log("[RMIClient]", "Message couldn´t be inserted!");
                }
            }
        }
    }

    @Override
    public void addObserver(RemoteObserverRMI observer, TUser user) throws RemoteException {
        synchronized (SV_CFG.RMIClients) {
            TUser dbUser = SV_CFG.DB.getUserByUsername(user.getUUsername());
            if (dbUser != null && dbUser.getUPassword().equals(user.getUPassword())) {
                if (SV_CFG.ClientListContains(new GenericPair<>(dbUser, null))) {
                    observer.notifyAuthentication(null);
                    observer.notifyObserver("Client is already logged in!");
                } else {
                    dbUser.setPassword();
                    SV_CFG.broadcastToRMI("User '" + dbUser.getUUsername() + "' has logged in!");
                    SV_CFG.RMIClients.put(observer, dbUser);
                    observer.notifyAuthentication(dbUser);
                    Main.Log("[RMIClient]", "User '" + user.getUUsername() + "' has logged in!");
                }
            } else {
                observer.notifyAuthentication(null);
                observer.notifyObserver("Username or Password incorrect!");
                Main.Log("[RMIClient]", "User '" + user.getUUsername() + "' may not exist or may be using the wrong password!");
            }
        }
    }

    @Override
    public void removeObserver(RemoteObserverRMI observer) throws RemoteException {
        synchronized (SV_CFG.RMIClients) {
            TUser user = SV_CFG.RMIClients.remove(observer);
            SV_CFG.broadcastToRMI("User '" + user.getUUsername() + "' has logged out!");
            Main.Log("[RMIClient]", "User '" + user.getUUsername() + "' has logged out!");
        }
    }

    public ServerRMI(ServerConfig SV_CFG) throws RemoteException {
        this.SV_CFG = SV_CFG;
    }
}
