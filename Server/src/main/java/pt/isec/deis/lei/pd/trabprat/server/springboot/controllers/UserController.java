package pt.isec.deis.lei.pd.trabprat.server.springboot.controllers;

import java.util.HashMap;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isec.deis.lei.pd.trabprat.server.springboot.interfaces.ITokenService;
import pt.isec.deis.lei.pd.trabprat.server.springboot.model.User;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private ITokenService tokens;

    @GetMapping("hello")
    public String hello() {
        return "Hello world!";
    }

    @PostMapping("login")
    public User login(@RequestBody User user) {
        if (user == null) {
            System.out.println("[RestAPI] Body is null!");
            return null;
        }
        // TODO: Fix User class.equals
        HashMap<User, String> tokenList = (HashMap<User, String>) tokens.getAll();
        // Check if user already logged in
        if (tokenList.containsKey(user)) {
            System.out.println("[RestAPI] Already contains user!");
            return null;
        }
        // Create a token
        String token = "Haihsebiwad";
        String secret = UUID.randomUUID().toString();
        // Generate token
        user.setPassword("");
        user.setToken(token);
        tokenList.put(user, secret);
        // Send to user
        return user;
    }
}
