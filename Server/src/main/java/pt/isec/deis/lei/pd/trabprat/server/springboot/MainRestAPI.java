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
import pt.isec.deis.lei.pd.trabprat.server.springboot.filter.AuthorizationFilter;
import pt.isec.deis.lei.pd.trabprat.server.springboot.model.TokenService;
import pt.isec.deis.lei.pd.trabprat.server.springboot.model.User;

@ComponentScan(basePackages = {"pt.isec.deis.lei.pd.trabprat.server.springboot"})
@SpringBootApplication
public class MainRestAPI implements Runnable {

    public final HashMap<User, String> tokens;
    private final AuthorizationFilter authFilter;

    @Override
    public void run() {
        ConfigurableApplicationContext context = SpringApplication.run(MainRestAPI.class, "--server.port=8080");
        TokenService bean = context.getBean(TokenService.class);
        bean.setTokens(tokens);
        authFilter.setTokens(bean);
    }

    public MainRestAPI() {
        tokens = new HashMap<>();
        authFilter = new AuthorizationFilter();
    }

    @EnableWebSecurity
    @Configuration
    class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .addFilterAfter(authFilter,
                            UsernamePasswordAuthenticationFilter.class)
                    .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/user/login").permitAll()
                    .anyRequest().authenticated().and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and().exceptionHandling().authenticationEntryPoint(
                            new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        }

    }
}
