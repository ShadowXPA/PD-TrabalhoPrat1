package pt.isec.deis.lei.pd.trabprat.observer;

import pt.isec.deis.lei.pd.trabprat.observer.rmi.ObserverObject;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("You need to provide a host and a port."
                    + "\nPlease use 'java -jar Observer.jar"
                    + " [ServerHost]'");
            System.exit(-1);
        }

        try {
            ObserverObject obs = new ObserverObject(args[0], System.in, System.out);
            obs.initialize();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
