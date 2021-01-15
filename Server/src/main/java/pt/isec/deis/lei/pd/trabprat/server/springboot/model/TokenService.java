package pt.isec.deis.lei.pd.trabprat.server.springboot.model;

import java.util.HashMap;
import org.springframework.stereotype.Component;
import pt.isec.deis.lei.pd.trabprat.server.springboot.interfaces.ITokenService;

@Component
public class TokenService implements ITokenService {

    private HashMap<User, String> tokens;

    @Override
    public HashMap<User, String> getAll() {
        return tokens;
    }

    @Override
    public void setTokens(HashMap<User, String> tokens) {
        this.tokens = tokens;
    }

    public TokenService() {
    }
}
