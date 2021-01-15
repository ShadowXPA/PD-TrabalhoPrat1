package pt.isec.deis.lei.pd.trabprat.server.springboot.model;

import org.springframework.stereotype.Component;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;
import pt.isec.deis.lei.pd.trabprat.server.springboot.interfaces.IServerService;

@Component
public class ServerService implements IServerService {

    private ServerConfig SV_CFG;

    @Override
    public ServerConfig getServer() {
        return SV_CFG;
    }

    @Override
    public void setServerConfig(ServerConfig SV_CFG) {
        this.SV_CFG = SV_CFG;
    }

    public ServerService() {
    }
}
