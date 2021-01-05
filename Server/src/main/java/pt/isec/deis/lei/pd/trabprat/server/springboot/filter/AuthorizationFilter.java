package pt.isec.deis.lei.pd.trabprat.server.springboot.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import pt.isec.deis.lei.pd.trabprat.server.springboot.interfaces.ITokenService;

public class AuthorizationFilter extends OncePerRequestFilter {

    private ITokenService tokens;

    @Override
    protected void doFilterInternal(HttpServletRequest hsr, HttpServletResponse hsr1, FilterChain fc) throws ServletException, IOException {
        String token = hsr.getHeader("Authorization");

        if (token != null /*&& something else*/) {

        }

        fc.doFilter(hsr, hsr1);
    }

    public void setTokens(ITokenService tokens) {
        this.tokens = tokens;
    }

    public AuthorizationFilter() {}
}
