package pt.isec.deis.lei.pd.trabprat.server.springboot.interfaces;

import java.util.Map;
import pt.isec.deis.lei.pd.trabprat.server.springboot.model.User;

public interface ITokenService {

    Map<User, String> getAll();

    void setTokens(Map<User, String> tokens);
}
