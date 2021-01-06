package pt.isec.deis.lei.pd.trabprat.server.springboot;

import java.util.HashMap;
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

    private final HashMap<User, String> tokens;
//    private final AuthorizationFilter authFilter;
    private ServerConfig SV_CFG;

    @Override
    public void run() {
        ConfigurableApplicationContext context = SpringApplication.run(MainRestAPI.class, "--server.port=8080");
        TokenService tokenBean = context.getBean(TokenService.class);
        ServerService svBean = context.getBean(ServerService.class);
        tokenBean.setTokens(tokens);
        synchronized (this) {
            try {
                while (SV_CFG == null) {
                    this.wait(1000);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        svBean.setServerConfig(SV_CFG);
//        authFilter.setTokens(tokenBean);
    }

    public MainRestAPI() {
        this.tokens = new HashMap<>();
//        this.authFilter = new AuthorizationFilter();
        this.SV_CFG = null;
    }

    public void setServerConfig(ServerConfig SV_CFG) {
        synchronized (this) {
            this.SV_CFG = SV_CFG;
            this.notifyAll();
        }
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
