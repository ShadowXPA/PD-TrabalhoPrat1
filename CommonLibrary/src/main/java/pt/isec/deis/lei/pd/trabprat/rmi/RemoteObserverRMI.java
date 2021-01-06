package pt.isec.deis.lei.pd.trabprat.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import pt.isec.deis.lei.pd.trabprat.model.TUser;

public interface RemoteObserverRMI extends Remote {

    void notifyObserver(String message) throws RemoteException;

    void notifyAuthentication(TUser user) throws RemoteException;
}
