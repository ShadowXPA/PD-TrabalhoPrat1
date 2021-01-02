package pt.isec.deis.lei.pd.trabprat.observer;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.observer.rmi.ObserverObject;
import pt.isec.deis.lei.pd.trabprat.rmi.RemoteServerRMI;

public class Main {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("You need to provide a host and a port."
                    + "\nPlease use 'java -jar Observer.jar"
                    + " [ServerHost] [ServerID]'");
            System.exit(-1);
        }
        RemoteServerRMI server = null;
        ObserverObject obs = null;
        try {
            obs = new ObserverObject(System.out);
            String host = args[0];
            String serverID = args[1];
            Registry reg = LocateRegistry.getRegistry(host);
            server = (RemoteServerRMI) reg.lookup(serverID + "_"
                    + RemoteServerRMI.SERVICE_NAME);
            server.addObserver(obs);
            // Fazer cenas aqui

        } catch (NotBoundException ex) {
            System.out.println("Server is not running a service...");
        } catch (AccessException ex) {
            ExceptionHandler.ShowException(ex);
        } catch (RemoteException ex) {
            ExceptionHandler.ShowException(ex);
        } finally {
            if (server != null) {
                try {
                    server.removeObserver(obs);
                } catch (RemoteException ex) {
                    System.out.println("Whoops....");
                }
            }
        }
    }
}
