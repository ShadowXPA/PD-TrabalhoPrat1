package pt.isec.deis.lei.pd.trabprat.server.springboot.filter;

import java.io.IOException;
import java.util.HashMap;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import pt.isec.deis.lei.pd.trabprat.model.TUser;

public class AuthorizationFilter extends OncePerRequestFilter {

    private final HashMap<TUser, String> tokens;

    @Override
    protected void doFilterInternal(HttpServletRequest hsr, HttpServletResponse hsr1, FilterChain fc) throws ServletException, IOException {
        String token = hsr.getHeader("Authorization");

        if (token != null /*&& something else*/) {

        }

        fc.doFilter(hsr, hsr1);
    }

    public AuthorizationFilter(HashMap<TUser, String> tokens) {
        this.tokens = tokens;
    }
}
