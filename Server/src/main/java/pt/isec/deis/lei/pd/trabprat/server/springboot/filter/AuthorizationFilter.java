package pt.isec.deis.lei.pd.trabprat.server.springboot.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pt.isec.deis.lei.pd.trabprat.server.springboot.interfaces.ITokenService;
import pt.isec.deis.lei.pd.trabprat.server.springboot.model.User;

public class AuthorizationFilter extends OncePerRequestFilter {

    private ITokenService tokens;

    @Override
    protected void doFilterInternal(HttpServletRequest hsr, HttpServletResponse hsr1, FilterChain fc) throws ServletException, IOException {
        String token = hsr.getHeader("Authorization");
        HashMap<User, String> tokenList = tokens.getAll();
        if (token != null) {
            Entry<User, String> token1 = getToken(token);
            if (token1 != null) {
                User user = token1.getKey();
                String secret = token1.getValue();
                Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret),
                        SignatureAlgorithm.HS256.getJcaName());

                Jws<Claims> jwt = Jwts.parserBuilder()
                        .setSigningKey(hmacKey)
                        .build()
                        .parseClaimsJws(user.getToken());
                System.out.println("Passei aqui!!!!!");
                UsernamePasswordAuthenticationToken uPAT = new UsernamePasswordAuthenticationToken("User", null, null);
                SecurityContextHolder.getContext().setAuthentication(uPAT);
            }
        }
        fc.doFilter(hsr, hsr1);
    }

    private Entry<User, String> getToken(String token) {
        for (var entry : tokens.getAll().entrySet()) {
            if (entry.getKey().getToken().equals(token)) {
                return entry;
            }
        }
        return null;
    }

    public void setTokens(ITokenService tokens) {
        this.tokens = tokens;
    }

    public AuthorizationFilter() {
    }
}
