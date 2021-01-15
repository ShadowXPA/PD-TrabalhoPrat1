package pt.isec.deis.lei.pd.trabprat.server.springboot.interfaces;

import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;

public interface IServerService {

    ServerConfig getServer();

    void setServerConfig(ServerConfig SV_CFG);
}
