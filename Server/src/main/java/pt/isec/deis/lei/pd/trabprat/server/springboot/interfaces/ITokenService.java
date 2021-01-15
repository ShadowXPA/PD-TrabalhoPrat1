package pt.isec.deis.lei.pd.trabprat.server.springboot.interfaces;

import java.util.HashMap;
import pt.isec.deis.lei.pd.trabprat.server.springboot.model.User;

public interface ITokenService {

    HashMap<User, String> getAll();

    void setTokens(HashMap<User, String> tokens);
}
