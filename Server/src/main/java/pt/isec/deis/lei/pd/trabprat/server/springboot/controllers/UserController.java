package pt.isec.deis.lei.pd.trabprat.server.springboot.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isec.deis.lei.pd.trabprat.model.TUser;

@RestController
@RequestMapping("user")
public class UserController {

    @GetMapping("hello")
    public String hello() {
        return "Hello world!";
    }

    @PostMapping("login")
    public TUser login(TUser user) {
        // TODO:
        // Check if user already logged in
        // Create a token
        // Send to user
        return null;
    }
}
