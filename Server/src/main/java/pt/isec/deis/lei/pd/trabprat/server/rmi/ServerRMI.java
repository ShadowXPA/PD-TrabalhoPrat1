package pt.isec.deis.lei.pd.trabprat.server.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import pt.isec.deis.lei.pd.trabprat.model.GenericPair;
import pt.isec.deis.lei.pd.trabprat.model.TMessage;
import pt.isec.deis.lei.pd.trabprat.model.TUser;
import pt.isec.deis.lei.pd.trabprat.rmi.RemoteObserverRMI;
import pt.isec.deis.lei.pd.trabprat.rmi.RemoteServerRMI;
import pt.isec.deis.lei.pd.trabprat.server.Main;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;

public class ServerRMI extends UnicastRemoteObject implements RemoteServerRMI {

    private final ServerConfig SV_CFG;

    @Override
    public void registerUser(TUser user) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendMessage(TMessage message) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
                    SV_CFG.RMIClients.put(observer, dbUser);
                    observer.notifyAuthentication(dbUser);
                    Main.Log("[RMIClient]", "User " + user.getUUsername() + " has logged in!");
                }
            } else {
                observer.notifyAuthentication(null);
                observer.notifyObserver("Username or Password incorrect!");
                Main.Log("[RMIClient]", "User " + user.getUUsername() + " may not exist or may be using the wrong password!");
            }
        }
    }

    @Override
    public void removeObserver(RemoteObserverRMI observer) throws RemoteException {
        synchronized (SV_CFG.RMIClients) {
            TUser user = SV_CFG.RMIClients.remove(observer);
            Main.Log("[RMIClient]", "User " + user.getUUsername() + " has logged out!");
        }
    }

    public ServerRMI(ServerConfig SV_CFG) throws RemoteException {
        this.SV_CFG = SV_CFG;
    }
}
