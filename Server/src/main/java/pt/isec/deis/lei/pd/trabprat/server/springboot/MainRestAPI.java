package pt.isec.deis.lei.pd.trabprat.server.springboot;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pt.isec.deis.lei.pd.trabprat.server.config.ServerConfig;
import pt.isec.deis.lei.pd.trabprat.server.springboot.filter.AuthorizationFilter;
import pt.isec.deis.lei.pd.trabprat.server.springboot.model.ServerService;
import pt.isec.deis.lei.pd.trabprat.server.springboot.model.TokenService;
import pt.isec.deis.lei.pd.trabprat.server.springboot.model.User;

@ComponentScan(basePackages = {"pt.isec.deis.lei.pd.trabprat.server.springboot"})
@SpringBootApplication
public class MainRestAPI implements Runnable {

    public static final Object LockTokens = new Object();
    public static final HashMap<User, String> tokens = new HashMap<>();
    public static ServerConfig SV_CFG;
//    private final AuthorizationFilter authFilter;

    public static Entry<User, String> getToken(String token) {
        synchronized (LockTokens) {
//        System.out.println("TOKENS: " + this.tokens/*.getAll()*/ + " SIZE:" + this.tokens/*.getAll()*/.size());
            for (var entry : tokens/*.getAll()*/.entrySet()) {
                if (entry.getKey().getToken().equals(token)) {
                    return entry;
                }
            }
            return null;
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            try {
                while (SV_CFG == null) {
                    this.wait(1000);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        ConfigurableApplicationContext context = SpringApplication.run(MainRestAPI.class, "--server.port=" + SV_CFG.SpringBootPort);
//        TokenService tokenBean = context.getBean(TokenService.class);
//        ServerService svBean = context.getBean(ServerService.class);
//        tokenBean.setTokens(tokens);
//        svBean.setServerConfig(SV_CFG);
//        authFilter.setTokens(tokenBean);
        Thread timeOutTokens = new Thread(() -> {
            ArrayList<User> toRemove = new ArrayList<>();
            while (true) {
                try {
                    synchronized (LockTokens) {
                        tokens.entrySet().forEach(set -> {
                            User user = set.getKey();
                            String secret = set.getValue();
                            Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret),
                                    SignatureAlgorithm.HS256.getJcaName());

                            try {
                                Jws<Claims> jwt = Jwts.parserBuilder()
                                        .setSigningKey(hmacKey)
                                        .build()
                                        .parseClaimsJws(user.getToken());
                            } catch (ExpiredJwtException ex) {
                                toRemove.add(user);
                                System.out.println("Token '" + user.getToken() + "' has expired!");
                            }
                        });
                        toRemove.forEach(r -> tokens.remove(r));
                    }
                    toRemove.clear();
                    Thread.sleep(120000);
                } catch (InterruptedException ex) {
                }
            }
        }, "TokenExpiredThread");
        timeOutTokens.setDaemon(true);
        timeOutTokens.start();
        System.out.println("RestAPI Running...");
    }

    public void setServerConfig(ServerConfig sv) {
        synchronized (this) {
            SV_CFG = sv;
            this.notifyAll();
        }
    }

    public MainRestAPI() {
//        this.authFilter = new AuthorizationFilter();
    }

    @EnableWebSecurity
    @Configuration
    class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .addFilterAfter(new AuthorizationFilter(),
                            UsernamePasswordAuthenticationFilter.class)
                    .authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/user/login").permitAll()
                    .antMatchers(HttpMethod.POST, "/user/login").permitAll()
                    .anyRequest().authenticated().and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and().exceptionHandling().authenticationEntryPoint(
                            new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        }
    }
}
