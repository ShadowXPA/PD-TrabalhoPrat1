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

    @GetMapping("login")
    public String login() {
        return "<!DOCTYPE html><html><meta charset=\"UTF-8\"/><head><style>#Page{background-color: #004C91;padding: 10px 10px 30px 10px;width: 22%;display: block;border-radius: 5px;margin: auto;}h1{color: white;text-align: center;}#MyForm{border-radius: 5px;background-color: #FFF;padding: 20px;margin: 2em auto 0 auto;width: 75%;border: 1px solid #AAA;}[type=button]{width: 30%;background-color: #2D9216;color: #FFF;padding: 14px 20px;border: none;text-transform: uppercase;font-weight: bold;transition: 0.5s;border-radius: 4px;cursor: pointer;margin: 10px 0 0 70%;}[type=button]:hover{background-color: #7B1691;}[type=text], [type=password]{width: 100%;background-color: #f0f0f0;padding: 12px 20px;margin: 8px 0;display: inline-block;border: 1px solid #CCC;border-radius: 4px;box-sizing: border-box;}</style></head><body><div id=\"Page\"><h1>Sign in to your account</h1> <div id=\"MyForm\"><label for=\"username\">Username</label> <input type=\"text\" placeholder=\"Enter your username\" id=\"user\" name=\"username\"><label for=\"password\">Password</label> <input type=\"password\" placeholder=\"Enter your password\" id=\"pass\" name=\"password\"> <input type=\"button\" value=\"Login\" onclick=\"submit()\"> </div></div><script>function submit(){let xhr=new XMLHttpRequest(); let form=document.getElementById('MyForm'); let username=form.children.user.value; let password=form.children.pass.value; xhr.open('POST', '/user/login', true); xhr.setRequestHeader('Content-Type', 'application/json'); let j={\"username\":username, \"password\":password,};xhr.send(JSON.stringify(j));}</script></body></html>";
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
