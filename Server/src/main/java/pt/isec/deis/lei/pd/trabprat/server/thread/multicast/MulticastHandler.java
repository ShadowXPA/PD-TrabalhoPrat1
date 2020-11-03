package pt.isec.deis.lei.pd.trabprat.server.thread.multicast;

import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;

public class MulticastHandler implements Runnable {

    private final ServerConfig SV_CFG;

    public MulticastHandler(ServerConfig SV_CFG) {
        this.SV_CFG = SV_CFG;
    }

    @Override
    public void run() {

    }
}
