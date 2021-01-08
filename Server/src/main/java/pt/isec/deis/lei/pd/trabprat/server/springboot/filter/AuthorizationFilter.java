package pt.isec.deis.lei.pd.trabprat.server.springboot.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.annotation.PostConstruct;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import pt.isec.deis.lei.pd.trabprat.server.springboot.MainRestAPI;
import pt.isec.deis.lei.pd.trabprat.server.springboot.interfaces.ITokenService;
import pt.isec.deis.lei.pd.trabprat.server.springboot.model.TokenService;
import pt.isec.deis.lei.pd.trabprat.server.springboot.model.User;

@Service
public class AuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private ApplicationContext appContext;
//    private ITokenService tokens;
    private HashMap<User, String> tokens;

    @Override
    protected void doFilterInternal(HttpServletRequest hsr, HttpServletResponse hsr1, FilterChain fc) throws ServletException, IOException {
        String token = hsr.getHeader("Authorization");
        if (token != null) {
            Entry<User, String> token1 = MainRestAPI.getToken(token);
            if (token1 != null) {
                User user = token1.getKey();
                String secret = token1.getValue();
                Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret),
                        SignatureAlgorithm.HS256.getJcaName());

                try {
                    Jws<Claims> jwt = Jwts.parserBuilder()
                            .setSigningKey(hmacKey)
                            .build()
                            .parseClaimsJws(user.getToken());
                    System.out.println("User authenticated: " + jwt.toString());
                    UsernamePasswordAuthenticationToken uPAT = new UsernamePasswordAuthenticationToken("User", null, null);
                    SecurityContextHolder.getContext().setAuthentication(uPAT);
                } catch (ExpiredJwtException ex) {
                    this.tokens.remove(user);
                    System.out.println("Token '" + user.getToken() + "' has expired!");
                }
            }
        }
        fc.doFilter(hsr, hsr1);
    }

//    public void setTokens(ITokenService tokens) {
//        this.tokens = tokens;
//    }
//    @PostConstruct
//    private void postConstruct() {
//        this.tokens = appContext.getBean(TokenService.class);
//        if (this.tokens == null) {
//            System.out.println("WHYYYYYYY!!!!");
//        } else {
//            System.out.println("TOKENS: " + this.tokens.getAll() + " SIZE:" + this.tokens.getAll().size());
//        }
//    }
    public AuthorizationFilter() {
        this.tokens = MainRestAPI.tokens;
    }
//
//    public AuthorizationFilter(HashMap<User, String> tokens) {
//        System.out.println("Hash constructor");
//        this.tokens = tokens;
//    }
}
