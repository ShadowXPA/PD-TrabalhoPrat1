package pt.isec.deis.lei.pd.trabprat.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import pt.isec.deis.lei.pd.trabprat.model.TMessage;
import pt.isec.deis.lei.pd.trabprat.model.TUser;

public interface RemoteServerRMI extends Remote {

    String SERVICE_NAME = "ServerRMI";

    void registerUser(RemoteObserverRMI observer, TUser user) throws RemoteException;

    void sendMessage(RemoteObserverRMI observer, TMessage message) throws RemoteException;

    void addObserver(RemoteObserverRMI observer, TUser user) throws RemoteException;

    void removeObserver(RemoteObserverRMI observer) throws RemoteException;
}
