package pt.isec.deis.lei.pd.trabprat.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteObserverRMI extends Remote {
    void notifyObserver(String message) throws RemoteException;
}
