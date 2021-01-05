package pt.isec.deis.lei.pd.trabprat.server.springboot.model;

import java.util.Map;
import org.springframework.stereotype.Component;
import pt.isec.deis.lei.pd.trabprat.server.springboot.interfaces.ITokenService;

@Component
public class TokenService implements ITokenService {

    private Map<User, String> tokens;

    public Map<User, String> getAll() {
        return tokens;
    }

    public void setTokens(Map<User, String> tokens) {
        this.tokens = tokens;
    }

    public TokenService() {
        System.out.println("Object...");
    }
}
