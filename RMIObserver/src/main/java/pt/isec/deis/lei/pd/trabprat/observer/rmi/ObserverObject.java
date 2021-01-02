package pt.isec.deis.lei.pd.trabprat.observer.rmi;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.rmi.RemoteObserverRMI;

public class ObserverObject extends UnicastRemoteObject implements RemoteObserverRMI {

    private final BufferedWriter out;

    @Override
    public void notifyObserver(String message) throws RemoteException {
        try {
            out.write(message);
            out.newLine();
            out.flush();
        } catch (IOException ex) {
            ExceptionHandler.ShowException(ex);
        }
    }

    public ObserverObject(OutputStream stream) {
        this.out = new BufferedWriter(new OutputStreamWriter(stream));
    }
}
